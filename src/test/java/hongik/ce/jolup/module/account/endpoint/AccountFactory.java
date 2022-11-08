package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountFactory {
    @Autowired
    AccountRepository accountRepository;

    public Account createAccount(String email) {
        return accountRepository.save(Account.with(email, email + "123", "1234"));
    }
}