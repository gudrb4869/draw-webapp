package hongik.ce.jolup.module.account.endpoint.form;

import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    private MultipartFile file;

    @Size(max = 30, message = "30자를 초과하였습니다.")
    private String bio;

    @NotBlank(message = "현재 비밀번호를 입력하세요.")
    private String password;

    public static Profile from(Account account) {
        return new Profile(account);
    }

    protected Profile(Account account) {
        this.bio = account.getBio();
    }
}