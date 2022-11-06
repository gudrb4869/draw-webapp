package hongik.ce.jolup.module.account.endpoint.validator;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.form.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ProfileValidator implements Validator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return Profile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validatePassword(Profile profile, Account account, Errors errors) {
        if (!passwordEncoder.matches(profile.getPassword(), account.getPassword())) {
            errors.rejectValue("password", "wrong.password", new Object[]{profile.getPassword()},
                    "현재 비밀번호와 일치하지 않습니다.");
        }
    }
}
