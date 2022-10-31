package hongik.ce.jolup.module.member.endpoint.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PasswordForm {
    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String currentPassword;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "신규 비밀번호는 8~30자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    @Size(min = 8, max = 30, message = "신규 비밀번호는 8~30자로 설정하세요.")
    private String newPassword;

//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,30}", message = "비밀번호 확인을 입력하세요.")
    @Size(min = 8, max = 30, message = "비밀번호 확인은 8~30자로 설정하세요.")
    private String newPasswordConfirm;
}
