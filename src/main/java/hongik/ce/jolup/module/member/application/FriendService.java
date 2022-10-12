package hongik.ce.jolup.module.member.application;

import hongik.ce.jolup.module.member.domain.entity.Friend;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public void createFriends(Member following, Member follower) {
        if (friendRepository.existsByFollowingAndFollower(following, follower)) {
            throw new IllegalStateException("이미 팔로잉한 회원입니다.");
        }
        friendRepository.save(Friend.from(following, follower));
    }

    public boolean isFollow(Member following, Member follower) {
        return friendRepository.existsByFollowingAndFollower(following, follower);
    }

    public void deleteFriends(Member following, Member follower) {
        friendRepository.deleteByFollowingAndFollower(following, follower);
    }
}
