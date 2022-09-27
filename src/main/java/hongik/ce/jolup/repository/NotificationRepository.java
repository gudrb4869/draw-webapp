package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByMemberAndChecked(Member member, boolean checked);

    List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Member member, boolean b);

    void deleteByMemberAndChecked(Member member, boolean b);
}
