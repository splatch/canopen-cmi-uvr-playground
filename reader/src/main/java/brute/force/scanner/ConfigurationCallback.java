package brute.force.scanner;

import brute.force.scanner.inout.AnalogOutput;
import brute.force.scanner.inout.DigitalOutput;
import brute.force.scanner.inout.TAOutput;
import brute.force.scanner.unit.UVRUnits;
import brute.force.scanner.unit.Unit;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.canopen.readwrite.IndexAddress;
import org.apache.plc4x.java.canopen.readwrite.io.IndexAddressIO;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationCallback extends PDOConsumer {

  private final Logger logger = LoggerFactory.getLogger(ConfigurationCallback.class);
  private final PlcConnection connection;

  public ConfigurationCallback(PlcConnection connection) {
    this.connection = connection;
  }

  @Override
  public void accept(PlcSubscriptionEvent plcSubscriptionEvent) {
    byte[] bytes = getBytes(plcSubscriptionEvent, plcSubscriptionEvent.getFieldNames().iterator().next());

    try {
      ReadBuffer buffer = new ReadBuffer(bytes, true);
      short sender = buffer.readUnsignedShort(8);
      IndexAddress indexAddress = IndexAddressIO.staticParse(buffer);
      byte[] raw = new byte[] {buffer.readByte(8), buffer.readByte(8)};
      buffer.readByte(8); // constant 0x41
      Unit unit = UVRUnits.get(buffer.readUnsignedShort(8));

      short subIndex = indexAddress.getSubindex();
      logger.info("Configuration from node {}, output {}, raw value {}, unit {}", sender,
          subIndex, Hex.encodeHexString(raw), unit);

      if (subIndex <= 32) { // analog
        scan(sender, new AnalogOutput(unit, subIndex));
      } else if (subIndex <= 64) { // digital
        scan(sender, new DigitalOutput(unit, (short) (subIndex - 32)));
      } else {
        logger.error("UVR reported unsupported output 0x{}/0x{}", Integer.toHexString(indexAddress.getIndex()), Integer.toHexString(subIndex));
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void scan(short nodeId, TAOutput output) {
    logger.info("{} scan", output);

//    Map<String, byte[]> values = new LinkedHashMap<>();
//
//    UVR16Printer.readBytes(connection, nodeId, output.getName()).handle((data, e) -> values.put("name", data))
//      .thenCombine(UVR16Printer.readBytes(connection, nodeId, output.getSourceType()), (f, s) -> values.put("source type", f))
//      .thenCombine(UVR16Printer.readBytes(connection, nodeId, output.getSourceObject()), (f, s) -> values.put("source object", s))
//      .thenCombine(UVR16Printer.readBytes(connection, nodeId, output.getSourceVariable()), (f, s) -> values.put("source variable", f))
//      .whenComplete((f, e) -> {
//        System.out.println(output + " " + values);
//      });

    UVR16Printer.readBytes(connection, nodeId, output.getName()).whenComplete((type, e) -> print(type, output + " name"));
//    UVR16Printer.readBytes(connection, nodeId, output.getSourceType()).whenComplete((type, e) -> print(type, output + " source type"));
//    UVR16Printer.readBytes(connection, nodeId, output.getSourceObject()).whenComplete((object, e) -> print(object, output + " source object"));
//    UVR16Printer.readBytes(connection, nodeId, output.getSourceVariable()).whenComplete((variable, e) -> print(variable, output + " source variable"));
  }

  private void print(byte[] type, String prefix) {
    System.out.println(prefix + " " + new String(type) + "(0x" + Hex.encodeHexString(type) + ")");
  }

}
