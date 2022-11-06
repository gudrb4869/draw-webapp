package hongik.ce.jolup.controller;

import hongik.ce.jolup.app.WithAccount;
import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
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
    @DisplayName("비밀번호 변경 폼")
    @WithAccount("gudrb")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("비밀번호 변경: 입력값 정상")
    @WithAccount("gudrb")
    void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("currentPassword", "1234")
                        .param("newPassword", "1235")
                        .param("newPasswordConfirm", "1235")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByEmail("gudrb").orElse(null);
        assertNotNull(account);
        assertTrue(passwordEncoder.matches("1235", account.getPassword()));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(현재 비밀번호 불일치)")
    @WithAccount("gudrb")
    void updatePasswordWithNotMatchedCurrentPassword() throws Exception {
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
    @DisplayName("패스워드 수정: 입력값 에러(현재 비밀번호와 신규 비밀번호가 같음)")
    @WithAccount("hongik")
    void updatePasswordCurrentPasswordEqualsNewPassword() throws Exception {
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
    @DisplayName("패스워드 수정: 입력값 에러(신규 비밀번호와 비밀번호 확인이 불일치)")
    @WithAccount("hongik")
    void updatePasswordWithNotMatchedNewPasswordAndConfirmPassword() throws Exception {
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
    @DisplayName("패스워드 수정: 입력값 에러(길이)")
    @WithAccount("hongik")
    void updatePasswordWithLengthError() throws Exception {
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
}