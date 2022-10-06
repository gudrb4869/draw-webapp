package hongik.ce.jolup.module.notification.endpoint;

import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.domain.entity.NotificationType;
import hongik.ce.jolup.module.room.application.JoinService;
import hongik.ce.jolup.module.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JoinService joinService;

    @GetMapping
    public String getNotifications(@AuthenticationPrincipal Member member, Model model) {
        List<Notification> notifications = notificationService.findByMemberAndCheckedOrderByCreatedDateDesc(member, false);
        long numberOfChecked = notificationService.countByMemberAndChecked(member, true);
        model.addAttribute("isNew", true);
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        notificationService.markAsRead(notifications);
        log.info("markAsRead Finished");
        return "notification/list";
    }

    @GetMapping("/old")
    public String getOldNotifications(@AuthenticationPrincipal Member member, Model model) {
        List<Notification> notifications = notificationService.findByMemberAndCheckedOrderByCreatedDateDesc(member, true);
        long numberOfNotChecked = notificationService.countByMemberAndChecked(member, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @DeleteMapping
    public String deleteNotifications(@AuthenticationPrincipal Member member, RedirectAttributes attributes) {
        notificationService.deleteByMemberAndChecked(member, true);
        attributes.addFlashAttribute("message", "알림을 삭제했습니다.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/accept")
    public String accept(@AuthenticationPrincipal Member member, @PathVariable Long id, RedirectAttributes attributes) {
        log.info("accept start");
        Notification notification = notificationService.findOne(id);
        if (notification.getNotificationType().equals(NotificationType.ROOM_INVITED)) {
            joinService.save(
                    Optional.ofNullable(notification.getMember()).map(Member::getId)
                            .orElseThrow(() -> new RuntimeException("예외가 발생했습니다.")),
                    Long.valueOf(notification.getLink()), Grade.USER);
            log.info("join room success");
        }
        log.info("accept Finished");
        attributes.addFlashAttribute("message", "방 참여 요청을 수락했습니다.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/reject")
    public String reject(@AuthenticationPrincipal Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Notification notification = notificationService.findOne(id);
        attributes.addFlashAttribute("message", "방 참여 요청을 거절했습니다.");
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        ArrayList<Notification> invitedRoomNotifications = new ArrayList<>();
        ArrayList<Notification> competitionCreatedNotifications = new ArrayList<>();
        ArrayList<Notification> matchUpdatedNotifications = new ArrayList<>();

        for (Notification notification : notifications) {
            switch (notification.getNotificationType()) {
                case ROOM_INVITED:
                    invitedRoomNotifications.add(notification);
                    break;
                case COMPETITION_CREATED:
                    competitionCreatedNotifications.add(notification);
                    break;
                case MATCH_UPDATED:
                    matchUpdatedNotifications.add(notification);
                    break;
            }
        }
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("invitedRoomNotifications", invitedRoomNotifications);
        model.addAttribute("competitionCreatedNotifications", competitionCreatedNotifications);
        model.addAttribute("matchUpdatedNotifications", matchUpdatedNotifications);
    }
}
