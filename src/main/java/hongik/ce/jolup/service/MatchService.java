package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.domain.match.MatchStatus;
import hongik.ce.jolup.domain.score.Score;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.MatchRepository;
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
public class MatchService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public Long save(Match match) {
        return matchRepository.save(match).getId();
    }

    public Long save(Long roomId, Long user1Id, Long user2Id) {
        if (user1Id == user2Id) {
            return null;
        }

        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        Optional<User> optionalUser1 = userRepository.findById(user1Id);
        Optional<User> optionalUser2 = userRepository.findById(user2Id);

        if (optionalRoom.isEmpty()) {
            throw new IllegalStateException("존재하지 않은 방입니다!");
        }

        if (optionalUser1.isEmpty() || optionalUser2.isEmpty()) {
            throw new IllegalStateException("존재하지 않은 회원입니다!");
        }

        Score score = Score.builder()
                .user1Score(0)
                .user2Score(0).build();

        Match match = Match.builder()
                .room(optionalRoom.get())
                .user1(optionalUser1.get())
                .user2(optionalUser2.get())
                .score(score)
                .matchStatus(MatchStatus.READY)
                .build();
        matchRepository.save(match);
        return match.getId();
    }

    public List<MatchDto> findByRoom(RoomDto roomDto) {
        List<Match> matches = matchRepository.findByRoom(roomDto.toEntity());
        List<MatchDto> matchDtos = new ArrayList<>();

        for (Match match : matches) {
            matchDtos.add(MatchDto.builder()
                    .id(match.getId())
                    .roomDto(Room.toDto(match.getRoom()))
                    .user1Dto(User.toDto(match.getUser1()))
                    .user2Dto(User.toDto(match.getUser2()))
                    .score(match.getScore())
                    .matchStatus(match.getMatchStatus())
                    .build());
        }
        return matchDtos;
    }

    public MatchDto findById(Long id) {
        Match match = matchRepository.findById(id).get();
        MatchDto matchDto = Match.toDto(match);
        return matchDto;
    }

}