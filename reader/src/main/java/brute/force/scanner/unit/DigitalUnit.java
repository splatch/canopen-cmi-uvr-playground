package brute.force.scanner.unit;

import java.util.Arrays;
import java.util.List;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;

class DigitalUnit implements Unit {

  private final String name;
  private final List<String> states;

  public DigitalUnit(int index, String name, String description, String ... states) {
    super();
    this.name = name;
    this.states = Arrays.asList(states);
  }

  @Override
  public Object parse(ReadBuffer buffer) {
    try {
      int index = buffer.readUnsignedInt(16);
      return index + ":" + states.get(index);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    return "Digital IO " + name;
  }

}
