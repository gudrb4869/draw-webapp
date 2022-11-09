package hongik.ce.jolup.module.account.application;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.domain.entity.Follow;
import hongik.ce.jolup.module.account.event.FollowEvent;
import hongik.ce.jolup.module.account.infra.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final ApplicationEventPublisher publisher;

    public void createFollow(Account following, Account follower) {
        if (followRepository.existsByFollowingAndFollower(following, follower)) {
            throw new IllegalStateException("이미 팔로잉한 회원입니다.");
        }
        Follow follow = followRepository.save(Follow.from(following, follower));
        log.info("팔로우 알림 이벤트 시작 전");
        publisher.publishEvent(new FollowEvent(follow, following.getName() + "님이 회원님을 팔로우했습니다."));
    }

    public boolean isFollow(Account following, Account follower) {
        return followRepository.existsByFollowingAndFollower(following, follower);
    }

    public void deleteFriends(Account following, Account follower) {
        followRepository.deleteByFollowingAndFollower(following, follower);
    }
}
