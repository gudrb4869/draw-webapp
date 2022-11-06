package hongik.ce.jolup.module.notification.application;

import hongik.ce.jolup.module.account.domain.entity.Account;
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

    public List<Notification> findByMemberAndCheckedOrderByCreatedDateDesc(Account account, boolean b) {
        return notificationRepository.findByAccountAndCheckedOrderByCreatedDateDesc(account, b);
    }

    public long countByMemberAndChecked(Account account, boolean b) {
        return notificationRepository.countByAccountAndChecked(account, b);
    }

    public void deleteByMemberAndChecked(Account account, boolean b) {
        notificationRepository.deleteByAccountAndChecked(account, b);
    }

    public Notification getNotification(Account account, Long id) {
        Notification notification = getNotification(id);
        check(account, notification);
        return notification;
    }

    private void check(Account account, Notification notification) {
        if (!notification.getAccount().equals(account)) {
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
