package hongik.ce.jolup.module.room.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface JoinRepository extends JpaRepository<Join, Long> {

    @EntityGraph(value = "Join.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Page<Join> findByRoom(Room room, Pageable pageable);

    @EntityGraph(value = "Join.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Page<Join> findWithJoinByMember(Member member, Pageable pageable);

    boolean existsByRoomAndMember(Room room, Member member);

    Optional<Join> findByRoomAndMember(Room room, Member member);

    @EntityGraph(attributePaths = {"room"})
    List<Join> findWithRoomByMember(Member member);
}
