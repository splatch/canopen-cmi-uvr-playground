package brute.force.scanner.unit;

import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;

class ScaledUnit implements Unit {

  private final int index;
  private final String name;
  private final double scale;
  private final boolean signed;

  public ScaledUnit(int index, String name, String dimensionslos, String symbol, double scale, boolean signed) {
    super();
    this.index = index;
    this.name = name;
    this.scale = scale;
    this.signed = signed;
  }

  @Override
  public Object parse(ReadBuffer buffer) {
    try {
      final short value = signed ? buffer.readShort(16) : buffer.readUnsignedShort(16);
      System.out.println("Read value " + value + " " + Integer.toHexString(value));
      return value * scale;
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String toString() {
    return name;
  }

}
