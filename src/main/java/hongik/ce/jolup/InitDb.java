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

            /**
             * 회원 테스트 DB 생성
             */
            List<Member> members = new ArrayList<>();
            for (int i = 0; i <= 64; i++) {
                members.add(memberService.saveMember(Integer.toString(i), "1", "user" + i));
            }

            /**
             * 방 테스트 DB 생성
             */
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

            /**
             * 대회, 경기 테스트 DB 생성
             */
            Long tournament64 = competitionService.save("test64", CompetitionType.TOURNAMENT, room4Id);
            List<Long> memberId64 = new ArrayList<>();
            for (int i = 1; i <= 64; i++) {
                memberId64.add(members.get(i).getId());
            }
            joinService.save(memberId64, tournament64);
            matchService.saveMatches(memberId64, tournament64);

            Long tournament48 = competitionService.save("test48", CompetitionType.TOURNAMENT, room4Id);
            List<Long> memberId48 = new ArrayList<>();
            for (int i = 1; i <= 48; i++) {
                memberId48.add(members.get(i).getId());
            }
            joinService.save(memberId48, tournament48);
            matchService.saveMatches(memberId48, tournament48);

            Long tournament32 = competitionService.save("test32", CompetitionType.TOURNAMENT, room4Id);
            List<Long> memberId32 = new ArrayList<>();
            for (int i = 1; i <= 32; i++) {
                memberId32.add(members.get(i).getId());
            }
            joinService.save(memberId32, tournament32);
            matchService.saveMatches(memberId32, tournament32);

            Long tournament24 = competitionService.save("test24", CompetitionType.TOURNAMENT, room4Id);
            List<Long> memberId24 = new ArrayList<>();
            for (int i = 1; i <= 24; i++) {
                memberId24.add(members.get(i).getId());
            }
            joinService.save(memberId24, tournament24);
            matchService.saveMatches(memberId24, tournament24);

            Long tournament12 = competitionService.save("test12", CompetitionType.TOURNAMENT, room4Id);
            List<Long> memberId12 = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                memberId12.add(members.get(i).getId());
            }
            joinService.save(memberId12, tournament12);
            matchService.saveMatches(memberId12, tournament12);

            Long league20 = competitionService.save("test20", CompetitionType.LEAGUE, room4Id);
            List<Long> memberId20 = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                memberId20.add(members.get(i).getId());
            }
            joinService.save(memberId20, league20);
            matchService.saveMatches(memberId20, league20);
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
