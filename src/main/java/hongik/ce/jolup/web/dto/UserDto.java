package hongik.ce.jolup.web.dto;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.user.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String name;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}
