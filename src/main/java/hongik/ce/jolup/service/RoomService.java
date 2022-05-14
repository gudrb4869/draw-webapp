package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.repository.RoomRepository;
import hongik.ce.jolup.dto.RoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public Long saveRoom(RoomDto roomDto) {
        return roomRepository.save(roomDto.toEntity()).getId();
    }

    public List<RoomDto> findAll() {
        return roomRepository.findAll().stream()
                .map(Room::toDto)
                .collect(Collectors.toList());
    }

    public RoomDto getRoom(Long roomId) {
        Optional<Room> roomWrapper = roomRepository.findById(roomId);
        if (roomWrapper.isEmpty()) {
            return null;
        }
        Room room = roomWrapper.get();
        log.info("room.joins = {}", room.getJoins());
        return room.toDto();
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
