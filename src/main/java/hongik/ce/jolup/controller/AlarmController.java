package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.alarm.Alarm;
import hongik.ce.jolup.domain.alarm.AlarmStatus;
import hongik.ce.jolup.domain.alarm.AlarmType;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.service.AlarmService;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmService alarmService;
    private final BelongService belongService;
    private final JoinService joinService;

    @GetMapping
    public String alarmList(@AuthenticationPrincipal Member member, Model model) {
        List<Alarm> alarms = alarmService.findAlarms(member.getId());
        model.addAttribute("alarms", alarms);
        return "alarms/alarmList";
    }

    @PostMapping("/{id}/accept")
    public String accept(@AuthenticationPrincipal Member member, @PathVariable Long id) {
        Alarm alarm = alarmService.findOne(id, member.getId());
        if (alarm != null) {
            if (alarm.getAlarmType().equals(AlarmType.ROOM_INVITE)) {
                belongService.save(member.getId(), alarm.getRequestId(), BelongType.USER);
            } else if (alarm.getAlarmType().equals(AlarmType.COMPETITION_INVITE)) {
                joinService.save(member.getId(), alarm.getRequestId());
            }
            alarmService.updateStatus(id, AlarmStatus.AFTER);
        }
        return "redirect:/alarms";
    }

    @PostMapping("/{id}/reject")
    public String reject(@AuthenticationPrincipal Member member, @PathVariable Long id) {
        Alarm alarm = alarmService.findOne(id, member.getId());
        if (alarm != null) {
            alarmService.updateStatus(id, AlarmStatus.AFTER);
        }
        return "redirect:/alarms";
    }

    @PostMapping("/{id}")
    public String delete(@AuthenticationPrincipal Member member, @PathVariable Long id) {
        Alarm alarm = alarmService.findOne(id, member.getId());
        if (alarm != null) {
            alarmService.delete(id);
        }
        return "redirect:/alarms";
    }
}
