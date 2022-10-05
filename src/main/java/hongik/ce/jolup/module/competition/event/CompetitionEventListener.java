package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.domain.entity.NotificationType;
import hongik.ce.jolup.module.notification.application.NotificationService;
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
