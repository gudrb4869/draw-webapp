package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.JoinDto;
import hongik.ce.jolup.dto.MemberDto;
import hongik.ce.jolup.dto.RoomDto;
import hongik.ce.jolup.repository.BelongRepository;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.repository.RoomRepository;
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

    private final MemberRepository memberRepository;
    private final BelongRepository belongRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Long save(Long memberId, Long roomId, BelongType type) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalMember.isEmpty() || optionalRoom.isEmpty()) {
            return null;
        }

        Member member = optionalMember.get();
        Room room = optionalRoom.get();
        Belong belong = Belong.builder().member(member).room(room).belongType(type).build();
        return belongRepository.save(belong).getId();
    }

    @Transactional
    public void saveMembers(Long roomId, BelongType type, List<MemberDto> memberDtos) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty()) {
            return;
        }
        Room room = optionalRoom.get();
        RoomDto roomDto = room.toDto();
        for (MemberDto memberDto : memberDtos) {
            BelongDto belongDto = BelongDto.builder().memberDto(memberDto).roomDto(roomDto).belongType(type).build();
            belongRepository.save(belongDto.toEntity());
        }
    }

    @Transactional
    public void update(Long memberId, Long roomId, BelongType type) {
        Optional<Belong> optionalBelong = belongRepository.findByMemberIdAndRoomId(memberId, roomId);
        if (optionalBelong.isEmpty()) {
            return;
        }
        Belong belong = optionalBelong.get();
        belong.updateType(type);
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

    public List<BelongDto> findByRoomId(Long roomId) {
        List<Belong> belongs = belongRepository.findByRoomId(roomId);
        List<BelongDto> belongDtos = belongs.stream().map(Belong::toDto).collect(Collectors.toList());
        return belongDtos;
    }
}
