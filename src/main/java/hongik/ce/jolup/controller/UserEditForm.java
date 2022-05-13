package hongik.ce.jolup.controller;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder @ToString
public class UserEditForm {
    @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password_current;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다!")
//    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password_new;

    @NotBlank(message = "이름은 필수 입력 값입니다!")
    private String name;
}
