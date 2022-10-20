package hongik.ce.jolup.module.member.endpoint.form;

import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationForm {
    private boolean competitionCreatedByWeb;
    private boolean matchUpdatedByWeb;

    protected NotificationForm(Member member) {
        this.competitionCreatedByWeb = member.isCompetitionCreatedByWeb();
        this.matchUpdatedByWeb = member.isMatchUpdatedByWeb();
    }

    public static NotificationForm from(Member member) {
        return new NotificationForm(member);
    }
}
