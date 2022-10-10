package hongik.ce.jolup;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
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
//        initService.dbInit();
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
        private final RoomService roomService;
        private final CompetitionService competitionService;

        public void dbInit() {

            /**
             * 회원 DB 생성
             */
            List<Member> members = new ArrayList<>();
            for (int i = 0; i <= 100; i++) {
                members.add(memberService.signup(createMemberForm(i)));
            }
            Member member = members.get(1);

            RoomForm roomForm1 = createRoomForm("1", true);
            Room room1 = roomService.createNewRoom(roomForm1, member);

            for (int i = 2; i <= 100; i++) {
                roomService.addMember(room1, members.get(i));
            }

            for (int i = 2; i <= 50; i++) {
                RoomForm roomForm = createRoomForm(Integer.toString(i), true);
                roomService.createNewRoom(roomForm, member);
            }

            /**
             * 대회, 경기 테스트 DB 생성
             */

            /*Long worldCupId = competitionService.save("zxcv", CompetitionType.TOURNAMENT, room1Id).getId();
            List<Long> memberId32 = new ArrayList<>();
            for (int i = 1; i <= 32; i++) {
                memberId32.add(members.get(i).getId());
            }
            tournamentService.save(memberId32, worldCupId);

            Long eplId = competitionService.save("asdf", CompetitionType.LEAGUE, room1Id).getId();
            List<Long> memberId20 = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                memberId20.add(members.get(i).getId());
            }
            leagueTableService.save(memberId20, eplId);
            leagueService.save(memberId20, eplId);*/
        }

        private RoomForm createRoomForm(String title, boolean access) {
            RoomForm roomForm = new RoomForm();
            roomForm.setTitle(title);
            roomForm.setAccess(access);
            return roomForm;
        }

        private SignupForm createMemberForm(int i) {
            SignupForm signupForm = new SignupForm();
            signupForm.setEmail(Integer.toString(i));
            signupForm.setPassword("1");
            signupForm.setName("user" + i);
            return signupForm;
        }
    }
}
