package hongik.ce.jolup.module.match.event;

import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.member.domain.entity.Member;
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
public class MatchEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleMatchUpdatedEvent(MatchUpdatedEvent matchUpdatedEvent) {
        Match match = matchUpdatedEvent.getMatch();
        String message = matchUpdatedEvent.getMessage();
        List<Member> members = matchUpdatedEvent.getMembers();
        members.forEach(member -> {
            if (member.isMatchUpdatedByWeb()) {
                saveNotification(match, message, member);
            }
        });
    }

    private void saveNotification(Match match, String message, Member member) {
        notificationRepository.save(Notification.from("/rooms/" + match.getCompetition().getRoom().getId() + "/competitions/" + match.getCompetition().getId() + "/matches/" + match.getId(),
                false, message, member));
    }
}
