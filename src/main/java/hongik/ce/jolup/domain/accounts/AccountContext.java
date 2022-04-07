/*
package hongik.ce.jolup.domain.accounts;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AccountContext extends User {
    private final Account account;

    public AccountContext(Account account) {
        super(account.getUsername(), account.getPassword(), getAuthorities(account.getRole()));
        this.account = account;
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(AccountRole role) {
        return Collections.singleton(new SimpleGrantedAuthority(role.getKey()));
    }
}
*/
