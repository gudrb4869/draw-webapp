package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Async
@Transactional
@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleRoomCreatedEvent(RoomCreateEvent roomCreateEvent) {

    }

    @EventListener
    public void handleRoomInvitedEvent(RoomInviteEvent roomInviteEvent) {
        Room room = roomInviteEvent.getRoom();
        String message = roomInviteEvent.getMessage();
        Set<Member> members = roomInviteEvent.getMembers();
        members.forEach(member -> {
            saveNotification(room, message, member);
        });
    }

    private void saveNotification(Room room, String message, Member member) {
        notificationRepository.save(Notification.from("/rooms/" + room.getId(), false, message, member));
    }
}
