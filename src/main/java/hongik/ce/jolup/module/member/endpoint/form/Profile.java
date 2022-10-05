package hongik.ce.jolup.module.member.endpoint.form;

import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String password;

    private String image;

    public static Profile from(Member member) {
        return new Profile(member);
    }

    protected Profile(Member member) {
        this.image = member.getImage();
    }
}