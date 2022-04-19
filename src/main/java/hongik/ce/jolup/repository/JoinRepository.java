package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinRepository extends JpaRepository<Join, Long> {
        List<Join> findByUser(User user);
        List<Join> findByRoom(Room room);
        Optional<Join> findByUserAndRoom(User user, Room room);

        @Query("SELECT j from Join j where j.room = :#{#room} order by j.result.points desc, j.result.goalDifference desc, j.result.goalFor desc, j.user.name asc")
        List<Join> findByRoomSort(@Param("room") Room room);
}
