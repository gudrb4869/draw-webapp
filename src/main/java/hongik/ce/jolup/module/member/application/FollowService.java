package hongik.ce.jolup.module.member.application;

import hongik.ce.jolup.module.member.domain.entity.Follow;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.event.FollowEvent;
import hongik.ce.jolup.module.member.infra.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final ApplicationEventPublisher publisher;

    public void createFollow(Member following, Member follower) {
        if (followRepository.existsByFollowingAndFollower(following, follower)) {
            throw new IllegalStateException("이미 팔로잉한 회원입니다.");
        }
        Follow follow = followRepository.save(Follow.from(following, follower));
        publisher.publishEvent(new FollowEvent(follow, following.getName() + "님이 회원님을 팔로우했습니다."));
    }

    public boolean isFollow(Member following, Member follower) {
        return followRepository.existsByFollowingAndFollower(following, follower);
    }

    public void deleteFriends(Member following, Member follower) {
        followRepository.deleteByFollowingAndFollower(following, follower);
    }
}
