package hongik.ce.jolup.module.room.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.event.RoomInvitedEvent;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final JoinRepository joinRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Long saveRoom(Room room) {
        return roomRepository.save(room).getId();
    }

    public Room createNewRoom(RoomForm roomForm, Member member) {
        Room room = Room.from(roomForm);
        return roomRepository.save(room);
    }

    public void inviteRoom(Room room, Set<Member> members, String admin) {
        eventPublisher.publishEvent(new RoomInvitedEvent(room, admin +"님이 회원님을 방에 초대하였습니다.", members));
    }

    public Long updateRoom(Long roomId, String name, boolean access) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        room.updateName(name);
        room.updateAccess(access);
        return room.getId();
    }

    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    public Room findOne(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }
}
