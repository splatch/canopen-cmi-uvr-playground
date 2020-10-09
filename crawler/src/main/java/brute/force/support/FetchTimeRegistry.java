package brute.force.support;

import edu.uci.ics.crawler4j.url.WebURL;

public interface FetchTimeRegistry {

  Timing get(WebURL url);

  void store(WebURL url, long start, long end);

  class Timing {
    public final long start;
    public final long end;

    public Timing(long start, long end) {
      this.start = start;
      this.end = end;
    }

    public long getStart() {
      return start;
    }

    public long getEnd() {
      return end;
    }
  }
}
