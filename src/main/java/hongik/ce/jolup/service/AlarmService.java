package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.alarm.Alarm;
import hongik.ce.jolup.domain.alarm.AlarmStatus;
import hongik.ce.jolup.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public Long save(Alarm notification) {
        return alarmRepository.save(notification).getId();
    }

    @Transactional
    public void updateStatus(Long id, AlarmStatus status) {
        Alarm alarm = alarmRepository.findById(id).orElse(null);
        if (alarm != null) {
            alarm.updateStatus(status);
        }
    }
    @Transactional
    public void delete(Long id) {
        alarmRepository.deleteById(id);
    }

    public Alarm findOne(Long id, Long memberId) {
        return alarmRepository.findByIdAndReceiveMemberId(id, memberId).orElse(null);
    }

    public Alarm findOne2(Long memberId, Long requestId) {
        return alarmRepository.findByReceiveMemberIdAndRequestId(memberId, requestId).orElse(null);
    }

    public List<Alarm> findAlarms(Long memberId) {
        return alarmRepository.findByReceiveMemberId(memberId);
    }
}
