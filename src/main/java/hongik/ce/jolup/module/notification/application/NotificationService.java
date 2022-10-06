package hongik.ce.jolup.module.notification.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(Notification::read);
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Member member, boolean b) {
        return notificationRepository.findByMemberAndCheckedOrderByCreatedDateDesc(member, b);
    }

    public long countByMemberAndChecked(Member member, boolean b) {
        return notificationRepository.countByMemberAndChecked(member, b);
    }

    @Transactional
    public void deleteByMemberAndChecked(Member member, boolean b) {
        notificationRepository.deleteByMemberAndChecked(member, b);
    }

    public Notification findOne(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
    }
}
