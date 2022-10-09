package hongik.ce.jolup.module.member.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.match.domain.entity.Match;
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

    @Builder
    public Member(Long id, String email, String password, String name, String image) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.image = image;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void updateProfile(Profile profile) {
        this.image = profile.getImage();
    }
}
