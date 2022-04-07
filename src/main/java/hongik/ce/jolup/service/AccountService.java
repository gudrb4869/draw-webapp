package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.accounts.AccountRepository;
import hongik.ce.jolup.web.dto.AccountSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public Long save(AccountSaveRequestDto requestDto) {
        return accountRepository.save(requestDto.toEntity()).getId();
    }
}