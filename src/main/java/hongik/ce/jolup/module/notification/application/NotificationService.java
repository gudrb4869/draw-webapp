package hongik.ce.jolup.module.notification.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(Notification::read);
    }

    public List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Member member, boolean b) {
        return notificationRepository.findByMemberAndCheckedOrderByCreatedDateDesc(member, b);
    }

    public long countByMemberAndChecked(Member member, boolean b) {
        return notificationRepository.countByMemberAndChecked(member, b);
    }

    public void deleteByMemberAndChecked(Member member, boolean b) {
        notificationRepository.deleteByMemberAndChecked(member, b);
    }

    public Notification getNotification(Member member, Long id) {
        Notification notification = getNotification(id);
        check(member, notification);
        return notification;
    }

    private void check(Member member, Notification notification) {
        if (!notification.getMember().equals(member)) {
            throw new IllegalStateException("잘못된 접근입니다.");
        }
    }

    private Notification getNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
    }

    public void read(Notification notification) {
        notification.read();
    }
}
