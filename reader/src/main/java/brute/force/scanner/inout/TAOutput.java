package brute.force.scanner.inout;

import brute.force.scanner.unit.Unit;
import org.apache.plc4x.java.canopen.readwrite.IndexAddress;

public abstract class TAOutput {

  private final Unit unit;
  private final short number;

  private final String label;

  protected TAOutput(String label, Unit unit, short number) {
    this.label = label;
    this.unit = unit;
    this.number = number;

//    name = new IndexAddress(addressOffset + 0xf, (short) (number - 1));
//    sourceType = new IndexAddress(addressOffset + 0x2000 + 0x50, (short) (number - 1));
//    sourceObject = new IndexAddress(addressOffset + 0x2000 + 0x51, (short) (number - 1));
//    sourceVariable = new IndexAddress(addressOffset + 0x2000 + 0x52, (short) (number - 1));
  }

  public String getLabel() {
    return label;
  }

  public Unit getUnit() {
    return unit;
  }

  public String toString() {
    return getClass().getSimpleName() + "@" + number + " " + unit + "(" + label + ")";
  }

}
