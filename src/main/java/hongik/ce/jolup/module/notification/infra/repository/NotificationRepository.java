package hongik.ce.jolup.module.notification.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByMemberAndChecked(Member member, boolean checked);

    List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Member member, boolean b);

    void deleteByMemberAndChecked(Member member, boolean b);
}
