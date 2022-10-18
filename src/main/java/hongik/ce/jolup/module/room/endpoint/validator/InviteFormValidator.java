package hongik.ce.jolup.module.room.endpoint.validator;

import hongik.ce.jolup.module.room.endpoint.form.InviteForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class InviteFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return InviteForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InviteForm inviteForm = (InviteForm) target;

        Set<Long> members = inviteForm.getMembers();
        if (members.size() < 1 || members.size() > 100) {
            errors.rejectValue("members", "wrong.members", "최소 1명부터 최대 100명까지 초대가능합니다.");
        }
    }
}
