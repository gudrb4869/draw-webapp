package hongik.ce.jolup.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSaveRequestDto {

    private String email;
    private String password;
    private String name;
}
