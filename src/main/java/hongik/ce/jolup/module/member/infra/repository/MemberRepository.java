package hongik.ce.jolup.module.member.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Set<Member> findMembersByNameIn(List<String> names);

    Optional<Member> findMemberWithFollowById(Long id);
}
