package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.join.JoinRole;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.JoinRepository;
import hongik.ce.jolup.repository.RoomRepository;
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

    public Long join(Long userId, Long roomId) {
        Optional<User> user = userRepository.findById(userId);
        RoomDto room = roomService.findRoom(roomId);

        Join join = Join.builder()
                .user(user.get())
                .room(room.toEntity())
                .build();

        joinRepository.save(join);
        return join.getId();
        /*return joinRepository.save(Join.builder()
                .user(user)
                .room(roomRepository.getById(roomId))
                .role(JoinRole.MASTER).build()).getId();*/
    }

    public List<JoinDto> findByUser(User user) {
        List<Join> joins = joinRepository.findByUser(user);
        List<JoinDto> joinDtos = new ArrayList<>();
        for(Join join : joins) {
            joinDtos.add(JoinDto.builder()
                            .id(join.getId())
                            .userDto(User.toDto(join.getUser()))
                            .roomDto(Room.toDto(join.getRoom())).build());
        }
        return joinDtos;
    }

    public List<Join> findByRoom(Room room) {
        return joinRepository.findByRoom(room);
    }
}
