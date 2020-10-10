package brute.force.dump;

import org.apache.commons.codec.binary.Hex;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.can.context.CANOpenDriverContext;
import org.apache.plc4x.java.can.listener.Callback;
import org.apache.plc4x.java.canopen.readwrite.CANOpenPayload;
import org.apache.plc4x.java.canopen.readwrite.CANOpenSDORequest;
import org.apache.plc4x.java.canopen.readwrite.CANOpenSDOResponse;
import org.apache.plc4x.java.canopen.readwrite.SDOInitiateUploadRequest;
import org.apache.plc4x.java.canopen.readwrite.SDORequest;
import org.apache.plc4x.java.canopen.readwrite.io.CANOpenPayloadIO;
import org.apache.plc4x.java.canopen.readwrite.types.CANOpenService;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;
import org.apache.plc4x.java.spi.generation.ParseException;
import org.apache.plc4x.java.spi.generation.ReadBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CANOpenDump {

  private static Logger LOGGER = LoggerFactory.getLogger(CANOpenDump.class);

  //private Queue

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("Usage: java -jar ... [can-interface] [uvr-address]");
      System.out.println(" [can-interface] - name of can interface to attach for audit");
      System.exit(0);
    }

    String iface = args[0];

//    MemoryFrameStore store = new MemoryFrameStore();
    CANOpenDriverContext.CALLBACK.addCallback(new Callback() {
      @Override
      public void receive(SocketCANFrame frame) {
        final int identifier = frame.getIdentifier();

        final CANOpenService service = serviceId(identifier);

        System.out.print(iface + " | " + Integer.toHexString(identifier) + " | " + frame.getData().length + " | ");
        System.out.printf("%-16s ", Hex.encodeHexString(frame.getData()));
        System.out.printf("%-16s ", service);
        System.out.printf("%3s", Math.abs(service.getMin() - identifier) + "");

        final ReadBuffer buffer = new ReadBuffer(frame.getData(), true);
        try {
          final CANOpenPayload payload = CANOpenPayloadIO.staticParse(buffer, service);
          if (payload instanceof CANOpenSDORequest) {
            CANOpenSDORequest req = (CANOpenSDORequest) payload;
            SDORequest sdo = req.getRequest();
            if (sdo instanceof SDOInitiateUploadRequest) {
              SDOInitiateUploadRequest x = (SDOInitiateUploadRequest) sdo;
              System.out.printf("  %4s/", Integer.toHexString(x.getAddress().getIndex()));
              System.out.printf("%-2s", Integer.toHexString(x.getAddress().getSubindex()));
            }
          } else if (payload instanceof CANOpenSDOResponse) {
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }

        System.out.println();
      }
    });

    PlcDriverManager driverManager = new PlcDriverManager();
    PlcConnection connection = driverManager
        .getConnection("canopen:javacan://" + iface + "?nodeId=12");

  }

  private static String intAndHex(int val) {
    return val + "(0x" + Integer.toHexString(val) + ")";
  }

  private static CANOpenService serviceId(int cobId) {
    // form 32 bit socketcan identifier
    CANOpenService service = CANOpenService.valueOf((byte) (cobId >> 7));
    if (service == null) {
      for (CANOpenService val : CANOpenService.values()) {
        if (val.getMin() > cobId && val.getMax() < cobId) {
          return val;
        }
      }
    }
    return service;
  }

}
