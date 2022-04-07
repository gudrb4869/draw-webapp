package hongik.ce.jolup.config.security.formlogin;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.accounts.AccountContext;
import hongik.ce.jolup.domain.accounts.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username 이 존재하지 않습니다: " + username));
        return new AccountContext(account);
    }
}
