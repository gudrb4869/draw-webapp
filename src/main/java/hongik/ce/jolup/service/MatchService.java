package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.match.Match;
import hongik.ce.jolup.dto.MatchDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.MatchRepository;
import hongik.ce.jolup.repository.RoomRepository;
import hongik.ce.jolup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchService {
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public Long saveMatch(MatchDto matchDto) {
        return matchRepository.save(matchDto.toEntity()).getId();
    }

    public List<MatchDto> findByRoom(RoomDto roomDto) {
        List<Match> matches = matchRepository.findByRoom(roomDto.toEntity());
        List<MatchDto> matchDtos = matches.stream()
                .map(Match::toDto)
                .collect(Collectors.toList());
        return matchDtos;
    }

    public MatchDto getMatch(Long id) {
        Optional<Match> matchWrapper = matchRepository.findById(id);
        if (matchWrapper.isEmpty()) {
            return null;
        }
        Match match = matchWrapper.get();
        MatchDto matchDto = match.toDto();
        return matchDto;
    }

    public MatchDto findOne(RoomDto roomDto, Integer roundNo, Integer matchNo) {
        Optional<Match> optionalMatch = matchRepository.findByRoomAndRoundNoAndMatchNo(roomDto.toEntity(), roundNo, matchNo);
        if (optionalMatch.isEmpty()) {
            return null;
        }
        Match match = optionalMatch.get();
        MatchDto matchDto = match.toDto();
        return matchDto;
    }

    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

}