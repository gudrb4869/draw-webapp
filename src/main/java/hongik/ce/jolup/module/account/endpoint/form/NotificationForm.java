package hongik.ce.jolup.module.account.endpoint.form;

import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationForm {
    private boolean competitionCreatedByWeb;
    private boolean matchUpdatedByWeb;

    protected NotificationForm(Account account) {
        this.competitionCreatedByWeb = account.isCompetitionCreatedByWeb();
        this.matchUpdatedByWeb = account.isMatchUpdatedByWeb();
    }

    public static NotificationForm from(Account account) {
        return new NotificationForm(account);
    }
}
