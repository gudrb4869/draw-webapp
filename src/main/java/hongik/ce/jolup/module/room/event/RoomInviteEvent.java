package hongik.ce.jolup.module.room.event;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.Getter;

import java.util.Set;

@Getter
public class RoomInviteEvent extends RoomEvent {
    private final Set<Account> accounts;

    public RoomInviteEvent(Room room, String message, Set<Account> accounts) {
        super(room, message);
        this.accounts = accounts;
    }
}
