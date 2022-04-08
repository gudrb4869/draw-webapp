package hongik.ce.jolup.domain.rooms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
//    Optional<Room> findByUserId(Long userId);
}
