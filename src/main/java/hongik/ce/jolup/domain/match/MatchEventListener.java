package hongik.ce.jolup.domain.match;

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
public class MatchEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleMatchUpdatedEvent(MatchUpdatedEvent matchUpdatedEvent) {
        Match match = matchUpdatedEvent.getMatch();
        Long roomId = matchUpdatedEvent.getRoomId();
        String message = matchUpdatedEvent.getMessage();
        Set<Member> members = matchUpdatedEvent.getMembers();
        log.info("match = {}", match);
        for (Member member : members) {
            saveNotification(match, roomId, message, member);
        }
        log.info(match.toString() + "is updated");
    }

    private void saveNotification(Match match, Long roomId, String message, Member member) {
        notificationService.save(Notification.from(match.getHome().getName() + " : " + match.getAway().getName(),
                "/rooms/" + roomId + "/competitions/" + match.getCompetition().getId(),
                message, false, member, NotificationType.MATCH_UPDATED));
    }


}
