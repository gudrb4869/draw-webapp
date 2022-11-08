package hongik.ce.jolup.module.account.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.domain.entity.Follow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    @EntityGraph(attributePaths = {"following", "follower"})
    List<Follow> findByFollowing(Account following);

    @EntityGraph(attributePaths = {"following", "follower"})
    List<Follow> findByFollower(Account follower);

    void deleteByFollowingAndFollower(Account following, Account follower);

    boolean existsByFollowingAndFollower(Account following, Account follower);
}
