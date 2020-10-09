package brute.force.scanner;

import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalOutputCallback extends PDOConsumer {

  private final Logger logger = LoggerFactory.getLogger(DigitalOutputCallback.class);

  @Override
  public void accept(PlcSubscriptionEvent plcSubscriptionEvent) {
    byte[] data = getBytes(plcSubscriptionEvent, plcSubscriptionEvent.getFieldNames().iterator().next());
    ReadBuffer buffer = new ReadBuffer(data, true);
    try {
      for (int index = 0; index < 32; index++) {
        logger.info("Digital Output {}={}", index, buffer.readBit());
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
