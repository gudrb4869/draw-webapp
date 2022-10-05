package hongik.ce.jolup.module.member.endpoint.form;

import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    @NotBlank(message = "아이디는 필수 입력 값입니다!")
    private String email;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$", message = "이름은 2~12자 한글, 영문 대소문자, 숫자를 사용하세요.")
    private String name;

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String password_current;

    private String image;

    public static Profile from(Member member) {
        return new Profile(member);
    }

    protected Profile(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
        this.image = member.getImage();
    }
}