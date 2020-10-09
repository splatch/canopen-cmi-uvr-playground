package brute.force.scanner.inout;

import brute.force.scanner.unit.Unit;
import org.apache.plc4x.java.canopen.readwrite.IndexAddress;

public abstract class TAOutput {

  private final int addressOffset;
  private final Unit unit;

  private final IndexAddress name;
  private /*final*/ IndexAddress sourceType;
  private /*final*/ IndexAddress sourceObject;
  private /*final*/ IndexAddress sourceVariable;

  protected TAOutput(int addressOffset, Unit unit, short number) {
    this.addressOffset = addressOffset;
    this.unit = unit;

    name = new IndexAddress(addressOffset + 0xf, (short) (number - 1));
//    sourceType = new IndexAddress(addressOffset + 0x2000 + 0x50, (short) (number - 1));
//    sourceObject = new IndexAddress(addressOffset + 0x2000 + 0x51, (short) (number - 1));
//    sourceVariable = new IndexAddress(addressOffset + 0x2000 + 0x52, (short) (number - 1));
  }

  public IndexAddress getName() {
    return name;
  }

  public IndexAddress getSourceType() {
    return sourceType;
  }

  public IndexAddress getSourceObject() {
    return sourceObject;
  }

  public IndexAddress getSourceVariable() {
    return sourceVariable;
  }

  public String toString() {
    return getClass().getSimpleName() + "@" + (name.getSubindex() + 1) + " " + unit;
  }

}
