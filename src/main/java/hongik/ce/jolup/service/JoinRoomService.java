package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.room.JoinRoom;
import hongik.ce.jolup.domain.room.JoinRoomRepository;
import hongik.ce.jolup.domain.room.RoomRepository;
import hongik.ce.jolup.domain.room.RoomRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JoinRoomService {

    private final JoinRoomRepository joinRoomRepository;
    private final RoomRepository roomRepository;
    @Transactional
    public Long createJoinRoom(User user, Long roomId) {
        return joinRoomRepository.save(JoinRoom.builder()
                .user(user)
                .room(roomRepository.getById(roomId))
                .roomRole(RoomRole.MASTER).build()).getId();
    }

    @Transactional
    public Long addJoinRoom(User user, Long roomId) {
        return joinRoomRepository.save(JoinRoom.builder()
                .user(user)
                .room(roomRepository.getById(roomId))
                .roomRole(RoomRole.GUEST).build()).getId();
    }

    public List<JoinRoom> findMyJoinRooms(User user) {
        return joinRoomRepository.findByUser(user);
    }
}
