package brute.force.store;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface FrameStore {

    List<FrameEntry> getFrames();

    List<FrameEntry> getFrames(int currentPage, int pageSize, Predicate<FrameEntry> filter);

    Optional<FrameEntry> find(UUID id);

}
