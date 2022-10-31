package hongik.ce.jolup.module.member.endpoint.validator;

import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupFormValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupForm signupForm = (SignupForm) target;
        if (memberRepository.existsByEmail(signupForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", "이미 사용중인 이메일입니다.");
        }
        if (!signupForm.getPassword().equals(signupForm.getPassword_confirm())) {
            errors.rejectValue("password_confirm", "wrong.password", "비밀번호가 일치하지 않습니다.");
        }
        if (memberRepository.existsByName(signupForm.getName())) {
            errors.rejectValue("name", "invalid.name", "이미 사용중인 이름입니다.");
        }

    }
}
