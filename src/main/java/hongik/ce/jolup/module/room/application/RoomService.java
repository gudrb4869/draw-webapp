package hongik.ce.jolup.module.room.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.event.RoomInvitedEvent;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Room createNewRoom(RoomForm roomForm, Member member) {
        Room room = Room.from(roomForm);
        return roomRepository.save(room);
    }

    public void inviteRoom(Room room, Set<Member> members, String admin) {
        eventPublisher.publishEvent(new RoomInvitedEvent(room, admin +"님이 회원님을 방에 초대하였습니다.", members));
    }

    public Room findOne(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    public Room getRoom(Long id) {
        return roomRepository.findRoomWithJoinsById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    public Room getRoom(Member member, Long id) {
        Room room = getRoom(id);
        check(member, room);
        return room;
    }

    private Join check(Member member, Room room) {
        Join join = room.getJoins().stream()
                .filter(j -> j.getMember().equals(member))
                .findFirst().orElse(null);
        if (join == null && !room.isAccess()) {
            throw new IllegalArgumentException("비공개 방입니다.");
        }
        return join;
    }

    public Room getRoomToUpdate(Member member, Long id) {
        Room room = getRoom(id);
        checkIsAdmin(member, room);
        return room;
    }

    private void checkIsAdmin(Member member, Room room) {
        Join join = room.getJoins().stream()
                .filter(j -> j.getMember().equals(member))
                .findFirst().orElse(null);
        if (join == null || !join.getGrade().equals(Grade.ADMIN)) {
            throw new IllegalArgumentException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void reveal(Room room) {
        room.reveal();
    }

    public void conceal(Room room) {
        room.conceal();
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() > 0 && newTitle.length() <= 50;
    }

    public void updateRoomTitle(Room room, String newTitle) {
        room.updateTitle(newTitle);
    }

    public void remove(Room room) {
        if (!room.isRemovable()) {
            throw new IllegalStateException("방을 삭제할 수 없습니다.");
        }
        roomRepository.delete(room);
    }
}
