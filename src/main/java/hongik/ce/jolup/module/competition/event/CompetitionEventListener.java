package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.room.domain.entity.Room;
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
        List<Member> members = competitionCreatedEvent.getMembers();
        members.forEach(member -> {
            if (member.isCompetitionCreatedByWeb()) {
                saveNotification(member, competition, message);
            }
        });
    }

    private void saveNotification(Member member, Competition competition, String message) {
        notificationRepository.save(Notification.from(
                "/rooms/" + competition.getRoom().getId() + "/competitions/" + competition.getId(), false,
                message, member));
    }
}
