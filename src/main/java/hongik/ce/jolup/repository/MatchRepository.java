package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRoom(Room room);
    List<Match> findByHome(Member home);
    List<Match> findByAway(Member away);
    Optional<Match> findByRoomAndRoundNoAndMatchNo(Room room, Integer roundNo, Integer matchNo);
}
