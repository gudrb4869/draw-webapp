package hongik.ce.jolup.module.account.infra.repository;

import hongik.ce.jolup.module.account.domain.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

    Optional<Account> findMemberWithFollowById(Long id);

    Page<Account> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    @EntityGraph("Account.withJoinsAndRoom")
    Optional<Account> findWithJoinsAndRoomById(Long id);
}
