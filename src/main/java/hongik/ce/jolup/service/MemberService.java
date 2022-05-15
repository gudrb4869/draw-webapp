package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.dto.BelongDto;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.domain.member.MemberAuth;
import hongik.ce.jolup.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetail 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Could not found : " + email));
    }

    public Long saveMember(MemberDto memberDto) {
        validateDuplicateUser(memberDto);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberDto.setPassword(encoder.encode(memberDto.getPassword()));
        memberDto.setAuth(MemberAuth.USER);

        return memberRepository.save(memberDto.toEntity()).getId();
    }

    public Long updateMember(MemberDto memberDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        memberDto.setPassword(encoder.encode(memberDto.getPassword()));
        return memberRepository.save(memberDto.toEntity()).getId();
    }

    private void validateDuplicateUser(MemberDto memberDto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberDto.getEmail());
        if (!optionalMember.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다!");
        }
    }

    public List<MemberDto> findAll() {
        List<Member> memberList = memberRepository.findAll();
        return memberList.stream()
                .map(Member::toDto)
                .collect(Collectors.toList());
    }

    public MemberDto getMember(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty())
            return null;
        Member member = optionalMember.get();
        log.info("member.joins = {}", member.getJoins());
        return member.toDto();
    }

    public MemberDto findOne(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            return null;
        }
        Member member = optionalMember.get();
        return member.toDto();
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    public HashMap<String, Object> emailOverlap(String email) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("result", memberRepository.existsByEmail(email));
        return map;
    }

    public List<BelongDto> getBelongs(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            return null;
        }
        Member member = optionalMember.get();
        List<BelongDto> belongDtos = member.getBelongs().stream().map(Belong::toDto).collect(Collectors.toList());
        return belongDtos;
    }
}