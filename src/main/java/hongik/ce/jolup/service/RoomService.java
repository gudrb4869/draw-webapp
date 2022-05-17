package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.CompetitionDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public Long saveRoom(RoomDto roomDto) {
        return roomRepository.save(roomDto.toEntity()).getId();
    }

    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }

    public RoomDto findOne(Long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        return room.toDto();
    }

    public List<BelongDto> getBelongs(Long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        List<BelongDto> belongDtos = room.getBelongs().stream().map(Belong::toDto).collect(Collectors.toList());
        return belongDtos;
    }

    public List<CompetitionDto> getCompetitions(Long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty())
            return null;
        Room room = optionalRoom.get();
        List<CompetitionDto> competitionDtos = room.getCompetitions().stream().map(Competition::toDto).collect(Collectors.toList());
        return competitionDtos;
    }
}
