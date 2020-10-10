package brute.force.scanner;

import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;

public interface ValueListener {

  void analog(int index, ReadBuffer value) throws ParseException;

  void digital(int index, boolean value);

}
