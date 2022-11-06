package hongik.ce.jolup.module.account.domain;

import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {

    private final Account account;

    public UserAccount(Account account) {
        super(account.getName(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
