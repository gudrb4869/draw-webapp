package hongik.ce.jolup.module.notification.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountAndCheckedOrderByCreatedDateDesc(Account account, boolean checked);

    void deleteByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountOrderByCreatedDateDesc(Account account);

    List<Notification> findFirst5ByAccountAndCheckedOrderByCreatedDateDesc(Account account, boolean checked);
}
