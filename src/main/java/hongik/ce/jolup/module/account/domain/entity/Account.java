package hongik.ce.jolup.module.account.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.account.endpoint.form.NotificationForm;
import hongik.ce.jolup.module.account.endpoint.form.Profile;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Account extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL) @ToString.Exclude
    private List<Follow> followings;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL) @ToString.Exclude
    private List<Follow> followers;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL) @ToString.Exclude
    private List<Notification> notifications = new ArrayList<>();

    private boolean competitionCreatedByWeb = true;
    private boolean matchUpdatedByWeb = true;

    public static Account with(String email, String name, String password) {
        Account account = new Account();
        account.email = email;
        account.name = name;
        account.password = password;
        return account;
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

    public boolean isFollowing(Account account) {
        return this.followings.stream()
                .anyMatch(f -> f.getFollower().equals(account));
    }

    public void updateNotification(NotificationForm notificationForm) {
        this.competitionCreatedByWeb = notificationForm.isCompetitionCreatedByWeb();
        this.matchUpdatedByWeb = notificationForm.isMatchUpdatedByWeb();
    }
}
