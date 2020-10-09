package brute.force;

import brute.force.cleaner.DefaultHtmlCleaner;
import brute.force.store.FrameStore;
import brute.force.store.MemoryFrameStore;
import brute.force.support.FetchTimeRegistry;
import brute.force.support.MapFetchTimeRegistry;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory;
import edu.uci.ics.crawler4j.crawler.authentication.BasicAuthInfo;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParser;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.DefaultCookieSpec;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.can.context.CANOpenDriverContext;
import org.apache.plc4x.java.can.listener.Callback;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;

public class Launcher {

  public static final File STORE_PATH = new File("store");

  public static void main(String[] args) throws Exception {
    String crawlStorageFolder = STORE_PATH.getAbsolutePath();

    if (args.length != 2) {
      System.out.println("Usage: java -jar ... [can-interface] [cmi-address]");
      System.out.println(" [can-interface] - name of can interface to attach for audit");
      System.out.println(" [cmi-address] - address of web endpoint for cmi, without trailing slash, ie: http://x.y.z.y");
      System.exit(0);
    }

    final String iface = args[0];
    final String address = args[1];

    File pages = new File(crawlStorageFolder + "/pages");
    if (pages.exists()) {
      pages.delete();
    }
    pages.mkdirs();

    DefaultHtmlCleaner cleaner = new DefaultHtmlCleaner();
    PlcDriverManager driverManager = new PlcDriverManager();

    MemoryFrameStore store = new MemoryFrameStore();
    CANOpenDriverContext.CALLBACK.addCallback(new Callback() {
      @Override
      public void receive(SocketCANFrame frame) {
        store.add(UUID.randomUUID(), System.currentTimeMillis(), iface, frame);
      }
    });

    PlcConnection connection = driverManager.getConnection("canopen:javacan://" + iface + "?nodeId=11");

      int numberOfCrawlers = 1;

    CrawlConfig config = new CrawlConfig();
    config.setCrawlStorageFolder(crawlStorageFolder);
    config.setUserAgentString("Yollo");
    //config.setMaxPagesToFetch(1);
    config.setAuthInfos(Collections.singletonList(new BasicAuthInfo("admin", "admin", address + "/menupagex.cgi")));

    final FetchTimeRegistry fetchTimeRegistry = new MapFetchTimeRegistry();

    // Instantiate the controller for this crawl.
    PageFetcher pageFetcher = new PageFetcher(config) {
      @Override
      public PageFetchResult fetchPage(WebURL webUrl) throws InterruptedException, IOException, PageBiggerThanMaxSizeException {
        Thread.sleep(2500);
        long start = System.currentTimeMillis();
        PageFetchResult fetchPage = super.fetchPage(webUrl);
        long end = System.currentTimeMillis();
        fetchTimeRegistry.store(webUrl, start, end);
        return fetchPage;
      }

      @Override
      protected HttpUriRequest newHttpUriRequest(String url) {
        HttpUriRequest request = super.newHttpUriRequest(url);
        DefaultCookieSpec cookieSpec = new DefaultCookieSpec();
        List<Header> cookies = cookieSpec.formatCookies(Arrays.asList(new BasicClientCookie("canremote1", "2A372DBBD")));
        cookies.forEach(request::addHeader);
        return request;
      }
    };
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

    HtmlParser htmlParser = new CmiTikaHtmlParser(config, address);

    Parser parser = new Parser(config, htmlParser);
    CrawlController controller = new CrawlController(config, pageFetcher,
        parser, robotstxtServer);

    controller.addSeed(address + "/menupagex.cgi");
    CrawlController.WebCrawlerFactory<CmiCrawler> factory = new WebCrawlerFactory<CmiCrawler>() {
      @Override
      public CmiCrawler newInstance() throws Exception {
        return new CmiCrawler(fetchTimeRegistry, cleaner, store, STORE_PATH.getAbsolutePath() + "/pages/", address);
      }
    };

    controller.start(factory, numberOfCrawlers);
  }
}
