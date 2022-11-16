package hongik.ce.jolup.module.room.application;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.event.RoomInviteEvent;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final JoinRepository joinRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Room createNewRoom(RoomForm roomForm, Account account) {
        Room room = roomRepository.save(Room.from(roomForm));
        joinRepository.save(Join.from(room, account, Grade.ADMIN));
        return room;
    }

    public void inviteRoom(List<Account> friends, Room room, Set<Long> members) {
        Set<Account> inviteList = friends.stream().filter(f -> members.contains(f.getId())).collect(Collectors.toSet());
        for (Account account : inviteList) {
            joinRepository.save(Join.from(room, account, Grade.USER));
            room.addCount();
        }
        eventPublisher.publishEvent(new RoomInviteEvent(room, "방 '" + room.getTitle() + "'에서 회원님을 초대했습니다.", inviteList));
    }

    public Room findOne(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    public Room getRoom(Long id) {
        return roomRepository.findRoomWithJoinsAndMembersById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    public Room getRoom(Account account, Long id) {
        Room room = getRoom(id);
        check(account, room);
        return room;
    }

    private void check(Account account, Room room) {
        Join join = room.getJoins().stream()
                .filter(j -> j.getAccount() != null && j.getAccount().equals(account))
                .findFirst().orElse(null);
        if (join == null && !room.isRevealed()) {
            throw new AccessDeniedException("비공개 방입니다.");
        }
    }

    public Room getRoomToUpdate(Account account, Long id) {
        Room room = getRoom(id);
        checkIsAdmin(account, room);
        return room;
    }

    private void checkIsAdmin(Account account, Room room) {
        Join join = room.getJoins().stream()
                .filter(j -> j.getAccount() != null && j.getAccount().equals(account))
                .findFirst().orElse(null);
        if (join == null || !join.getGrade().equals(Grade.ADMIN)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void remove(Room room) {
        if (!room.isRemovable()) {
            throw new IllegalStateException("방을 삭제할 수 없습니다.");
        }
        roomRepository.delete(room);
    }

    public void addMember(Room room, Account account) {
        if (joinRepository.existsByRoomAndAccount(room, account)) {
            throw new IllegalArgumentException("이미 방에 참여중입니다.");
        }
        room.addCount();
        joinRepository.save(Join.from(room, account, Grade.USER));
    }

    public void removeMember(Room room, Account account) {
        Join join = joinRepository.findByRoomAndAccount(room, account)
                .orElseThrow(() -> new IllegalArgumentException("방에 참여중인 회원이 아닙니다."));
        if (join.getGrade().equals(Grade.ADMIN)) {
            throw new IllegalArgumentException("관리자는 방을 나갈 수 없습니다. 설정에 들어가서 방을 삭제하세요.");
        }
        room.subCount();
        joinRepository.delete(join);
    }

    public void updateRoom(Room room, RoomForm roomForm) {
        room.updateFrom(roomForm);
    }
}
