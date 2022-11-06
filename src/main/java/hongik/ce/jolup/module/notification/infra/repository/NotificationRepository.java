package hongik.ce.jolup.module.notification.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByAccountAndChecked(Account account, boolean checked);

    @Transactional
    List<Notification> findByAccountAndCheckedOrderByCreatedDateDesc(Account account, boolean b);

    @Transactional
    void deleteByAccountAndChecked(Account account, boolean b);

    @Transactional
    List<Notification> findByAccountOrderByCreatedDateDesc(Account account);

    @Transactional
    List<Notification> findFirst5ByAccountOrderByCreatedDateDesc(Account account);
}
