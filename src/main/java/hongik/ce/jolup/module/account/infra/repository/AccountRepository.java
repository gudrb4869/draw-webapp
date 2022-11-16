package hongik.ce.jolup.module.account.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<Account> findMemberWithFollowById(Long id);

    Page<Account> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
