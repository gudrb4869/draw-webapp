package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;

    public Long save(Join join) {
        return joinRepository.save(join).getId();
    }

    public Long save(Long userId, Long roomId) {
        Optional<User> user = userRepository.findById(userId);
        RoomDto roomDto = roomService.findRoom(roomId);

        Join join = Join.builder()
                .user(user.get())
                .room(roomDto.toEntity())
                .result(Result.builder().plays(0).win(0).draw(0)
                        .lose(0).goalFor(0).goalAgainst(0)
                        .goalDifference(0).points(0).build())
                .build();

        joinRepository.save(join);
        return join.getId();
    }

    public List<JoinDto> findJoins(List<Join> joins) {
        List<JoinDto> joinDtos = new ArrayList<>();
        for(Join join : joins) {
            joinDtos.add(JoinDto.builder()
                    .id(join.getId())
                    .userDto(User.toDto(join.getUser()))
                    .roomDto(Room.toDto(join.getRoom()))
                    .result(join.getResult())
                    .build());
        }
        return joinDtos;
    }

    public List<JoinDto> findByUser(User user) {
        return findJoins(joinRepository.findByUser(user));
    }

    public List<JoinDto> findByRoom(RoomDto roomDto) {
        return findJoins(joinRepository.findByRoom(roomDto.toEntity()));
    }

    public Join findOne(User user, Room room) {
        return joinRepository.findByUserAndRoom(user, room).get();
    }

    public List<JoinDto> findByRoomSort(RoomDto roomDto) {
        return findJoins(joinRepository.findByRoomSort(roomDto.toEntity()));
    }
}
