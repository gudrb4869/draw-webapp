package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.infra.IntegrationTest;
import hongik.ce.jolup.module.account.application.FollowService;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.account.application.AccountService;
import hongik.ce.jolup.module.account.infra.repository.FollowRepository;
import hongik.ce.jolup.module.notification.infra.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
class AccountControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    FollowRepository followRepository;
    @Autowired
    FollowService followService;
    @Autowired
    NotificationRepository notificationRepository;
/*
    @AfterEach
    void afterEach() throws Exception {
        notificationRepository.deleteAll();
        followRepository.deleteAll();
        accountRepository.deleteAll();
    }*/
    @Test
    void 회원가입화면() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("signupForm"));
    }

    @Test
    void 회원가입실패_최소길이() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "a")
                        .param("password", "a")
                        .param("passwordConfirm", "a")
                        .param("name", "1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"));
    }

    @Test
    void 회원가입실패_비밀번호불일치() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "gudrb")
                        .param("password", "1234")
                        .param("passwordConfirm", "1235")
                        .param("name", "형규")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"));
    }

    @Test
    void 회원가입성공() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "gudrb")
                        .param("password", "1234")
                        .param("passwordConfirm", "1234")
                        .param("name", "형규")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        assertTrue(accountRepository.existsByEmail("gudrb"));
        Account account = accountRepository.findByEmail("gudrb");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "1234");
    }

    @Test
    @WithAccount(value = {"test", "gudrb"})
    void 팔로우성공() throws Exception {
        Account source = accountRepository.findByEmail("gudrb");
        Account target = accountRepository.findByEmail("test");
        mockMvc.perform(post("/profile/" + target.getId() + "/follow")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + target.getId()));
        assertTrue(followRepository.existsByFollowingAndFollower(source, target));
    }

    @Test
    @WithAccount(value = {"test", "gudrb"})
    void 언팔로우성공() throws Exception {
        Account source = accountRepository.findByEmail("gudrb");
        Account target = accountRepository.findByEmail("test");
        followService.createFollow(source, target);
        mockMvc.perform(post("/profile/" + target.getId() + "/unfollow")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/" + target.getId()));
        assertFalse(followRepository.existsByFollowingAndFollower(source, target));
    }
}