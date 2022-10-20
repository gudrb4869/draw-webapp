package hongik.ce.jolup.module.member.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.endpoint.form.NotificationForm;
import hongik.ce.jolup.module.member.endpoint.form.Profile;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "email", "name"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    private String bio;

    @OneToMany(mappedBy = "following")
    private List<Follow> followings;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followers;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Participate> participates = new ArrayList<>();

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL)
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "away", cascade = CascadeType.ALL)
    private List<Match> awayMatches = new ArrayList<>();

    private boolean competitionCreatedByWeb = true;
    private boolean matchUpdatedByWeb = true;

    @Builder
    public Member(Long id, String email, String password, String name, String image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.image = image;
    }

    public static Member with(String email, String name, String password) {
        Member member = new Member();
        member.email = email;
        member.name = name;
        member.password = password;
        return member;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateProfile(Profile profile) {
        this.image = profile.getImage();
        this.bio = profile.getBio();
    }

    public boolean isFollowing(Member member) {
        return this.followings.stream()
                .anyMatch(f -> f.getFollower().equals(member));
    }

    public void updateNotification(NotificationForm notificationForm) {
        this.competitionCreatedByWeb = notificationForm.isCompetitionCreatedByWeb();
        this.matchUpdatedByWeb = notificationForm.isMatchUpdatedByWeb();
    }
}
