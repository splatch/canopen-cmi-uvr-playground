package brute.force;

import edu.uci.ics.crawler4j.parser.AllTagMapper;
import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;

public class CmiTikaHtmlParser implements edu.uci.ics.crawler4j.parser.HtmlParser {
    protected static final Logger logger = LoggerFactory.getLogger(CmiTikaHtmlParser.class);

    private final CrawlConfig config;

    private final HtmlParser htmlParser;
    private final ParseContext parseContext;
    private final String base;

    public CmiTikaHtmlParser(CrawlConfig config, String base) throws InstantiationException, IllegalAccessException {
        this.config = config;
        this.base = base;

        htmlParser = new HtmlParser();
        parseContext = new ParseContext();
        parseContext.set(HtmlMapper.class, AllTagMapper.class.newInstance());
    }

    public HtmlParseData parse(Page page, String contextURL) throws ParseException {
        HtmlParseData parsedData = new HtmlParseData();

        CmiHtmlContentHandler contentHandler = new CmiHtmlContentHandler(base);
        Metadata metadata = new Metadata();

        try (InputStream inputStream = new ByteArrayInputStream(page.getContentData())) {
            htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
        } catch (Exception e) {
            logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
            throw new ParseException();
        }

        String contentCharset = chooseEncoding(page, metadata);
        parsedData.setContentCharset(contentCharset);

        parsedData.setText("");
        parsedData.setTitle(metadata.get(DublinCore.TITLE));
        parsedData.setMetaTags(new HashMap<>());

        Set<WebURL> outgoingUrls = getOutgoingUrls(contextURL, contentHandler, contentCharset);
        parsedData.setOutgoingUrls(outgoingUrls);

        try {
            if (page.getContentCharset() == null) {
                parsedData.setHtml(new String(page.getContentData()));
            } else {
                parsedData.setHtml(new String(page.getContentData(), page.getContentCharset()));
            }

            return parsedData;
        } catch (UnsupportedEncodingException e) {
            logger.error("error parsing the html: " + page.getWebURL().getURL(), e);
            throw new ParseException();
        }

    }

    private Set<WebURL> getOutgoingUrls(String contextURL, CmiHtmlContentHandler contentHandler, String contentCharset) {
        Set<WebURL> outgoingUrls = new HashSet<>();

        String baseURL = contentHandler.getBaseUrl();
        if (baseURL != null) {
            contextURL = baseURL;
        }

        int urlCount = 0;
        for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {

            String href = urlAnchorPair.getHref();
            if ((href == null) || href.trim().isEmpty()) {
                continue;
            }

            String hrefLoweredCase = href.trim().toLowerCase();
            if (!hrefLoweredCase.contains("javascript:") &&
                    !hrefLoweredCase.contains("mailto:") && !hrefLoweredCase.contains("@")) {
                // Prefer page's content charset to encode href url
                Charset hrefCharset = ((contentCharset == null) || contentCharset.isEmpty()) ?
                        StandardCharsets.UTF_8 : Charset.forName(contentCharset);
                String url = URLCanonicalizer.getCanonicalURL(href, contextURL, hrefCharset);
                if (url != null) {
                    WebURL webURL = new WebURL();
                    webURL.setURL(url);
                    webURL.setTag(urlAnchorPair.getTag());
                    webURL.setAnchor(urlAnchorPair.getAnchor());
                    webURL.setAttributes(urlAnchorPair.getAttributes());
                    outgoingUrls.add(webURL);
                    urlCount++;
                    if (urlCount > config.getMaxOutgoingLinksToFollow()) {
                        break;
                    }
                }
            }
        }
        return outgoingUrls;
    }

    private String chooseEncoding(Page page, Metadata metadata) {
        String pageCharset = page.getContentCharset();
        if (pageCharset == null || pageCharset.isEmpty()) {
            return metadata.get("Content-Encoding");
        }
        return pageCharset;
    }
}