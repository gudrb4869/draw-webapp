package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.infra.IntegrationTest;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
class SettingsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("gudrb")
    void 프로필수정성공() throws Exception {
        String bio = "한 줄 소개";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        Account gudrb = accountRepository.findByEmail("gudrb");
        assertEquals(bio, gudrb.getBio());
    }

    @Test
    @WithAccount("gudrb")
    void 프로필수정실패_소개최대길이초과() throws Exception {
        String bio = "30자넘으면에러발생30자넘으면에러발생30자넘으면에러발생30자넘으면에러발생";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
        Account gudrb = accountRepository.findByEmail("gudrb");
        assertNull(gudrb.getBio());
    }

    @Test
    @WithAccount("gudrb")
    void 프로필조회() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경폼() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경성공() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1234")
                        .param("newPassword", "1235")
                        .param("newPasswordConfirm", "1235")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByEmail("gudrb");
        assertNotNull(account);
        assertTrue(passwordEncoder.matches("1235", account.getPassword()));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경실패_현재비밀번호불일치() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1235")
                        .param("newPassword", "1236")
                        .param("newPasswordConfirm", "1236")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경실패_현재비밀번호와신규비밀번호가같음() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1234")
                        .param("newPassword", "1234")
                        .param("newPasswordConfirm", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경_신규비밀번호와비밀번호학인불일치() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1234")
                        .param("newPassword", "1235")
                        .param("newPasswordConfirm", "1236")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("gudrb")
    void 비밀번호변경실패_길이() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1234")
                        .param("newPassword", "1")
                        .param("newPasswordConfirm", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithAccount("gudrb")
    void 알림설정화면() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_NOTIFICATION_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_NOTIFICATION_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notificationForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 알림설정() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_NOTIFICATION_URL)
                        .param("competitionCreatedByWeb", "true")
                        .param("matchUpdatedByWeb", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_NOTIFICATION_URL))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByEmail("gudrb");
        assertTrue(account.isCompetitionCreatedByWeb());
        assertTrue(account.isMatchUpdatedByWeb());
    }

    @Test
    @WithAccount("gudrb")
    void 계정관리화면() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nameForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 이름수정성공() throws Exception {
        String newName = "gudrb1234";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("name", newName)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByEmail("gudrb");
        assertEquals(newName, account.getName());
    }

    @Test
    @WithAccount("gudrb")
    void 이름수정실패_최소길이() throws Exception {
        String newName = "g";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("name", newName)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nameForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 이름수정실패_최대길이() throws Exception {
        String newName = "gudrbgudrbgudrb";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("name", newName)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nameForm"));
    }

    @Test
    @WithAccount("gudrb")
    void 이름수정실패_중복() throws Exception {
        String newName = "gudrb123";
        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                        .param("name", newName)
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nameForm"));
    }
}