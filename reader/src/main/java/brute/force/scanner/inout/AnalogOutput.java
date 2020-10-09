package brute.force.scanner.inout;

import brute.force.scanner.unit.Unit;

public class AnalogOutput extends TAOutput {

  public AnalogOutput(Unit unit, short number) {
    super(0x2280, unit, number);
  }

}
