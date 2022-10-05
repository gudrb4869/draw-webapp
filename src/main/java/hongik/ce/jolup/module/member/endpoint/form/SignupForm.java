package hongik.ce.jolup.module.member.endpoint.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class SignupForm {
//        @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9_]{3,19}$", message = "아이디는 4~20자 영문 대소문자, 숫자를 사용하세요.")
    private String email;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "비밀번호는 8~30자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8~30자로 설정하세요.")
    private String password;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "비밀번호는 8~30자 영문 대소문자, 숫자, 특수문자를 사용하세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8~30자로 설정하세요.")
    private String password_confirm;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "이름은 2~12자 한글, 영문 대소문자, 숫자를 사용하세요.")
    private String name;
}
