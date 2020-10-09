package brute.force.scanner;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest.Builder;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.value.PlcBYTE;
import org.apache.plc4x.java.api.value.PlcSINT;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.can.context.CANOpenDriverContext;
import org.apache.plc4x.java.can.listener.Callback;
import org.apache.plc4x.java.canopen.readwrite.IndexAddress;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;

public class UVR16Printer {

  private static Logger LOGGER = LoggerFactory.getLogger(UVR16Printer.class);

  //private Queue

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java -jar ... [can-interface] [uvr-address]");
      System.out.println(" [can-interface] - name of can interface to attach for audit");
      System.out.println(" [uvr-address] - CANopen node ID for UVR - 1..127");
      System.exit(0);
    }

    String iface = args[0];
    int nodeId = Integer.parseInt(args[1]);

//    MemoryFrameStore store = new MemoryFrameStore();
    CANOpenDriverContext.CALLBACK.addCallback(new Callback() {
      @Override
      public void receive(SocketCANFrame frame) {
        //System.out.println("-> " + Integer.toHexString(frame.getIdentifier()) + " " + Hex.encodeHexString(frame.getData()));
      }
    });

    PlcDriverManager driverManager = new PlcDriverManager();
    PlcConnection connection = driverManager
        .getConnection("canopen:javacan://" + iface + "?nodeId=11&heartbeat=true");

//    System.out.println("name " + readString(connection, nodeId, 0x2512, 0x00));
//    System.out.println("funk_daten " + readString(connection, nodeId, 0x57E0, 0x07));
//    System.out.println("version " + readString(connection, nodeId, 0x57E0, 0x00));
//    System.out.println("serial " + readString(connection, nodeId, 0x57E0, 0x01));
//    System.out.println("prodDate " + readString(connection, nodeId, 0x57E0, 0x02));
//    System.out.println("bootsector " + readString(connection, nodeId, 0x57E0, 0x03));
//    System.out.println("hardware_deckel " + readString(connection, nodeId, 0x57E0, 0x04));
//    System.out.println("hardware_netz " + readString(connection, nodeId, 0x57E0, 0x05));

//    Subscribed to 385/0x181 rx PDO Digital
//    Subscribed to 513/0x201 rx PDO Analog 0
//    Subscribed to 641/0x281 rx PDO Analog 1
//    Subscribed to 769/0x301 rx PDO Analog 2
//    Subscribed to 897/0x381 rx PDO Analog 3
//    Subscribed to 577/0x241 rx PDO Analog 0
//    Subscribed to 705/0x2c1 rx PDO Analog 1
//    Subscribed to 833/0x341 rx PDO Analog 2
//    Subscribed to 961/0x3c1 rx PDO Analog 3

//    System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_1.getMin() + nodeId));
//    System.out.println(intAndHex(CANOpenService.RECEIVE_PDO_1.getMin() + nodeId));
//    System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_2.getMin() + nodeId));
//    System.out.println(intAndHex(CANOpenService.RECEIVE_PDO_2.getMin() + nodeId));
//    System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_3.getMin() + nodeId));
//    System.out.println(intAndHex(CANOpenService.RECEIVE_PDO_1.getMin() + nodeId + 0x40));
//    System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_2.getMin() + nodeId + 0x40));
//    System.out.println(intAndHex(CANOpenService.RECEIVE_PDO_2.getMin() + nodeId + 0x40));
//    System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_3.getMin() + nodeId + 0x40));

    //IntStream.range(0, 2).forEach(index -> System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_3.getMin() + nodeId + (index * 0x80))));
    //IntStream.range(0, 2).forEach(index -> System.out.println(intAndHex(CANOpenService.TRANSMIT_PDO_4.getMin() + nodeId + (index * 0x80))));

    subscribe(connection, "tpdo_0x180", "TRANSMIT_PDO_1:" + nodeId + ":RECORD", new DigitalOutputCallback()); // digital

    subscribe(connection, "rpdo_0x200", "RECEIVE_PDO_1:" + nodeId + ":RECORD", new AnalogOutputCallback(0));
    subscribe(connection, "tpdo_0x280", "TRANSMIT_PDO_2:" + nodeId + ":RECORD", new AnalogOutputCallback(4));
    subscribe(connection, "rpdo_0x300", "RECEIVE_PDO_2:" + nodeId + ":RECORD", new AnalogOutputCallback(8));
    subscribe(connection, "tpdo_0x380", "TRANSMIT_PDO_3:" + nodeId + ":RECORD", new AnalogOutputCallback(12));

    subscribe(connection, "rpdo_0x240", "RECEIVE_PDO_1:" + nodeId + 0x40 + ":RECORD", new AnalogOutputCallback(16));
    subscribe(connection, "tpdo_0x2c0", "TRANSMIT_PDO_2:" + nodeId + 0x40 + ":RECORD", new AnalogOutputCallback(20));
    subscribe(connection, "rpdo_0x340", "RECEIVE_PDO_2:" + nodeId + 0x40 + ":RECORD", new AnalogOutputCallback(24));
    subscribe(connection, "tpdo_0x3c0", "TRANSMIT_PDO_3:" + nodeId + 0x40 + ":RECORD", new AnalogOutputCallback(28));
    subscribe(connection, "tpdo_0x3c0", "TRANSMIT_PDO_3:" + nodeId + 0x40 + ":RECORD", new AnalogOutputCallback(28));

    subscribe(connection, "tpdo_0x480", "TRANSMIT_PDO_4:" + nodeId + ":RECORD", new ConfigurationCallback(connection));

    System.out.println("Send MPDO to 0x480 address");
    connection.writeRequestBuilder()
      .addItem("mpdo", "TRANSMIT_PDO_4:0:RECORD",
          //new CANOpenMPDO((short) (0x80 + nodeId), new IndexAddress(0x4e01, (short) 1), new byte[] {0,0,0,1})
          (Byte) (byte) (0x80 + nodeId),
          (Byte) (byte) 0x01,
          (Byte) (byte) 0x4e,
          (Byte) (byte) 0x01,
          (Byte) (byte) 0x01,
          (Byte) (byte) 0x00,
          (Byte) (byte) 0x00,
          (Byte) (byte) 0x00
        )
      .build().execute().get();

