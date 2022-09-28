package hongik.ce.jolup.validator;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.Profile;
import hongik.ce.jolup.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ProfileValidator implements Validator {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Profile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Profile profile = (Profile) target;
        Member member = memberRepository.findByEmail(profile.getEmail()).orElse(null);
        if (member == null) {
            errors.rejectValue("email", "invalid.email", new Object[]{profile.getEmail()},
                    "존재하지 않는 아이디입니다.");
            return;
        }
        if (!member.getName().equals(profile.getName()) && memberRepository.existsByName(profile.getName())) {
            errors.rejectValue("name", "invalid.name", new Object[]{profile.getName()},
                    "이미 사용중인 이름입니다.");
        }
        if (!passwordEncoder.matches(profile.getPassword_current(), member.getPassword())) {
            errors.rejectValue("password_current", "wrong.password_current", new Object[]{profile.getPassword_current()},
                    "현재 비밀번호와 일치하지 않습니다.");
        }
    }
}
