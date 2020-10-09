package brute.force.cleaner;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlSerializer;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;

public class DefaultHtmlCleaner implements SimpleHtmlCleaner {

  private final CleanerProperties props;
  private final HtmlCleaner htmlCleaner;
  private final HtmlSerializer htmlSerializer;

  public DefaultHtmlCleaner() {
    props = new CleanerProperties();
    htmlCleaner = new HtmlCleaner(props);
    htmlSerializer = new PrettyHtmlSerializer(props);
  }

  @Override
  public String clean(String input) {
    TagNode tagNode = htmlCleaner.clean(input);
    return htmlSerializer.getAsString(tagNode, "utf-8");
  }

}
