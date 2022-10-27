package hongik.ce.jolup.module.member.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<Member> findMemberWithFollowById(Long id);

    Page<Member> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @EntityGraph("Member.withJoinsAndRoom")
    Optional<Member> findWithJoinsAndRoomById(Long id);
}
