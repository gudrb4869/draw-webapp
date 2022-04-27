package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.dto.UserDto;
import hongik.ce.jolup.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRepository;
    private final UserService userService;
    private final RoomService roomService;

    public Long save(JoinDto joinDto) {
        return joinRepository.save(joinDto.toEntity()).getId();
    }

    public Long save(Long userId, Long roomId) {
        UserDto userDto = userService.findUser(userId);
        RoomDto roomDto = roomService.findRoom(roomId);

        Join join = Join.builder()
                .result(Result.builder().plays(0).win(0).draw(0)
                        .lose(0).goalFor(0).goalAgainst(0)
                        .goalDifference(0).points(0).build())
                .build();
        join.setUser(userDto.toEntity());
        join.setRoom(roomDto.toEntity());
        joinRepository.save(join);
        return join.getId();
    }

    public List<JoinDto> findJoins(List<Join> joins) {
        List<JoinDto> joinDtos = new ArrayList<>();
        for(Join join : joins) {
            joinDtos.add(join.toDto());
        }
        return joinDtos;
    }

    public List<JoinDto> findByUser(User user) {
        List<Join> joins = joinRepository.findByUser(user);
        List<JoinDto> joinDtos = new ArrayList<>();
        for (Join join : joins) {
            joinDtos.add(join.toDto());
        }
        return joinDtos;
    }

    public List<JoinDto> findByRoom(RoomDto roomDto) {
        List<Join> joins = joinRepository.findByRoom(roomDto.toEntity());
        List<JoinDto> joinDtos = new ArrayList<>();
        for (Join join : joins) {
            joinDtos.add(join.toDto());
        }
        return joinDtos;
    }

    public JoinDto findOne(User user, Room room) {
        Optional<Join> optionalJoin = joinRepository.findByUserAndRoom(user, room);
        if (optionalJoin.isEmpty())
            return null;
        Join join = optionalJoin.get();
        JoinDto joinDto = join.toDto();
        return joinDto;
    }

    public List<JoinDto> findByRoomSort(RoomDto roomDto) {
        return findJoins(joinRepository.findByRoomSort(roomDto.toEntity()));
    }

    public void delete(Long id) {
        joinRepository.deleteById(id);
    }
}
