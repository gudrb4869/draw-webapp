package hongik.ce.jolup.module.room.infra.repository;

import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryExtension {
    @EntityGraph(value = "Room.withJoinsAndMembers")
    Optional<Room> findRoomWithJoinsAndMembersById(Long id);

    List<Room> findFirst9ByRevealedOrderByCreatedDateDesc(boolean revealed);
}
