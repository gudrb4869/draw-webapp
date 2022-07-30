package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.CompetitionDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public Long saveRoom(Room room) {
        return roomRepository.save(room).getId();
    }

    @Transactional
    public Long updateRoom(Long roomId, String title, RoomSetting roomSetting) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        room.updateTitle(title);
        room.updateRoomSetting(roomSetting);
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
