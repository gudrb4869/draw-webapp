package hongik.ce.jolup.domain.room;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.notification.Notification;
import hongik.ce.jolup.domain.notification.NotificationType;
import hongik.ce.jolup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class RoomEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleRoomInvitedEvent(RoomInvitedEvent roomInvitedEvent) {
        Room room = roomInvitedEvent.getRoom();
        Set<Member> members = roomInvitedEvent.getMembers();
        String message = roomInvitedEvent.getMessage();
        for (Member member : members) {
            saveNotification(room, message, member);
        }
        log.info("invited to " + room.getName() + ".");
    }

    private void saveNotification(Room room, String message, Member member) {
        notificationService.save(Notification.from(room.getName(), room.getId().toString(),
                message, false, member, NotificationType.ROOM_INVITED));
    }
}
