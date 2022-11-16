package hongik.ce.jolup.module.room.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
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

    @EntityGraph(attributePaths = {"account", "room"})
    Page<Join> findByRoom(Room room, Pageable pageable);

    @EntityGraph(attributePaths = {"account", "room"})
    Page<Join> findWithJoinByAccount(Account account, Pageable pageable);

    boolean existsByRoomAndAccount(Room room, Account account);

    Optional<Join> findByRoomAndAccount(Room room, Account account);

    @Transactional
    @EntityGraph(attributePaths = {"account", "room"})
    List<Join> findByAccount(Account account);
}
