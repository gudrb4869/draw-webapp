/*
package hongik.ce.jolup.module.match.event;

import hongik.ce.jolup.module.competition.domain.entity.Participate;
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
import java.util.stream.Collectors;

@Async
@Transactional(readOnly = true)
@Component
@RequiredArgsConstructor
public class MatchEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleMatchEvent(MatchEvent matchEvent) {
        Match match = matchEvent.getMatch();
        String message = matchEvent.getMessage();
        List<Member> members = match.getCompetition().getParticipates().stream().map(Participate::getMember).collect(Collectors.toList());
        for (Member member : members) {
            saveNotification(match, message, member);
        }
    }

    private void saveNotification(Match match, String message, Member member) {
        notificationRepository.save(Notification.from(match.getHome().getName() + " : " + match.getAway().getName(),
                "/rooms/" + match.getCompetition().getRoom().getId() + "/competitions/" + match.getCompetition().getId(), false, message, member));
    }
}
*/
