package hongik.ce.jolup.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class JoinForm {
    private List<String> emails = new ArrayList<>();

    public void addEmail(String email) {
        this.emails.add(email);
    }
}
