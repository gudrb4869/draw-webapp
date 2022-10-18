package hongik.ce.jolup.module.member.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @EntityGraph(attributePaths = {"following", "follower"})
    List<Follow> findByFollowing(Member following);

    @EntityGraph(attributePaths = {"following", "follower"})
    List<Follow> findByFollower(Member follower);

    @Transactional
    void deleteByFollowingAndFollower(Member following, Member follower);

    boolean existsByFollowingAndFollower(Member following, Member follower);
}
