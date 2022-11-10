package hongik.ce.jolup.module.account.endpoint.form;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NameForm {
    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "이름 형식이 올바르지 않습니다.")
    private String name;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String password;

    public NameForm(String name) {
        this.name = name;
    }
}
