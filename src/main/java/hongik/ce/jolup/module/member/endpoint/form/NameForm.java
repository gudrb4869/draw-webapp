package hongik.ce.jolup.module.member.endpoint.form;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NameForm {
    @NotBlank
    @Length(min = 2, max = 12)
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,12}$")
    private String name;

    @NotBlank
    private String password;

    public NameForm(String name) {
        this.name = name;
    }
}
