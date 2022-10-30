package hongik.ce.jolup.module.competition.infra.repository;

import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipateRepository extends JpaRepository<Participate, Long>, ParticipateRepositoryCustom {
    @EntityGraph(attributePaths = {"member"})
    List<Participate> findByMember(Member member);
}
