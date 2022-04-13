package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRepository extends JpaRepository<Join, Long> {
        List<Join> findByUser(User user);
        List<Join> findByRoom(Room room);
}
