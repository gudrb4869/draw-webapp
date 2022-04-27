package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.join.Join;
import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.user.UserRole;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
    private List<JoinDto> joinDtos;

    public User toEntity() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .joins(joinDtos.stream().map(JoinDto::toEntity).collect(Collectors.toList()))
                .build();
    }
}
