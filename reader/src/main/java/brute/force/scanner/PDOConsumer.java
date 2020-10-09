package brute.force.scanner;

import java.util.List;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.value.PlcList;
import org.apache.plc4x.java.api.value.PlcValue;

public abstract class PDOConsumer implements Consumer<PlcSubscriptionEvent> {

  protected final byte[] getBytes(PlcSubscriptionEvent event, String field) {
    PlcValue value = event.getPlcValue(field);

    if (value instanceof PlcList) {
      PlcList list = (PlcList) value;
      byte[] data = new byte[list.getLength()];
      List<? extends PlcValue> values = list.getList();
      for (int index = 0, listSize = values.size(); index < listSize; index++) {
        PlcValue plcValue = values.get(index);
        data[index] = plcValue.getByte();
      }
      return data;
    }

    return new byte[] {value.getByte() };
  }

}
