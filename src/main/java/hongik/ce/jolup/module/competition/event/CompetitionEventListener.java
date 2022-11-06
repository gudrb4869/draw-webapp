package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Async
@Transactional
@Component
@RequiredArgsConstructor
public class CompetitionEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleCompetitionCreatedEvent(CompetitionCreatedEvent competitionCreatedEvent) {
        Competition competition = competitionCreatedEvent.getCompetition();
        String message = competitionCreatedEvent.getMessage();
        List<Account> accounts = competitionCreatedEvent.getAccounts();
        accounts.forEach(member -> {
            if (member.isCompetitionCreatedByWeb()) {
                saveNotification(member, competition, message);
            }
        });
    }

    private void saveNotification(Account account, Competition competition, String message) {
        notificationRepository.save(Notification.from(
                "/rooms/" + competition.getRoom().getId() + "/competitions/" + competition.getId(), false,
                message, account));
    }
}
