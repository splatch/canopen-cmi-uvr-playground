package brute.force.scanner;

import brute.force.scanner.inout.AnalogOutput;
import brute.force.scanner.inout.DigitalOutput;
import brute.force.scanner.inout.TAOutput;
import brute.force.scanner.unit.UVRUnits;
import brute.force.scanner.unit.Unit;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
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
  private final Map<OutputKey, TAOutput> outputs;

  public ConfigurationCallback(PlcConnection connection, Map<OutputKey, TAOutput> outputs) {
    this.connection = connection;
    this.outputs = outputs;
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
        final short index = (short) (subIndex - 1);
        final IndexAddress nameAddress = new IndexAddress(0x2280 + 0xf, index);
        Function<String, TAOutput> outputFunction = ((Function<String, TAOutput>) (name) -> new AnalogOutput(name, unit, index)).andThen(output ->
            outputs.put(OutputKey.analog(index), output)
        );
        scan(sender, outputFunction, nameAddress);
      } else if (subIndex <= 64) { // digital
        final short index = (short) (subIndex - 33);
        final IndexAddress nameAddress = new IndexAddress(0x2380 + 0xf, index);
        Function<String, TAOutput> outputFunction = ((Function<String, TAOutput>) (name) -> new DigitalOutput(name, unit, index)).andThen(output ->
            outputs.put(OutputKey.digital(index), output)
        );
        scan(sender, outputFunction, nameAddress);
      } else {
        logger.error("UVR reported unsupported output 0x{}/0x{}", Integer.toHexString(indexAddress.getIndex()), Integer.toHexString(subIndex));
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private void scan(short nodeId, Function<String, ?> output, IndexAddress index) {
    logger.info("{} scan", output);

    UVR16Printer.readBytes(connection, nodeId, index).whenComplete((type, e) -> {
      if (e != null) {
        e.printStackTrace();
        return;
      }
      final String label = new String(type, StandardCharsets.UTF_16LE);
      output.apply(label);
    });
  }

}
