package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.infra.IntegrationTest;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.WithAccount;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.account.infra.repository.FollowRepository;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Grade;
import hongik.ce.jolup.module.room.domain.entity.Join;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.infra.repository.JoinRepository;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@IntegrationTest
class RoomControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomService roomService;
    @Autowired
    JoinRepository joinRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    FollowRepository followRepository;

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {
        joinRepository.deleteAll();
        notificationRepository.deleteAll();
        followRepository.deleteAll();
        roomRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("gudrb")
    void 방만들기화면() throws Exception {
        mockMvc.perform(get("/rooms/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("roomForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 방만들기성공() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/rooms/create")
                .param("title", "room-title")
                .param("shortDescription", "short-description")
                .param("revealed", "true")
                .with(csrf()));
        Room room = roomRepository.findAll().stream().findFirst().orElseThrow(() -> new IllegalStateException("등록된 방이 없습니다."));
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms/" + room.getId()));
    }

    @Test
    @WithAccount("gudrb")
    void 방만들기실패() throws Exception {
        mockMvc.perform(post("/rooms/create")
                        .param("title", "room-titleroom-titleroom-titleroom-titleroom-titleroom-title")
                        .param("shortDescription", "short-descriptionshort-descriptionshort-description")
                        .param("revealed", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("room/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithAccount("gudrb")
    void 방메인화면() throws Exception {
        Account account = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(true)
                .build(), account);
        mockMvc.perform(get("/rooms/" + room.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("room/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    @WithAccount("gudrb")
    void 방멤버화면() throws Exception {
        Account account = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(true)
                .build(), account);
        mockMvc.perform(get("/rooms/" + room.getId() + "/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/members"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    @WithAccount("gudrb")
    void 방수정뷰() throws Exception {
        Account account = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(false)
                .build(), account);
        mockMvc.perform(get("/rooms/" + room.getId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("room/update-form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("roomForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 방수정() throws Exception {
        Account account = accountRepository.findByEmail("gudrb");
        String newTitle = "new-title";
        String newShortDescription = "new-shortDescription";
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(false)
                .build(), account);
        mockMvc.perform(post("/rooms/" + room.getId() + "/update")
                        .param("title", newTitle)
                        .param("shortDescription", newShortDescription)
                        .param("revealed", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms/" + room.getId()))
                .andExpect(flash().attributeExists("message"));
        Room updatedRoom = roomService.getRoom(room.getId());
        assertEquals(updatedRoom.getTitle(), newTitle);
        assertEquals(updatedRoom.getShortDescription(), newShortDescription);
    }

    @Test
    @WithAccount(value = {"gudrb", "test"})
    void 외부사용자의비공개방진입시에러발생() throws Exception {
        Account manager = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(false)
                .build(), manager);
        mockMvc.perform(get("/rooms/" + room.getId() + "/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @WithAccount(value = {"gudrb", "test"})
    void 방들어가기() throws Exception {
        Account manager = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(true)
                .build(), manager);
        mockMvc.perform(post("/rooms/" + room.getId() + "/join")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms/" + room.getId() + "/members"));
        Account member = accountRepository.findByEmail("test");
        assertNotEquals(Optional.empty(), joinRepository.findByRoomAndAccount(room, member));
    }

    @Test
    @WithAccount(value = {"gudrb", "test"})
    void 방나가기() throws Exception {
        Account manager = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(true)
                .build(), manager);
        Account member = accountRepository.findByEmail("test");
        roomService.addMember(room, member);
        mockMvc.perform(post("/rooms/" + room.getId() + "/leave")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms/" + room.getId() + "/members"));
        assertEquals(Optional.empty(), joinRepository.findByRoomAndAccount(room, member));
    }

    @Test
    @WithAccount("gudrb")
    void 방삭제() throws Exception {
        Account manager = accountRepository.findByEmail("gudrb");
        Room room = roomService.createNewRoom(RoomForm.builder()
                .title("room-title")
                .shortDescription("short-description")
                .revealed(true)
                .build(), manager);
        mockMvc.perform(delete("/rooms/" + room.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        Optional<Room> byId = roomRepository.findById(room.getId());
        assertEquals(Optional.empty(), byId);
    }
}