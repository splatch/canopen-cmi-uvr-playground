package brute.force.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.plc4x.java.socketcan.readwrite.SocketCANFrame;

public class MemoryFrameStore implements FrameStore {

    private final LinkedList<FrameEntry> frames = new LinkedList<>();
    private final int size;

    public MemoryFrameStore() {
        this(5000);
    }

    public MemoryFrameStore(int size) {
        this.size = size;
    }

    @Override
    public List<FrameEntry> getFrames() {
        return Collections.unmodifiableList(new ArrayList<>(frames));
    }

    @Override
    public List<FrameEntry> getFrames(int currentPage, int pageSize, Predicate<FrameEntry> predicate) {
        return frames.stream()
            .filter(predicate)
            .skip(pageSize * (currentPage - 1))
            .limit(pageSize)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<FrameEntry> find(UUID id) {
        return frames.stream().filter(entry -> id.equals(entry.id))
            .findFirst();
    }

    public void add(UUID uuid, long time, String iface, SocketCANFrame frame) {
        if (frames.size() >= size) {
            frames.removeLast();
        }
        frames.addLast(new FrameEntry(uuid, time, iface, frame));
    }

    public void clear() {
        frames.clear();
    }

}
