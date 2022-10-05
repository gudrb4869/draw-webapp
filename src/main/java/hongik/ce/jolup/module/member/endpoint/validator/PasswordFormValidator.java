package hongik.ce.jolup.module.member.endpoint.validator;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.PasswordForm;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PasswordFormValidator implements Validator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        Member member = memberRepository.findByEmail(passwordForm.getEmail()).orElse(null);
        if (member == null) {
            errors.rejectValue("email", "invalid.email", new Object[]{passwordForm.getEmail()},
                    "존재하지 않는 아이디입니다.");
            return;
        }
        if (!passwordEncoder.matches(passwordForm.getPassword_current(), member.getPassword())) {
            errors.rejectValue("password_current", "wrong.password_current", new Object[]{passwordForm.getPassword_current()},
                    "현재 비밀번호와 일치하지 않습니다.");
        }
        if (passwordForm.getPassword_current().equals(passwordForm.getPassword_new())) {
            errors.rejectValue("password_new", "wrong.password_new", new Object[]{passwordForm.getPassword_new()},
                    "신규 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
        }
        if (!passwordForm.getPassword_new().equals(passwordForm.getPassword_confirm())) {
            errors.rejectValue("password_confirm", "wrong.password_confirm", new Object[]{passwordForm.getPassword_confirm()},
                    "신규 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}
