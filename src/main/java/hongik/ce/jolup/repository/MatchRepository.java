package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRoom(Room room);
    List<Match> findByUser1(User user1);
    List<Match> findByUser2(User user2);
    Optional<Match> findByRoomAndRoundNoAndMatchNo(Room room, Long roundNo, Long matchNo);
}
