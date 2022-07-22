package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.dto.JoinDto;
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
    public void saveMembers(Long roomId, BelongType type, List<String> emails) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        List<Member> members = memberRepository.findByEmailIn(emails);
        if (optionalRoom.isEmpty() || members.size() < emails.size()) {
            return;
        }
        Room room = optionalRoom.get();
        for (Member member : members) {
            Belong belong = Belong.builder().member(member).room(room).belongType(type).build();
            belongRepository.save(belong);
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

    public List<JoinDto> getJoins(Long memberId, Long roomId) {
        Optional<Belong> optionalBelong = belongRepository.findByMemberIdAndRoomId(memberId, roomId);
        if (optionalBelong.isEmpty())
            return null;
        Belong belong = optionalBelong.get();
        return belong.getJoins().stream().map(Join::toDto).collect(Collectors.toList());
    }

    public List<BelongDto> findByRoomId(Long roomId) {
        List<Belong> belongs = belongRepository.findByRoomId(roomId);
        List<BelongDto> belongDtos = belongs.stream().map(Belong::toDto).collect(Collectors.toList());
        return belongDtos;
    }
}
