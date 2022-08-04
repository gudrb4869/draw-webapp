package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
        Member member = Member.builder().email(email)
                .password(encoder.encode(password))
                .name(name).role(Role.USER).build();

        return memberRepository.save(member);
    }

    @Transactional
    public Long updateMember(Long memberId, String oldPassword, String newPassword, String name) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return null;
        }
        Member member = optionalMember.get();
        if (!encoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalStateException("비밀번호가 틀렸습니다.");
        }
        member.updatePassword(encoder.encode(newPassword));
        member.updateName(name);
        return member.getId();
    }

    @Transactional
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public HashMap<String, Object> emailOverlap(String email) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", memberRepository.existsByEmail(email));
        return map;
    }

    private void validateDuplicateUser(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다!");
        }
    }

    public Member findOne(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findOne(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public List<Member> findMembers(List<String> emails) {
        return memberRepository.findByEmailIn(emails);
    }
}