package brute.force.proto;

import org.apache.plc4x.java.spi.generation.ReadBuffer;

public class Util {

  public static boolean isLastElement(ReadBuffer io) {
    return io.getTotalBytes() - io.getPos() == 4;
  }

}
