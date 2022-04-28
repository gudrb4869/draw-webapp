package hongik.ce.jolup.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JoinForm {
    private List<@NotBlank(message = "형식이 올바르지 않습니다!") String> emails = new ArrayList<>();

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
