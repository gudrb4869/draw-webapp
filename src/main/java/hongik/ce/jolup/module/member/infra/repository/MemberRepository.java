package hongik.ce.jolup.module.member.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllById(Iterable<Long> longs);

    Set<Member> findByEmailIn(Collection<String> emails);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByName(String name);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}
