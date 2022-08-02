package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.dto.BelongDto;
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
    public void saveBelongs(Long roomId, BelongType type, List<Member> members) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isEmpty()) {
            return;
        }
        Room room = optionalRoom.get();
        for (Member member : members) {
            Belong belong = Belong.builder().member(member).room(room).belongType(type).build();
            belongRepository.save(belong);
        }
    }

    @Transactional
    public Long update(Long id, BelongType type) {
        Belong belong = belongRepository.findById(id).orElse(null);
        if (belong == null) {
            return null;
        }
        belong.updateType(type);
        return belong.getId();
    }

    @Transactional
    public void deleteBelong(Long belongId) {
        belongRepository.deleteById(belongId);
    }

    public List<Belong> findByMemberId(Long memberId) {
        return belongRepository.findByMemberId(memberId);
    }

    public Belong findByIdAndRoomId(Long id, Long roomId) {
        return belongRepository.findByIdAndRoomId(id, roomId).orElse(null);
    }

    public Belong findOne(Long memberId, Long roomId) {
        return belongRepository.findByMemberIdAndRoomId(memberId, roomId).orElse(null);
    }

    public List<Belong> findByRoomId(Long roomId) {
        return belongRepository.findByRoomId(roomId);
    }
}
