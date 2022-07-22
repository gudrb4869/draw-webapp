package hongik.ce.jolup;

import hongik.ce.jolup.domain.belong.Belong;
import hongik.ce.jolup.domain.belong.BelongType;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.domain.member.Role;
import hongik.ce.jolup.domain.room.Room;
import hongik.ce.jolup.domain.room.RoomSetting;
import hongik.ce.jolup.service.BelongService;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        public void dbInit() {

            List<Member> members = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                Member member = createMember(i);
                members.add(member);
                memberService.saveMember(member);
            }

            Room room1 = createRoom("private", RoomSetting.PRIVATE);
            roomService.saveRoom(room1);

            belongService.save(members.get(1).getId(), room1.getId(), BelongType.MASTER);
            for (int i = 2; i < 11; i++) {
                belongService.save(members.get(i).getId(), room1.getId(), BelongType.USER);
            }

            Room room2 = createRoom("public", RoomSetting.PUBLIC);
            roomService.saveRoom(room2);

            belongService.save(members.get(1).getId(), room2.getId(), BelongType.MASTER);
            for (int i = 11; i < 20; i++) {
                belongService.save(members.get(i).getId(), room2.getId(), BelongType.USER);
            }

            Room room3 = createRoom("test123", RoomSetting.PUBLIC);
            roomService.saveRoom(room3);

            belongService.save(members.get(1).getId(), room3.getId(), BelongType.MASTER);
            for (int i = 7; i < 14; i++) {
                belongService.save(members.get(i).getId(), room3.getId(), BelongType.USER);
            }

        }

        private Room createRoom(String title, RoomSetting setting) {
            return Room.builder().title(title).roomSetting(setting).build();
        }

        private Member createMember(int i) {
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            Member member = Member.builder()
//                    .email(Integer.toString(i))
//                    .password(encoder.encode("1"))
//                    .name("user" + i)
//                    .role(Role.USER)
//                    .build();
//            return member;

            Member member = Member.builder()
                    .email(Integer.toString(i))
                    .password("1")
                    .name("user" + i)
                    .build();
            return member;
        }
    }
}
