package brute.force.support;

import edu.uci.ics.crawler4j.url.WebURL;
import java.util.HashMap;
import java.util.Map;

public class MapFetchTimeRegistry implements FetchTimeRegistry {

  private final Map<WebURL, Timing> timingMap = new HashMap<>();

  @Override
  public Timing get(WebURL url) {
    return timingMap.get(url);
  }

  @Override
  public void store(WebURL url, long start, long end) {
    timingMap.put(url, new Timing(start, end));
  }
}
