package hongik.ce.jolup.module.room.endpoint;

import hongik.ce.jolup.infra.IntegrationTest;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.WithAccount;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.room.application.RoomService;
import hongik.ce.jolup.module.room.domain.entity.Room;
import hongik.ce.jolup.module.room.endpoint.form.RoomForm;
import hongik.ce.jolup.module.room.infra.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}