package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long> {
        List<JoinRoom> findByUser(User user);
}
