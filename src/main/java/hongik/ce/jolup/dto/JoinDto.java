package hongik.ce.jolup.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class JoinDto {
    private List<String> emails = new ArrayList<>();

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
