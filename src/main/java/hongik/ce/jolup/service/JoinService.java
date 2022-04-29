package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.dto.UserDto;
import hongik.ce.jolup.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class JoinService {

    private final JoinRepository joinRepository;
    private final UserService userService;
    private final RoomService roomService;

    public Long saveJoin(JoinDto joinDto) {
        return joinRepository.save(joinDto.toEntity()).getId();
    }

    public List<JoinDto> findByUser(User user) {
        List<Join> joins = joinRepository.findByUser(user);
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public List<JoinDto> findByRoom(RoomDto roomDto) {
        List<Join> joins = joinRepository.findByRoom(roomDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public JoinDto findOne(UserDto userDto, RoomDto roomDto) {
        Optional<Join> optionalJoin = joinRepository.findByUserAndRoom(userDto.toEntity(), roomDto.toEntity());
        if (optionalJoin.isEmpty())
            return null;
        Join join = optionalJoin.get();
        JoinDto joinDto = join.toDto();
        return joinDto;
    }

    public List<JoinDto> findByRoomSort(RoomDto roomDto) {
        List<Join> joins = joinRepository.findByRoomSort(roomDto.toEntity());
        List<JoinDto> joinDtos = joins.stream()
                .map(Join::toDto)
                .collect(Collectors.toList());
        return joinDtos;
    }

    public void deleteJoin(Long id) {
        joinRepository.deleteById(id);
    }
}
