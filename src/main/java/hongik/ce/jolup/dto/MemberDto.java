package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(of = {"id", "email", "name", "role"})
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Role role;

    @Builder
    public MemberDto(Long id, String email, String password, String name, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
    }
}
