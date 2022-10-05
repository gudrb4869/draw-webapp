package hongik.ce.jolup.module.notification.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.member.domain.entity.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id"})
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column
    private String title;
    @Column
    private String link;
    @Column
    private String message;
    @Column
    private boolean checked;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType notificationType;

    public static Notification from(String title, String link, String message, boolean checked, Member member, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.title = title;
        notification.link = link;
        notification.message = message;
        notification.checked = checked;
        notification.member = member;
        notification.notificationType = notificationType;
        return notification;
    }

    public void read() {
        this.checked = true;
    }
}
