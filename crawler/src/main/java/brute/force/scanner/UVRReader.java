package brute.force.scanner;

import brute.force.store.MemoryFrameStore;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Queue;
import java.util.UUID;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.can.context.CANOpenDriverContext;
import org.apache.plc4x.java.can.listener.Callback;
import org.apache.plc4x.java.cmi.readwrite.DisplayPage;
import org.apache.plc4x.java.cmi.readwrite.DisplayPages;
import org.apache.plc4x.java.cmi.readwrite.IndexAddress;
import org.apache.plc4x.java.cmi.readwrite.io.DisplayPagesIO;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;
import org.apache.plc4x.java.spi.generation.ReadBuffer;

public class UVRReader {

  private static String prefix = "  ";

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

    MemoryFrameStore store = new MemoryFrameStore();
    CANOpenDriverContext.CALLBACK.addCallback(new Callback() {
      @Override
      public void receive(SocketCANFrame frame) {
        store.add(UUID.randomUUID(), System.currentTimeMillis(), iface, frame);
      }
    });

    PlcDriverManager driverManager = new PlcDriverManager();
    PlcConnection connection = driverManager
        .getConnection("canopen:javacan://" + iface + "?nodeId=11");

    read(connection, nodeId, 0x5800, 0x0, 0);
  }

  private static void read(PlcConnection connection, int nodeId, int index, int subindex, int nesting) {
    connection.readRequestBuilder()
        .addItem("root", "SDO:" + nodeId + ":" + index + "/" + subindex + ":RECORD")
        .build().execute().handle((response, error) -> handleAnswer(connection, nodeId, nesting, response, error));
  }

  private static Object handleAnswer(PlcConnection connection, int nodeId, int nesting, PlcReadResponse response, Throwable error) {
    if (error != null) {
      System.out.println(repeat(nesting) + " Error awaiting answer");
      System.out.println(repeat(nesting) + " -> " + error.getMessage());
      error.printStackTrace(System.out);
    } else {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();

      Collection<Byte> bytes = response.getAllBytes("root");
      bytes.forEach(stream::write);

      try {
        DisplayPages pages = parse(stream.toByteArray());
        for (DisplayPage page : pages.getPages()) {
          IndexAddress link = page.getLink();
          IndexAddress text = page.getText();
          String label = connection.readRequestBuilder().addItem("text", "SDO:" + nodeId + ":" + text.getIndex() + "/" + text.getSubIndex() + ":VISIBLE_STRING")
            .build().execute().handle((r, e) -> e != null ? "error " + e.getMessage() : r.getString("text"))
            .get();
          System.out.println(repeat(nesting) + " " + label);
          read(connection, nodeId, nesting + 1, link.getIndex(), link.getSubIndex());
        }
      } catch (Exception e) {
        System.out.println(repeat(nesting) + " Error while parsing data");
        System.out.println(repeat(nesting) + " -> " + e.getMessage());
      }
    }

    return null;
  }

  private static DisplayPages parse(byte[] data) throws Exception {
    ReadBuffer buffer = new ReadBuffer(data, true);
    return DisplayPagesIO.staticParse(buffer);
  }

  private static String repeat(int times) {
    String out = "";
    for (int i = 0; i < times; i++) {
      out += "  ";
    }
    return out;
  }
}
