package brute.force;

import static java.lang.Integer.toHexString;

import brute.force.ByteStorage.SDOUploadStorage;
import brute.force.cleaner.SimpleHtmlCleaner;
import brute.force.store.FrameEntry;
import brute.force.store.MemoryFrameStore;
import brute.force.support.FetchTimeRegistry;
import brute.force.support.FetchTimeRegistry.Timing;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.plc4x.java.canopen.readwrite.CANOpenPayload;
import org.apache.plc4x.java.canopen.readwrite.CANOpenSDORequest;
import org.apache.plc4x.java.canopen.readwrite.CANOpenSDOResponse;
import org.apache.plc4x.java.canopen.readwrite.SDOInitiateUploadRequest;
import org.apache.plc4x.java.canopen.readwrite.SDOResponse;
import org.apache.plc4x.java.canopen.readwrite.SDOSegmentUploadResponse;
import org.apache.plc4x.java.canopen.readwrite.io.CANOpenPayloadIO;
import org.apache.plc4x.java.canopen.readwrite.types.CANOpenService;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;

public class CmiCrawler extends WebCrawler {

  private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
      + "|png|mp3|mp4|zip|gz))$");
  public static final CharsetDecoder ASCII_DECODER = StandardCharsets.US_ASCII.newDecoder()
    .onMalformedInput(CodingErrorAction.IGNORE)
    .onUnmappableCharacter(CodingErrorAction.REPLACE)
    .replaceWith("_");
  public static final CharsetDecoder UTF_DECODER = StandardCharsets.UTF_16LE.newDecoder()
    .onMalformedInput(CodingErrorAction.IGNORE)
    .onUnmappableCharacter(CodingErrorAction.REPLACE)
    .replaceWith("_");

  private final FetchTimeRegistry fetchTimeRegistry;
  private final SimpleHtmlCleaner cleaner;
  private final MemoryFrameStore frameStore;
  private final String storePath;
  private final String basePath;

  public CmiCrawler(FetchTimeRegistry fetchTimeRegistry, SimpleHtmlCleaner cleaner, MemoryFrameStore frameStore, String storePath, String basePath) {
    this.fetchTimeRegistry = fetchTimeRegistry;
    this.cleaner = cleaner;
    this.frameStore = frameStore;
    this.storePath = storePath;
    this.basePath = basePath;
  }

  /**
   * This method receives two parameters. The first parameter is the page
   * in which we have discovered this new url and the second parameter is
   * the new url. You should implement this function to specify whether
   * the given url should be crawled or not (based on your crawling logic).
   * In this example, we are instructing the crawler to ignore urls that
   * have css, js, git, ... extensions and to only accept urls that start
   * with "https://www.ics.uci.edu/". In this case, we didn't need the
   * referringPage parameter to make the decision.
   */
  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    return !FILTERS.matcher(href).matches()
        && href.startsWith(basePath) /*&& href.contains("devpagex.cgi")*/;
  }

  /**
   * This function is called when a page is fetched and ready
   * to be processed by your program.
   */
  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();
    System.out.println("URL: " + url);

    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();

      String frameDump = generateSDODump();
      // clean up past records
      frameStore.clear();

      String register = "root";
      if (url.contains("=")) {
        register = url.substring(url.lastIndexOf("=") + 1);
      }

      String outgoing = page.getParseData().getOutgoingUrls().stream()
          .map(link -> link.getURL() + " " + getLinkText(link, html))
          .reduce("", (left, right) -> left + "\n" + right);

      File data = new File(storePath + "/" + register + ".html");
      Timing timing = fetchTimeRegistry.get(page.getWebURL());
      if (!data.exists()) {
        try {
          Files.write(data.toPath(), Arrays.asList(
            frameDump,
            "parent: " + page.getWebURL().getParentUrl(),
            "outgoing: " + outgoing,
            text, tidy(html)
          ));
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.err.println("Already indexed " + url);
      }

//      if (timing == null) {
//        System.err.println("Unknown timing!");
//      } else {
//        try {
//          File meta = new File(storePath + "/" + register + "-metadata.xml");
//          Files.write(meta.toPath(), Arrays.asList(
//            "<?xml version=\"1.0\"?>\n<metadata>",
//              "<parent>" + page.getWebURL().getParentUrl() + "</parent>",
//              "<outgoing>" + links.size() + "</outgoing>",
//              "<start>" + Optional.ofNullable(timing).map(Timing::getStart).map(Objects::toString).orElse("") + "</start>",
//              "<end>" + Optional.ofNullable(timing).map(Timing::getEnd).map(Objects::toString).orElse("") + "</end>",
//              "<start-date>" + Optional.ofNullable(timing).map(Timing::getStart).map(Date::new).map(Objects::toString).orElse("") + "</start-date>",
//              "<end-date>" + Optional.ofNullable(timing).map(Timing::getEnd).map(Date::new).map(Objects::toString).orElse("") + "</end-date>",
//              "</metadata>"
//          ));
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
    }
  }

  // ok this logic is faulty cause it navigates over unstructured document..
  private String getLinkText(WebURL link, String html) {
//    String anchor = link.getAnchor();
//
//    int startLocation = html.indexOf(anchor);
    return ""; //html.substring(startLocation, html.indexOf("</div>", startLocation));
  }

  private String tidy(String html) {
    return cleaner.clean(html);
  }

  private String generateSDODump() {
    String canDump = "";
    String textDump = "";

    SDOUploadStorage storage = new SDOUploadStorage();
    for (FrameEntry fe : frameStore.getFrames()) {
      String payload = separated(Hex.encodeHexString(fe.frame.getData()));
      String header = fe.time + " (" + new Date(fe.time) + ")";
      String address = toHexString(fe.frame.getIdentifier());
      CANOpenService function = serviceId(fe.frame.getIdentifier());

      if (function != CANOpenService.TRANSMIT_SDO && function != CANOpenService.RECEIVE_SDO) {
        // skip - non sdo traffic as it is generally unrelated to what CMI does
        continue;
      }

      ReadBuffer buffer = new ReadBuffer(fe.frame.getData(), true);
      try {
        CANOpenPayload co = CANOpenPayloadIO.staticParse(buffer,
            function);
        if (co instanceof CANOpenSDORequest) {
          CANOpenSDORequest sdo = (CANOpenSDORequest) co;
          if (sdo.getRequest() instanceof SDOInitiateUploadRequest) {
            SDOInitiateUploadRequest req = (SDOInitiateUploadRequest) sdo.getRequest();
            canDump +=  "decimal " + req.getAddress().getIndex() + "/" + req.getAddress().getSubindex()  +
                ", hex " + toHexString(req.getAddress().getIndex()) + "/0x" + toHexString(req.getAddress().getSubindex()) +
                ", little endian " + toHexString(Integer.reverseBytes(req.getAddress().getIndex())) + "/0x" + toHexString(Integer.reverseBytes(req.getAddress().getSubindex())) + "\n";
          }
        }

        if (co instanceof CANOpenSDOResponse) {
          CANOpenSDOResponse sdo = (CANOpenSDOResponse) co;
          storage.append(sdo.getResponse());

          SDOResponse response = sdo.getResponse();
          if (response instanceof SDOSegmentUploadResponse) {
            if (((SDOSegmentUploadResponse) response).getLast()) {
              byte[] data = storage.get();
              canDump += Hex.encodeHexString(data) + "\n";
              canDump += "(utf) " + safeString(data, UTF_DECODER) + "\n\n";
              storage = new SDOUploadStorage();
            }
          }
        }
      } catch (ParseException e) {
        canDump += header + " " + address + " could not parse due to: " + e;
      }

      textDump += header + " " + address + " " + payload + "\n";
    }
    return canDump + "\n" + textDump;
  }

  private static String safeString(byte[] data, CharsetDecoder decoder) {
    try {
      return decoder.decode(ByteBuffer.wrap(data)).toString();
    } catch (Exception e) {
      return "Could not decode " + e;
    }
  }

  private CANOpenService serviceId(int nodeId) {
    return CANOpenService.valueOf((byte) (nodeId >> 7));
  }

  private String separated(String hexString) {
    return Arrays.stream(hexString.split("(?<=\\G.{2})"))
        .reduce("", (left, right) -> left + " " + right);
  }

  public static void main(String[] args) throws Exception {
    String hex = "1f1744004c0020006f00750074007000750074000000";

    byte[] data = Hex.decodeHex(hex.toCharArray());
    System.out.println(safeString(data, UTF_DECODER));
  }
}
