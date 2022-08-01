package hongik.ce.jolup.repository;

import hongik.ce.jolup.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllById(Iterable<Long> ids);

    List<Member> findByEmailIn(Collection<String> emails);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
