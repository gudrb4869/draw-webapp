package hongik.ce.jolup.domain.alarm;

import hongik.ce.jolup.domain.BaseTimeEntity;
import hongik.ce.jolup.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id"})
public class Alarm extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_member_id")
    private Member sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receive_member_id")
    private Member receiveMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type")
    private AlarmType alarmType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlarmStatus status;

    @Column(name = "request_id")
    private Long requestId;

    @Builder
    public Alarm(Member sendMember, Member receiveMember, AlarmType alarmType, AlarmStatus status, Long requestId) {
        this.sendMember = sendMember;
        this.receiveMember = receiveMember;
        this.alarmType = alarmType;
        this.status = status;
        this.requestId = requestId;
    }

    public void updateStatus(AlarmStatus status) {
        this.status = status;
    }
}
