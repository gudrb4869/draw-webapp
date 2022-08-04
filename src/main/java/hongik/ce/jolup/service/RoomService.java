package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
