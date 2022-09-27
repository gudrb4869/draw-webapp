package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.notification.Notification;
import hongik.ce.jolup.repository.NotificationRepository;
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
        return notificationRepository.findById(id).orElse(null);
    }
}
