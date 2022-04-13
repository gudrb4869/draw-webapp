package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.RoomRepository;
import hongik.ce.jolup.domain.join.JoinRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRoomRepository;
    private final RoomRepository roomRepository;

    public Long createJoinRoom(User user, Long roomId) {
        /*return joinRoomRepository.save(Join.builder()
                .user(user)
                .room(roomRepository.getById(roomId))
                .role(JoinRole.MASTER).build()).getId();*/
        return null;
    }

    public Long addJoinRoom(User user, Long roomId) {
        /*return joinRoomRepository.save(Join.builder()
                .user(user)
                .room(roomRepository.getById(roomId))
                .role(JoinRole.GUEST).build()).getId();*/
        return null;
    }

    public List<Join> findMyJoinRooms(User user) {
        return joinRoomRepository.findByUser(user);
    }
}
