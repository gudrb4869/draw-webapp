package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.Access;
import hongik.ce.jolup.domain.room.RoomInvitedEvent;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long saveRoom(Room room) {
        return roomRepository.save(room).getId();
    }

    @Transactional
    public void inviteRoom(Room room, Set<Member> members, String admin) {
        eventPublisher.publishEvent(new RoomInvitedEvent(room, admin +"님이 회원님을 방에 초대하였습니다.", members));
    }

    @Transactional
    public Long updateRoom(Long roomId, String name, Access access) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        room.updateName(name);
        room.updateAccess(access);
        return room.getId();
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    public Room findOne(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }
}
