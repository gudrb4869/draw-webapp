package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString(of = {"id", "email", "name", "role"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Role role;

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
