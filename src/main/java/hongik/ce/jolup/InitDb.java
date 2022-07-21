package hongik.ce.jolup;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

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

        private final EntityManager em;

        public void dbInit() {
            for (int i = 1; i <= 20; i++) {
                Member member = createMember(i);
                em.persist(member);
            }


        }

        private Member createMember(int i) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Member member = Member.builder()
                    .email("test" + i)
                    .password(encoder.encode("1"))
                    .name("user" + i)
                    .role(Role.USER)
                    .build();
            return member;
        }
    }
}
