package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.repository.BelongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BelongService {

    private final BelongRepository belongRepository;

    @Transactional
    public Long saveBelong(BelongDto belongDto) {
        return belongRepository.save(belongDto.toEntity()).getId();
    }

    @Transactional
    public void deleteBelong(Long belongId) {
        belongRepository.deleteById(belongId);
    }

    public List<BelongDto> findByMemberId(Long memberId) {
        List<Belong> belongs = belongRepository.findByMemberId(memberId);
        List<BelongDto> belongDtos = belongs.stream()
                .map(Belong::toDto)
                .collect(Collectors.toList());

        return belongDtos;
    }

    public BelongDto findByIdAndRoomId(Long id, Long roomId) {
        Optional<Belong> optionalBelong = belongRepository.findByIdAndRoomId(id, roomId);
        if (optionalBelong.isPresent()) {
            return optionalBelong.get().toDto();
        }
        return null;
    }

    public BelongDto findOne(Long memberId, Long roomId) {
        Optional<Belong> optionalBelong = belongRepository.findByMemberIdAndRoomId(memberId, roomId);
        if (optionalBelong.isEmpty())
            return null;
        Belong belong = optionalBelong.get();
        return belong.toDto();
    }

    public List<JoinDto> getJoins(Long memberId, Long roomId) {
        Optional<Belong> optionalBelong = belongRepository.findByMemberIdAndRoomId(memberId, roomId);
        if (optionalBelong.isEmpty())
            return null;
        Belong belong = optionalBelong.get();
        return belong.getJoins().stream().map(Join::toDto).collect(Collectors.toList());
    }
}
