package brute.force.scanner.inout;

import brute.force.scanner.unit.Unit;

public class DigitalOutput extends TAOutput {

  public DigitalOutput(Unit unit, short number) {
    super(0x2380, unit, number);
  }

}
