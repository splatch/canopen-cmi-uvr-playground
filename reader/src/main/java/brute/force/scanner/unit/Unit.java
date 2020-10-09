package brute.force.scanner.unit;

import org.apache.plc4x.java.spi.generation.ReadBuffer;

public interface Unit {

  Object parse(ReadBuffer buffer);

}
