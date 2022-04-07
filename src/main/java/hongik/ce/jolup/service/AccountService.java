package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.accounts.Account;
import hongik.ce.jolup.domain.accounts.AccountRepository;
import hongik.ce.jolup.domain.accounts.AccountRole;
import hongik.ce.jolup.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetail 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));
    }

    @Transactional
    public Long save(AccountSaveRequestDto requestDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        requestDto.setPassword(encoder.encode(requestDto.getPassword()));

        return accountRepository.save(Account.builder()
                .userId(requestDto.getUserId())
                .name(requestDto.getName())
                .password(requestDto.getPassword())
                .role(AccountRole.USER).build()).getId();
    }
}