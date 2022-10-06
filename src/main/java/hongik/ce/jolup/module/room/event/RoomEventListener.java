package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.domain.entity.NotificationType;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.notification.application.NotificationService;
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
        log.info("invited to " + room.getTitle() + ".");
    }

    private void saveNotification(Room room, String message, Member member) {
        notificationService.save(Notification.from(room.getTitle(), room.getId().toString(),
                message, false, member, NotificationType.ROOM_INVITED));
    }
}