//    int functionLimit = 0x80;
//    int inputLimit = 16;
//    int outputLimit = 16;
//
//    int analogBase = 0x2280;
//    System.out.println("Analog outputs");
//    for (int index = 0; index < inputLimit; index++) {
//      readIO(connection, nodeId, analogBase, index);
//    }
//
//    int digitalBase = 0x2380;
//    System.out.println("Digital outputs");
//    for (int index = 0; index < inputLimit; index++) {
//      readIO(connection, nodeId, digitalBase, index);
//    }
  }

  private static void subscribe(PlcConnection connection, String name, String field, PDOConsumer callback) throws Exception {
    Builder subscriptionBuilder = connection.subscriptionRequestBuilder();
    subscriptionBuilder.addEventField(name, field);

    PlcSubscriptionResponse response = subscriptionBuilder.build().execute().get();
    for (String subscriptionName : response.getFieldNames()) {
      response.getSubscriptionHandle(subscriptionName).register((reply) -> {
        try (MDCCloseable mdc = MDC.putCloseable("field", "PDO:" + field)) {
          callback.accept(reply);
        }
      });
    }
  }


  private static String readString(PlcConnection connection, int nodeId, int index,
      int subindex) {
    PlcReadRequest request = connection.readRequestBuilder()
      .addItem("text", "SDO:" + nodeId + ":" + index + "/" + subindex + ":VISIBLE_STRING")
      .build();

    try {
      return request.execute().get().getString("text");
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    return null;
  }

  static CompletableFuture<byte[]> readBytes(PlcConnection connection, int nodeId, IndexAddress address) {
    return readBytes(connection, nodeId, address.getIndex(), address.getSubindex());
  }

  static CompletableFuture<byte[]> readBytes(PlcConnection connection, int nodeId, int index, int subindex) {
    try (final MDCCloseable mdc = MDC.putCloseable("field", "SDO:" + Integer.toHexString(index) + "/" + Integer.toHexString(subindex))) {
      System.out.println("---> Request " + intAndHex(index) + "/" + intAndHex(subindex) + " from " + nodeId);

      String fieldExpression = "SDO:" + nodeId + ":0x" + Integer.toHexString(index) + "/0x" + Integer.toHexString(subindex) + ":RECORD";
      PlcReadRequest request = connection.readRequestBuilder()
        .addItem("record", fieldExpression)
        .build();

      LOGGER.info("SDO request {}", fieldExpression);

      return request.execute().thenApply(response -> {
        System.out.println("<--- Response " + intAndHex(index) + "/" + intAndHex(subindex) + " from " + nodeId);
        Object recordObj = response.getObject("record");
        //LOGGER.info("SDO Answer {}, payload type: {}", fieldExpression, (recordObj != null ? recordObj.getClass() : "<unknown>"));

        if (recordObj instanceof Collection) {
          Collection<PlcValue> record = (Collection<PlcValue>) recordObj;
          byte[] data = new byte[record.size()];
          int pos = 0;
          for (PlcValue val : record) {
            if (val instanceof PlcSINT) {
              data[pos++] = val.getByte();
            } else if (val instanceof PlcBYTE) {
              data[pos++] = (byte) ((PlcBYTE) val).getBYTE();
            } else {
              LOGGER.error("Unknown value type " + val.getClass());
            }
          }

          return data;
        }
        return new byte[0];
      });
    }
  }

  private static String intAndHex(int val) {
    return val + "(0x" + Integer.toHexString(val) + ")";
  }

}
