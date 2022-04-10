package hongik.ce.jolup.domain.rooms;

import hongik.ce.jolup.domain.accounts.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRoomRepository extends JpaRepository<JoinRoom, Long> {
        List<JoinRoom> findByAccount(Account account);
}
