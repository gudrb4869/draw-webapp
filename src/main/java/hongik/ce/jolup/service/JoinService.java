package hongik.ce.jolup.service;

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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;

    public Long save(Long userId, Long roomId) {
        Optional<User> user = userRepository.findById(userId);
        RoomDto room = roomService.findRoom(roomId);

        Join join = Join.builder()
                .user(user.get())
                .room(room.toEntity())
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
                    .roomDto(Room.toDto(join.getRoom())).build());
        }
        return joinDtos;
    }

    public List<JoinDto> findByUser(User user) {
        return findJoins(joinRepository.findByUser(user));
    }

    public List<JoinDto> findByRoom(RoomDto roomDto) {
        return findJoins(joinRepository.findByRoom(roomDto.toEntity()));
    }
}
