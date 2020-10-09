package brute.force.store;

import java.util.UUID;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;

public class FrameEntry {

    public final UUID id;
    public final String iface;
    public final long time;
    public final SocketCANFrame frame;

    public FrameEntry(UUID id, long time, String iface, SocketCANFrame frame) {
        this.id = id;
        this.time = time;
        this.iface = iface;
        this.frame = frame;
    }

    public UUID getId() {
        return id;
    }

    public String getIface() {
        return iface;
    }

    public long getTime() {
        return time;
    }

    public SocketCANFrame getFrame() {
        return frame;
    }
}