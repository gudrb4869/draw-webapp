package hongik.ce.jolup.module.competition.endpoint.validator;

import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Set;

@Component
public class CompetitionFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return CompetitionForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CompetitionForm competitionForm = (CompetitionForm) target;
        Set<Long> members = competitionForm.getMembers();
        CompetitionType type = competitionForm.getType();
        if (members.size() < 2) {
            errors.rejectValue("members", "wrong.members", "적어도 2명은 참가해야합니다.");
            return;
        }

        if (type.equals(CompetitionType.LEAGUE) && members.size() > 24) {
            errors.rejectValue("type", "wrong.type", "리그는 최대 24명까지 참가가능합니다.");
        }
        else if (type.equals(CompetitionType.TOURNAMENT) && members.size() > 64) {
            errors.rejectValue("type", "wrong.type", "토너먼트는 최대 64명까지 참가가능합니다.");
        }
    }
}
