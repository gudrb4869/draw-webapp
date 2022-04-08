package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.accounts.AccountRepository;
import hongik.ce.jolup.domain.rooms.Room;
import hongik.ce.jolup.domain.rooms.RoomRepository;
import hongik.ce.jolup.web.dto.RoomSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Long creatRoom(RoomSaveRequestDto requestDto, Account account) {
        return roomRepository.save(Room.builder()
                .subject(requestDto.getSubject())
                .roomType(requestDto.getRoomType())
                .account(account).build()).getId();
    }

    /*public Optional<Room> findMyRooms(Long userId) {
        return roomRepository.findByUserId(userId);
    }*/

    public List<Room> findMyRooms() {
        return roomRepository.findAll();
    }
}
