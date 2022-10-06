package hongik.ce.jolup.module.room.infra.repository;

import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface RoomRepository extends JpaRepository<Room, Long> {
}
