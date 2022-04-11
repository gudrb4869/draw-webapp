package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.rooms.Room;
import hongik.ce.jolup.domain.rooms.RoomRepository;
import hongik.ce.jolup.web.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public Long createRoom(RoomDto requestDto) {
        return roomRepository.save(Room.builder()
                .subject(requestDto.getSubject())
                .roomType(requestDto.getRoomType())
                .memNum(requestDto.getMemNum()).build()).getId();
    }
}
