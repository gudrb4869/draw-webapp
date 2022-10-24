package hongik.ce.jolup.module.notification.endpoint;

import hongik.ce.jolup.module.member.support.CurrentMember;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentMember Member member, Model model) {
        List<Notification> notificationList = notificationRepository.findByMemberOrderByCreatedDateDesc(member);
        long numberOfNotChecked = notificationRepository.countByMemberAndChecked(member, false);
        model.addAttribute("notificationList", notificationList);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        return "notification/list";
    }

    @GetMapping("/notifications/{id}")
    public String viewNotification(@CurrentMember Member member, @PathVariable Long id) {
        Notification notification = notificationService.getNotification(member, id);
        notificationService.read(notification);
        return "redirect:" + notification.getLink();
    }

    @GetMapping("/notifications/all-read")
    public String allRead(@CurrentMember Member member, RedirectAttributes attributes) {
        List<Notification> notifications = notificationRepository.findByMemberAndCheckedOrderByCreatedDateDesc(member, false);
        notificationService.markAsRead(notifications);
        attributes.addFlashAttribute("message", "읽지 않은 알림들을 읽음 처리 했습니다.");
        return "redirect:/notifications";
    }

    @DeleteMapping("/notifications/{id}")
    public String deleteNotifications(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        notificationService.deleteByMemberAndChecked(member, true);
        attributes.addFlashAttribute("message", "알림을 삭제했습니다.");
        return "redirect:/notifications";
    }

    /*@PostMapping("/{id}/accept")
    public String accept(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Notification notification = notificationService.findOne(id);
        if (notification.getNotificationType().equals(NotificationType.ROOM_INVITED)) {
            joinService.save(
                    Optional.ofNullable(notification.getMember()).map(Member::getId)
                            .orElseThrow(() -> new RuntimeException("예외가 발생했습니다.")),
                    Long.valueOf(notification.getLink()), Grade.USER);
        }
        attributes.addFlashAttribute("message", "모임 참여 요청을 수락했습니다.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/reject")
    public String reject(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Notification notification = notificationService.findOne(id);
        attributes.addFlashAttribute("message", "모임 참여 요청을 거절했습니다.");
        return "redirect:/notifications";
    }*/
}
