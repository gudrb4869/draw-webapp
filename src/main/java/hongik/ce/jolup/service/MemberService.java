package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public Member saveMember(String email, String password, String name) {
        validateDuplicateUser(email);
        validateDuplicateName(name);
        Member member = Member.builder().email(email)
                .password(encoder.encode(password))
                .name(name).role(Role.USER).build();

        return memberRepository.save(member);
    }

    @Transactional
    public Long updatePassword(Long memberId, String oldPassword, String newPassword) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return null;
        }
        validateOldAndNewPassword(oldPassword, member.getPassword());
        member.updatePassword(encoder.encode(newPassword));
        return member.getId();
    }

    @Transactional
    public Long updateMember(Long memberId, String oldPassword, String newPassword, String name) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return null;
        }
        validateOldAndNewPassword(oldPassword, member.getPassword());
        member.updatePassword(encoder.encode(newPassword));
        if (!member.getName().equals(name)) {
            validateDuplicateName(name);
        }
        member.updateName(name);
        return member.getId();
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    private void validateDuplicateUser(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다!");
        }
    }

    private void validateDuplicateName(String name) {
        Optional<Member> optionalMember = memberRepository.findByName(name);
        if (optionalMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 이름입니다!");
        }
    }

    private void validateOldAndNewPassword(String oldPassword, String newPassword) {
        if (!encoder.matches(oldPassword, newPassword)) {
            throw new IllegalStateException("비밀번호가 맞지 않습니다.");
        }
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

    public List<Member> findMembers(List<String> emails) {
        return memberRepository.findByEmailIn(emails);
    }
}