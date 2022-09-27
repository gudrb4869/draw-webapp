package hongik.ce.jolup.domain.competition;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.notification.Notification;
import hongik.ce.jolup.domain.notification.NotificationType;
import hongik.ce.jolup.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Async
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class CompetitionEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleCompetitionCreatedEvent(CompetitionCreatedEvent competitionCreatedEvent) {
        Competition competition = competitionCreatedEvent.getCompetition();
        String message = competitionCreatedEvent.getMessage();
        Set<Member> members = competitionCreatedEvent.getMembers();
        for (Member member : members) {
            saveNotification(competition, message, member);
        }
        log.info(competition.getName() + " is created.");
    }

    private void saveNotification(Competition competition, String message, Member member) {
        notificationService.save(Notification.from(competition.getName(),
                "/rooms/" + competition.getRoom().getId() + "/competitions/" + competition.getId(),
                message, false, member, NotificationType.COMPETITION_CREATED));
    }
}
