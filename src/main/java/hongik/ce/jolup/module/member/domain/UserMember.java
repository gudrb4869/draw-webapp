package hongik.ce.jolup.module.member.domain;

import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserMember extends User {

    private final Member member;

    public UserMember(Member member) {
        super(member.getName(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }
}
