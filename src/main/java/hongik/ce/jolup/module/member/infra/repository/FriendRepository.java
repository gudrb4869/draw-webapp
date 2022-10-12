package hongik.ce.jolup.module.member.infra.repository;

import hongik.ce.jolup.module.member.domain.entity.Friend;
import hongik.ce.jolup.module.member.domain.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @EntityGraph(attributePaths = {"following", "follower"})
    List<Friend> findByFollowing(Member following);

    @Transactional
    void deleteByFollowingAndFollower(Member following, Member follower);

    boolean existsByFollowingAndFollower(Member following, Member follower);
}
