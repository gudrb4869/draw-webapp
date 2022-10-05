package hongik.ce.jolup.module.member.application;

import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.Profile;
import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.member.infra.repository.MemberRepository;
import hongik.ce.jolup.module.member.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signup(SignupForm signupForm) {
        Member member = Member.builder().email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .name(signupForm.getName()).role(Role.USER).build();

        return memberRepository.save(member);
    }

    @Transactional
    public Long updatePassword(Member member, String newPassword) {
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public void updateProfile(Member member, Profile profile) {
        member.updateName(profile.getName());
        member.updateImage(profile.getImage());
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name).orElse(null);
    }

    public Set<Member> findMembers(List<String> emails) {
        return memberRepository.findByEmailIn(emails);
    }

    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetails 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed : No User Info -> " + username));
    }
}