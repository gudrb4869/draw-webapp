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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JoinRepository joinRepository;
    private final ParticipateRepository participateRepository;
    private final MatchRepository matchRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.path}")
    private String filePath;

    public Account signup(SignupForm signupForm) {
        Account account = saveNewMember(signupForm);
        return account;
    }

    private Account saveNewMember(SignupForm signupForm) {
        Account account = Account.with(signupForm.getEmail(), signupForm.getName(), passwordEncoder.encode(signupForm.getPassword()));
        return accountRepository.save(account);
    }

    public void updateProfile(Account account, Profile profile) {
        String base64String = profile.getImage();
        String bio = profile.getBio();
        String image = null;
        System.out.println("base64String = " + base64String);
        if (base64String != null && !base64String.isEmpty()) {
            String[] strings = base64String.split(",");
            System.out.println("logic start");
            String extension;
            switch (strings[0]) {
                case "data:image/jpeg;base64":
                    extension = "jpeg";
                    break;
                case "data:image/png;base64":
                    extension = "png";
                    break;
                default:
                    extension = "jpg";
                    break;
            }
            // convert base64 string to binary data
            byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
            image = UUID.randomUUID() + "." + extension;
            String path = getFullPath(image);
            File file = new File(path);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        account.updateProfile(profile);
        account.setImage(image);
        account.setBio(bio);
        accountRepository.save(account);
    }

    public String getFullPath(String filename) {
        return filePath + filename;
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