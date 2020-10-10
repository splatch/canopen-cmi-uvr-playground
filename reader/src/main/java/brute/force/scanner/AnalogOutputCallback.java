package brute.force.scanner;

import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalogOutputCallback extends PDOConsumer {

  private final Logger logger = LoggerFactory.getLogger(AnalogOutputCallback.class);
  private final ValueListener listener;
  private final int offset;

  public AnalogOutputCallback(ValueListener listener, int offset) {
    this.listener = listener;
    this.offset = offset;
  }

  @Override
  public void accept(PlcSubscriptionEvent plcSubscriptionEvent) {
    byte[] bytes = getBytes(plcSubscriptionEvent, plcSubscriptionEvent.getFieldNames().iterator().next());

    ReadBuffer buffer = new ReadBuffer(bytes, true);
    try {
      for (int index = 0; index < 4; index++) {
        // we could use here getBytes or just delegate reading to unit
        listener.analog(offset + index, buffer);
      }
    }catch (ParseException e) {
      e.printStackTrace();
    }
  }

}
