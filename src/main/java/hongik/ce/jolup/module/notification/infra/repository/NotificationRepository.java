package hongik.ce.jolup.module.notification.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByMemberAndChecked(Member member, boolean checked);

    @Transactional
    List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Member member, boolean b);

    @Transactional
    void deleteByMemberAndChecked(Member member, boolean b);

    @Transactional
    List<Notification> findByMemberOrderByCreatedDateDesc(Member member);

    @Transactional
    List<Notification> findFirst5ByMemberOrderByCreatedDateDesc(Member member);
}
