package hongik.ce.jolup.validator;

import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.repository.MemberRepository;
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
            errors.rejectValue("email", "invalid.email", new Object[]{signupForm.getEmail()},
                    "이미 사용중인 이메일입니다.");
        }
        if (memberRepository.existsByName(signupForm.getName())) {
            errors.rejectValue("name", "invalid.name", new Object[]{signupForm.getName()},
                    "이미 사용중인 이름입니다.");
        }

    }
}
