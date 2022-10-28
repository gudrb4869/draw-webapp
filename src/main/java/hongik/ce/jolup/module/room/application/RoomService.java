package hongik.ce.jolup.module.room.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.event.RoomInvitedEvent;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

    public Room createNewRoom(RoomForm roomForm, Member member) {
        Room room = roomRepository.save(Room.from(roomForm));
        joinRepository.save(Join.from(room, member, Grade.ADMIN));
        return room;
    }

    public void inviteRoom(List<Member> friends, Room room, Set<Long> members) {
        Set<Member> inviteList = friends.stream().filter(f -> members.contains(f.getId())).collect(Collectors.toSet());
        for (Member member : inviteList) {
            joinRepository.save(Join.from(room, member, Grade.USER));
        }
        eventPublisher.publishEvent(new RoomInvitedEvent(room, "모임 '" + room.getTitle() + "'에서 회원님을 초대했습니다.", inviteList));
    }

    public Room findOne(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
    }

    public Room getRoom(Long id) {
        return roomRepository.findRoomWithJoinsAndMembersById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
    }

    public Room getRoom(Member member, Long id) {
        Room room = getRoom(id);
        check(member, room);
        return room;
    }

    private void check(Member member, Room room) {
        Join join = room.getJoins().stream()
                .filter(j -> j.getMember().equals(member))
                .findFirst().orElse(null);
        if (join == null && !room.isAccess()) {
            throw new IllegalArgumentException("비공개 모임입니다.");
        }
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
            throw new IllegalStateException("모임을 삭제할 수 없습니다.");
        }
        roomRepository.delete(room);
    }

    public void addMember(Room room, Member member) {
        if (joinRepository.existsByRoomAndMember(room, member)) {
            throw new IllegalArgumentException("이미 모임에 참여중입니다.");
        }
        room.addCount();
        joinRepository.save(Join.from(room, member, Grade.USER));
    }

    public void removeMember(Room room, Member member) {
        Join join = joinRepository.findByRoomAndMember(room, member)
                .orElseThrow(() -> new IllegalArgumentException("모임에 참여중인 회원이 아닙니다."));
        if (join.getGrade().equals(Grade.ADMIN)) {
            throw new IllegalArgumentException("관리자는 모임을 나갈 수 없습니다. 설정에 들어가서 모임을 삭제하세요.");
        }
        room.subCount();
        joinRepository.delete(join);
    }
}
