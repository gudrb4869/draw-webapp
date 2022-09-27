package hongik.ce.jolup.domain.member;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.competition.LeagueGame;
import hongik.ce.jolup.domain.competition.LeagueTable;
import hongik.ce.jolup.domain.notification.Notification;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "email", "name", "role"})
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Member extends BaseTimeEntity implements UserDetails {

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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Join> joins = new ArrayList<>();

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL)
    private List<LeagueGame> homeLeagueGames = new ArrayList<>();

    @OneToMany(mappedBy = "away", cascade = CascadeType.ALL)
    private List<LeagueGame> awayLeagueGames = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<LeagueTable> leagueTables = new ArrayList<>();

    @OneToMany(mappedBy = "home", cascade = CascadeType.ALL)
    private List<LeagueGame> homeSingleLegGames = new ArrayList<>();

    @OneToMany(mappedBy = "away", cascade = CascadeType.ALL)
    private List<LeagueGame> awaySingleLegGames = new ArrayList<>();

    @Builder
    public Member(Long id, String email, String password, String name, String image, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.image = image;
        this.role = role;
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

    public void updateRole(Role role) {
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    // 필수 override 메소드를 구현
    // 사용자의 권한이 ,로 구분되어 있는 auth를 활용, 콜렉션 형태로 반환시킴
    // 단, 자료형은 GrantedAuthority를 구현해야 함
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(role.getKey()));
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료되지 않음
    }

    // 계정 잠김 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠기지 않음
    }

    // 비밀번호 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 만료되지 않음
    }

    // 계정의 활성화 여부 반환
    @Override
    public boolean isEnabled() {
        return true; // 활성화 됨
    }
}
