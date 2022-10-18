package hongik.ce.jolup.module.member.event;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Async
@Transactional
@Component
@RequiredArgsConstructor
public class FollowEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleFollowEvent(FollowEvent followEvent) {
        Follow follow = followEvent.getFollow();
        String message = followEvent.getMessage();
        Member follower = follow.getFollower();
        Member following = follow.getFollowing();
        Notification notification = Notification.from("/profile/" + following.getId(), false, message, follower);
        notificationRepository.save(notification);
    }
}
