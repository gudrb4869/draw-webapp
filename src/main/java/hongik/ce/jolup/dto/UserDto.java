package hongik.ce.jolup.dto;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.user.UserRole;
import lombok.*;

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

    public User toEntity() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}
