package hongik.ce.jolup.module.competition.event;

import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Async
@Transactional
@Component
@RequiredArgsConstructor
public class CompetitionEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleCompetitionEvent(CompetitionEvent competitionEvent) {
        Competition competition = competitionEvent.getCompetition();
        Room room = competition.getRoom();
        List<Member> members = room.getJoins().stream().map(Join::getMember)
                .collect(Collectors.toList());
        for (Member member : members) {
            saveNotification(competitionEvent, member, room, competition);
        }
    }

    private void saveNotification(CompetitionEvent competitionEvent, Member member, Room room, Competition competition) {
        notificationRepository.save(Notification.from(room.getTitle() + " / " + competition.getTitle(),
                "/rooms/" + room.getId() + "/competitions/" + competition.getId(), false,
                competitionEvent.getMessage(), member));
    }
}
