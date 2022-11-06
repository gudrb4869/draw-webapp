package hongik.ce.jolup.module.notification.domain.entity;

import hongik.ce.jolup.BaseTimeEntity;
import hongik.ce.jolup.module.account.domain.entity.Account;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    public static Notification from(String link, boolean checked, String message, Account account) {
        Notification notification = new Notification();
        notification.link = link;
        notification.checked = checked;
        notification.message = message;
        notification.account = account;
        return notification;
    }

    public void read() {
        this.checked = true;
    }
}
