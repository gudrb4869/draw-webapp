package hongik.ce.jolup.module.account.endpoint.validator;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.form.PasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validateForm(PasswordForm passwordForm, Account account, Errors errors) {
        if (!passwordEncoder.matches(passwordForm.getCurrentPassword(), account.getPassword())) {
            errors.rejectValue("currentPassword", "wrong.currentPassword", new Object[]{passwordForm.getCurrentPassword()},
                    "현재 비밀번호와 일치하지 않습니다.");
        }
        if (passwordForm.getCurrentPassword().equals(passwordForm.getNewPassword())) {
            errors.rejectValue("newPassword", "wrong.newPassword", new Object[]{passwordForm.getNewPassword()},
                    "신규 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
        }
        if (!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPasswordConfirm", "wrong.newPasswordConfirm", new Object[]{passwordForm.getNewPasswordConfirm()},
                    "신규 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}
