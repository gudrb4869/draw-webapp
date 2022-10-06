package hongik.ce.jolup.module.room.endpoint.validator;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import hongik.ce.jolup.module.room.endpoint.form.InviteForm;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InviteFormValidator implements Validator {

    private final MemberRepository memberRepository;
    private final JoinRepository joinRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return InviteForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InviteForm inviteForm = (InviteForm) target;

        List<String> names = inviteForm.getNames();
        if (names.size() != inviteForm.getCount()) {
            errors.rejectValue("inviteForm", "wrong.count", "회원 수와 회원 개수가 일치하지 않습니다.");
        }

        Set<Member> members = memberRepository.findMembersByNameIn(names);

    }
}
