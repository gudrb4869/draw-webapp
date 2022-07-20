package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
