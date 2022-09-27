package hongik.ce.jolup;

import hongik.ce.jolup.domain.join.Grade;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.Access;
import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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

//        private final EntityManager em;
//
//        public void dbInit() {
//
//            List<Member> members = new ArrayList<>();
//            for (int i = 0; i < 20; i++) {
//                Member member = createMember(i);
//                members.add(member);
//                em.persist(member);
//            }
//
//            Room room1 = createRoom("private", RoomSetting.PRIVATE);
//            em.persist(room1);
//
//            Belong belong1 = Belong.builder().member(members.get(1)).room(room1).belongType(BelongType.MASTER).build();
//            em.persist(belong1);
//            for (int i = 2; i < 11; i++) {
//                Belong belong = Belong.builder().member(members.get(i)).room(room1).belongType(BelongType.USER).build();
//                em.persist(belong);
//            }
//
//            Room room2 = createRoom("public", RoomSetting.PUBLIC);
//            em.persist(room2);
//
//            Belong belong2 = Belong.builder().member(members.get(1)).room(room2).belongType(BelongType.MASTER).build();
//            em.persist(belong2);
//            for (int i = 11; i < 20; i++) {
//                Belong belong = Belong.builder().member(members.get(i)).room(room2).belongType(BelongType.USER).build();
//                em.persist(belong);
//            }
//
//            Room room3 = createRoom("test123", RoomSetting.PUBLIC);
//            em.persist(room3);
//
//            Belong belong3 = Belong.builder().member(members.get(1)).room(room3).belongType(BelongType.MASTER).build();
//            em.persist(belong3);
//            for (int i = 7; i < 14; i++) {
//                Belong belong = Belong.builder().member(members.get(i)).room(room3).belongType(BelongType.USER).build();
//                em.persist(belong);
//            }

        private final MemberService memberService;
        private final JoinService joinService;
        private final RoomService roomService;
        private final CompetitionService competitionService;
        private final LeagueTableService leagueTableService;
        private final LeagueGameService leagueGameService;
        private final SingleLegGameService singleLegGameService;

        public void dbInit() {

            /**
             * 회원 DB 생성
             */
            List<Member> members = new ArrayList<>();
            for (int i = 0; i <= 32; i++) {
                members.add(memberService.signup(createMember(i)));
            }

            Room room1 = createRoom("Test", Access.PRIVATE);
            Long room1Id = roomService.saveRoom(room1);

            joinService.save(members.get(1).getId(), room1Id, Grade.ADMIN);
            for (int i = 2; i <= 32; i++) {
                joinService.save(members.get(i).getId(), room1Id, Grade.USER);
            }

            Room room2 = createRoom("private", Access.PRIVATE);
            Long room2Id = roomService.saveRoom(room2);

            joinService.save(members.get(1).getId(), room2Id, Grade.ADMIN);

            Room room3 = createRoom("public", Access.PUBLIC);
            Long room3Id = roomService.saveRoom(room3);

            joinService.save(members.get(1).getId(), room3Id, Grade.ADMIN);

            /**
             * 대회, 경기 테스트 DB 생성
             */

            /*Long worldCupId = competitionService.save("WorldCup", CompetitionType.TOURNAMENT, room1Id);
            List<Long> memberId32 = new ArrayList<>();
            for (int i = 1; i <= 32; i++) {
                memberId32.add(members.get(i).getId());
            }
            singleLegGameService.save(memberId32, worldCupId);

            Long eplId = competitionService.save("EPL", CompetitionType.LEAGUE, room1Id);
            List<Long> memberId20 = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                memberId20.add(members.get(i).getId());
            }
            leagueTableService.save(memberId20, eplId);
            leagueGameService.save(memberId20, eplId);*/
        }

        private Room createRoom(String name, Access access) {
            return Room.builder().name(name).access(access).build();
        }

        private SignupForm createMember(int i) {
            SignupForm signupForm = new SignupForm();
            signupForm.setEmail(Integer.toString(i));
            signupForm.setPassword("1");
            signupForm.setName("user" + i);
            return signupForm;
        }
    }
}
