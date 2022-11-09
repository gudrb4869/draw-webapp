package hongik.ce.jolup.module.notification.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.support.CurrentUser;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
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
    public String getNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notificationList = notificationRepository.findByAccountOrderByCreatedDateDesc(account);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        model.addAttribute(account);
        model.addAttribute("notificationList", notificationList);
        model.addAttribute("numberOfChecked", notificationList.size() - numberOfNotChecked);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        return "notification/list";
    }

    @GetMapping("/notifications/{id}")
    public String viewNotification(@CurrentUser Account account, @PathVariable Long id) {
        Notification notification = notificationService.getNotification(account, id);
        notificationService.read(notification);
        return "redirect:" + notification.getLink();
    }

    @GetMapping("/notifications/all-read")
    public String allRead(@CurrentUser Account account, RedirectAttributes attributes) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedDateDesc(account, false);
        notificationService.markAsRead(notifications);
        attributes.addFlashAttribute("message", "읽지 않은 알림들을 읽음 처리 했습니다.");
        return "redirect:/notifications";
    }

    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentUser Account account, RedirectAttributes attributes) {
        notificationService.deleteByMemberAndChecked(account, true);
        attributes.addFlashAttribute("message", "읽은 알림을 모두 삭제했습니다.");
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
        attributes.addFlashAttribute("message", "방 참여 요청을 수락했습니다.");
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/reject")
    public String reject(@CurrentMember Member member, @PathVariable Long id, RedirectAttributes attributes) {
        Notification notification = notificationService.findOne(id);
        attributes.addFlashAttribute("message", "방 참여 요청을 거절했습니다.");
        return "redirect:/notifications";
    }*/
}
