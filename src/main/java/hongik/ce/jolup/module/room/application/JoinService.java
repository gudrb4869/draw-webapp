package hongik.ce.jolup.module.room.application;

import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JoinService {

    private final MemberRepository memberRepository;
    private final JoinRepository joinRepository;
    private final RoomRepository roomRepository;

    public Join save(Long memberId, Long roomId, Grade grade) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
        Join join = Join.builder().member(member).room(room).grade(grade).build();
        return joinRepository.save(join);
    }

    public Long update(Long id, Grade grade) {
        Join join = joinRepository.findById(id).orElse(null);
        if (join == null) {
            return null;
        }
        join.updateGrade(grade);
        return join.getId();
    }

    public void delete(Long joinId) {
        joinRepository.deleteById(joinId);
    }

    public void setNull(Long memberId) {
        List<Join> joins = joinRepository.findByMemberId(memberId);
        for (Join join : joins) {
            join.updateMember(null);
        }
    }

    public Join findByIdAndRoomId(Long id, Long roomId) {
        return joinRepository.findByIdAndRoomId(id, roomId).orElse(null);
    }

    public Join findOne(Long memberId, Long roomId) {
        return joinRepository.findByMemberIdAndRoomId(memberId, roomId).orElse(null);
    }

    public List<Join> findByRoomId(Long roomId) {
        return joinRepository.findByRoomId(roomId);
    }

    public Join addMember(Room room, Member member) {
        if (joinRepository.existsByRoomAndMember(room, member)) {
            throw new IllegalArgumentException("이미 방에 참여중입니다.");
        }
        return joinRepository.save(Join.builder().room(room).member(member).grade(Grade.USER).build());
    }

    public void removeMember(Room room, Member member) {
        Join join = joinRepository.findByRoomAndMember(room, member)
                .orElseThrow(() -> new IllegalArgumentException("방에 참여중인 회원이 아닙니다."));
        if (join.getGrade().equals(Grade.ADMIN)) {
            throw new IllegalArgumentException("관리자는 방을 나갈 수 없습니다. 설정에 들어가서 방을 삭제하세요.");
        }
        joinRepository.delete(join);
    }
}
