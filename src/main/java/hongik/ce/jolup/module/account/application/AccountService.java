package hongik.ce.jolup.module.account.application;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.competition.domain.entity.Participate;
import hongik.ce.jolup.module.competition.infra.repository.ParticipateRepository;
import hongik.ce.jolup.module.match.domain.entity.Match;
import hongik.ce.jolup.module.match.infra.repository.MatchRepository;
import hongik.ce.jolup.module.account.domain.UserAccount;
import hongik.ce.jolup.module.account.endpoint.form.NotificationForm;
import hongik.ce.jolup.module.account.endpoint.form.Profile;
import hongik.ce.jolup.module.account.endpoint.form.SignupForm;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JoinRepository joinRepository;
    private final ParticipateRepository participateRepository;
    private final MatchRepository matchRepository;
    private final PasswordEncoder passwordEncoder;

    public Account signup(SignupForm signupForm) {
        Account account = saveNewMember(signupForm);
        return account;
    }

    private Account saveNewMember(SignupForm signupForm) {
        Account account = Account.with(signupForm.getEmail(), signupForm.getName(), passwordEncoder.encode(signupForm.getPassword()));
        return accountRepository.save(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.updateProfile(profile);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.updatePassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, NotificationForm notificationForm) {
        account.updateNotification(notificationForm);
        accountRepository.save(account);
    }

    public void updateName(Account account, String name) {
        account.updateName(name);
        accountRepository.save(account);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserAccount(account),
                account.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetails 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserAccount(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findMemberWithFollowById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    public void remove(Account account) {
        List<Join> joins = joinRepository.findByAccount(account);
        for (Join join : joins) {
            join.updateMember(null);
        }
        List<Participate> participates = participateRepository.findByAccount(account);
        for (Participate participate : participates) {
            participate.updateMember(null);
        }
        List<Match> homeMatches = matchRepository.findMatchByHome(account);
        for (Match homeMatch : homeMatches) {
            homeMatch.updateHome(null);
        }
        List<Match> awayMatches = matchRepository.findMatchByAway(account);
        for (Match awayMatch : awayMatches) {
            awayMatch.updateAway(null);
        }
        accountRepository.delete(account);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}