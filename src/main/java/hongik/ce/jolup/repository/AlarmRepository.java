package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.alarm.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select a from Alarm a join fetch a.sendMember join fetch a.receiveMember where a.id = :id and a.receiveMember.id = :memberId")
    Optional<Alarm> findByIdAndReceiveMemberId(@Param("id") Long id, @Param("memberId") Long memberId);

    @Query("select a from Alarm a join fetch a.sendMember join fetch a.receiveMember where a.receiveMember.id = :memberId")
    List<Alarm> findByReceiveMemberId(@Param("memberId") Long memberId);

    @Query("select a from Alarm a join fetch a.sendMember join fetch a.receiveMember where a.receiveMember.id = :memberId and a.requestId = :requestId")
    Optional<Alarm> findByReceiveMemberIdAndRequestId(@Param("memberId") Long memberId, @Param("requestId") Long requestId);
}
