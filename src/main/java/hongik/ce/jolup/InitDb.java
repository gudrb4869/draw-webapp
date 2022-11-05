package hongik.ce.jolup;

import hongik.ce.jolup.module.competition.application.CompetitionService;
import hongik.ce.jolup.module.competition.domain.entity.Competition;
import hongik.ce.jolup.module.competition.domain.entity.CompetitionType;
import hongik.ce.jolup.module.competition.endpoint.form.CompetitionForm;
import hongik.ce.jolup.module.member.application.FollowService;
import hongik.ce.jolup.module.member.application.MemberService;
import hongik.ce.jolup.module.member.domain.entity.Member;
import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        Long id = initService.dbInit();
        initService.createCompetition(id);
    }

    @Component
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
        private final FollowService followService;
        private final RoomService roomService;
        private final CompetitionService competitionService;

        @Transactional
        public Long dbInit() {

            /**
             * 회원 DB 생성
             */
            List<Member> members = new ArrayList<>();
            for (int i = 0; i <= 50; i++) {
                members.add(memberService.signup(createMemberForm(i)));
            }
            Member member = members.get(1);
            for (int i = 2; i <= 50; i++) {
                followService.createFollow(member, members.get(i));
            }

            RoomForm roomForm1 = createRoomForm("1", true);
            Room room = roomService.createNewRoom(roomForm1, member);
            for (int i = 2; i <= 50; i++) {
                roomService.addMember(room, members.get(i));
            }

            for (int i = 2; i <= 50; i++) {
                RoomForm roomForm = createRoomForm(Integer.toString(i), true);
                roomService.createNewRoom(roomForm, member);
            }
            return room.getId();
        }

        public void createCompetition(Long id) {
            Room room = roomService.getRoom(id);
            /**
             * 대회, 경기 테스트 DB 생성
             */
            List<Member> members = room.getJoins().stream().map(Join::getMember).collect(Collectors.toList());
            Competition competition1 = competitionService.createCompetition(members, room,
                    createCompetitionForm(members, "demo1", CompetitionType.SINGLE_ROUND_ROBIN, 0, 11));

            Competition competition2 = competitionService.createCompetition(members, room,
                    createCompetitionForm(members, "demo2", CompetitionType.DOUBLE_ROUND_ROBIN, 12, 19));

            Competition competition3 = competitionService.createCompetition(members, room,
                    createCompetitionForm(members, "demo3", CompetitionType.SINGLE_ELIMINATION_TOURNAMENT, 20, 49));
        }

        private CompetitionForm createCompetitionForm(List<Member> members, String title, CompetitionType type, int start, int end) {
            CompetitionForm competitionForm = new CompetitionForm();
            competitionForm.setTitle(title);
            competitionForm.setType(type);
            for (int i = start; i <= end; i++) {
                competitionForm.getMembers().add(members.get(i).getId());
            }
            return competitionForm;
        }

        private RoomForm createRoomForm(String title, boolean revealed) {
            RoomForm roomForm = new RoomForm();
            roomForm.setTitle(title);
            roomForm.setRevealed(revealed);
            roomForm.setShortDescription(title + "소개");
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
