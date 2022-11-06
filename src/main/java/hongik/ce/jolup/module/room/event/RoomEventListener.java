package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.account.domain.entity.Account;
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
    public void handleRoomInvitedEvent(RoomInviteEvent roomInviteEvent) {
        Room room = roomInviteEvent.getRoom();
        String message = roomInviteEvent.getMessage();
        Set<Account> accounts = roomInviteEvent.getAccounts();
        accounts.forEach(account -> {
            saveNotification(room, message, account);
        });
    }

    private void saveNotification(Room room, String message, Account account) {
        notificationRepository.save(Notification.from("/rooms/" + room.getId(), false, message, account));
    }
}
