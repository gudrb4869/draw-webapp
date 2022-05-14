package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.MemberAuth;
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
    private MemberAuth auth;
    private List<JoinDto> joinDtos;

    @Builder
    public MemberDto(Long id, String email, String password, String name, MemberAuth auth) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.auth = auth;
    }

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .auth(auth)
//                .joins(joinDtos.stream().map(JoinDto::toEntity).collect(Collectors.toList()))
                .build();
    }
}
