package hongik.ce.jolup.module.account.endpoint.validator;

import hongik.ce.jolup.module.account.endpoint.form.SignupForm;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm) target;
        if (accountRepository.existsByEmail(signupForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", "이미 사용중인 이메일입니다.");
        }
        if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "wrong.password", "비밀번호가 일치하지 않습니다.");
        }
        if (accountRepository.existsByName(signupForm.getName())) {
            errors.rejectValue("name", "invalid.name", "이미 사용중인 이름입니다.");
        }

    }
}
