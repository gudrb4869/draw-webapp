package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.model.Profile;
import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.model.PasswordForm;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

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
}