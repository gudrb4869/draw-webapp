package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.FollowRepository;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Async
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleRoomEvent(RoomEvent roomEvent) {
        Room room = roomEvent.getRoom();
        Member following = room.getJoins().stream().filter(j -> j.getGrade().equals(Grade.ADMIN)).map(Join::getMember)
                .findAny().orElseThrow(() -> new IllegalStateException("방장이 없습니다."));
        String message = roomEvent.getMessage();
        List<Member> members = followRepository.findByFollowing(following).stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
        for (Member member : members) {
            saveNotification(room, message, member);
        }
    }

    private void saveNotification(Room room, String message, Member member) {
        notificationRepository.save(Notification.from("/rooms/" + room.getId(), false, message, member));
    }
}
