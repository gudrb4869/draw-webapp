package hongik.ce.jolup;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.competition.Competition;
import hongik.ce.jolup.domain.competition.CompetitionType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import hongik.ce.jolup.domain.result.Result;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
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
        private final BelongService belongService;
        private final RoomService roomService;
        private final CompetitionService competitionService;
        private final JoinService joinService;
        private final MatchService matchService;

        public void dbInit() {

            List<Member> members = new ArrayList<>();
            for (int i = 0; i <= 64; i++) {
                members.add(memberService.saveMember(Integer.toString(i), "1", "user" + i));
            }

            Room room1 = createRoom("private", RoomSetting.PRIVATE);
            Long room1Id = roomService.saveRoom(room1);

            belongService.save(members.get(1).getId(), room1Id, BelongType.ADMIN);
            for (int i = 2; i < 11; i++) {
                belongService.save(members.get(i).getId(), room1Id, BelongType.USER);
            }

            Room room2 = createRoom("public", RoomSetting.PUBLIC);
            Long room2Id = roomService.saveRoom(room2);

            belongService.save(members.get(1).getId(), room2Id, BelongType.ADMIN);
            for (int i = 11; i < 20; i++) {
                belongService.save(members.get(i).getId(), room2Id, BelongType.USER);
            }

            Room room3 = createRoom("test123", RoomSetting.PUBLIC);
            Long room3Id = roomService.saveRoom(room3);

            belongService.save(members.get(1).getId(), room3Id, BelongType.ADMIN);
            for (int i = 7; i < 14; i++) {
                belongService.save(members.get(i).getId(), room3Id, BelongType.USER);
            }

            Room room4 = createRoom("Test", RoomSetting.PRIVATE);
            Long room4Id = roomService.saveRoom(room4);

            belongService.save(members.get(1).getId(), room4Id, BelongType.ADMIN);
            for (int i = 2; i <= 64; i++) {
                belongService.save(members.get(i).getId(), room4Id, BelongType.USER);
            }
        }

        private Room createRoom(String title, RoomSetting setting) {
            return Room.builder().title(title).roomSetting(setting).build();
        }

        private Member createMember(int i) {
            Member member = Member.builder()
                    .email(Integer.toString(i))
                    .password("1")
                    .name("user" + i)
                    .build();
            return member;
        }
    }
}
