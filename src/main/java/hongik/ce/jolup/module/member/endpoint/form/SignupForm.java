package hongik.ce.jolup.module.member.endpoint.form;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SignupForm {
//        @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Pattern(regexp = "^[a-z0-9]{4,20}$")
    private String email;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "비밀번호는 8~30자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
    @Size(min = 4, max = 30)
    private String password;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "비밀번호는 8~30자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
    @Size(min = 4, max = 30)
    private String password_confirm;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9_-]{2,12}$")
    private String name;
}
