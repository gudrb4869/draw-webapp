package hongik.ce.jolup.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
public class JoinRoomDto {
    private List<String> emails;

    public JoinRoomDto() {
        emails = new ArrayList<>();
    }

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
