package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.repository.RoomRepository;
import hongik.ce.jolup.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public Long save(RoomDto roomDto) {
        return roomRepository.save(Room.builder()
                .subject(roomDto.getSubject())
                .roomType(roomDto.getRoomType())
                .memNum(roomDto.getMemNum()).build()).getId();
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public RoomDto findRoom(Long roomId) {
        Optional<Room> roomWrapper = roomRepository.findById(roomId);
        Room room = roomWrapper.get();

        RoomDto roomDto = RoomDto.builder()
                .id(room.getId())
                .subject(room.getSubject())
                .roomType(room.getRoomType())
                .memNum(room.getMemNum())
                .build();

        return roomDto;
    }
}
