package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.rooms.JoinRoom;
import hongik.ce.jolup.domain.rooms.JoinRoomRepository;
import hongik.ce.jolup.domain.rooms.RoomRepository;
import hongik.ce.jolup.domain.rooms.RoomRole;
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
    public Long createJoinRoom(Account account, Long roomId) {
        return joinRoomRepository.save(JoinRoom.builder()
                .account(account)
                .room(roomRepository.getById(roomId))
                .roomRole(RoomRole.MASTER).build()).getId();
    }

    public List<JoinRoom> findMyJoinRooms(Account account) {
        return joinRoomRepository.findByAccount(account);
    }
}
