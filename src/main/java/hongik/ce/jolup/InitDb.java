package hongik.ce.jolup;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import hongik.ce.jolup.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final MemberService memberService;

        public void dbInit() {
            for (int i = 0; i < 20; i++) {
                Member member = createMember(i);
                memberService.saveMember(member.toDto());
            }


        }

        private Member createMember(int i) {
            Member member = Member.builder()
                    .email("test" + i)
                    .password("1")
                    .name("user" + i)
                    .role(Role.USER)
                    .build();
            return member;
        }
    }
}
