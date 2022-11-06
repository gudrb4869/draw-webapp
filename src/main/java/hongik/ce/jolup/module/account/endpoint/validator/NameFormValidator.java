package hongik.ce.jolup.module.account.endpoint.validator;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.form.NameForm;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NameFormValidator implements Validator {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return NameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NameForm nameForm = (NameForm) target;
        if (accountRepository.existsByName(nameForm.getName())) {
            errors.rejectValue("name", "wrong.value", "이미 사용중인 이름입니다.");
        }
    }

    public void validatePassword(NameForm nameForm, Account account, Errors errors) {
        if (!passwordEncoder.matches(nameForm.getPassword(), account.getPassword())) {
            errors.rejectValue("password", "wrong.password", new Object[]{nameForm.getPassword()}, "비밀번호가 일치하지 않습니다.");
        }
    }
}
