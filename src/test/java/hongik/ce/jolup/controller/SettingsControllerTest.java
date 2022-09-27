package hongik.ce.jolup.controller;

import hongik.ce.jolup.app.WithMember;
import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("비밀번호 변경 폼")
    @WithMember("hongik")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/updatePasswordForm"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("비밀번호 변경: 입력값 정상")
    @WithMember("hongik")
    void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("email", "hongik")
                        .param("password_current", "qwer1234")
                        .param("password_new", "asdf1234")
                        .param("password_confirm", "asdf1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message"));
        Member member = memberRepository.findByEmail("hongik").orElse(null);
        assertNotNull(member);
        assertTrue(passwordEncoder.matches("asdf1234", member.getPassword()));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(현재 비밀번호 불일치)")
    @WithMember("hongik")
    void updatePasswordWithNotMatchedCurrentPassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("email", "hongik")
                        .param("password_current", "zxcv1234")
                        .param("password_new", "asdf1234")
                        .param("password_confirm", "asdf1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("members/updatePasswordForm"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"));
//                .andExpect(model().attributeExists("member"));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(현재 비밀번호와 신규 비밀번호가 같음)")
    @WithMember("hongik")
    void updatePasswordCurrentPasswordEqualsNewPassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("email", "hongik")
                        .param("password_current", "qwer1234")
                        .param("password_new", "qwer1234")
                        .param("password_confirm", "qwer1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("members/updatePasswordForm"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"));
//                .andExpect(model().attributeExists("member"));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(신규 비밀번호와 비밀번호 확인이 불일치)")
    @WithMember("hongik")
    void updatePasswordWithNotMatchedNewPasswordAndConfirmPassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("email", "hongik")
                        .param("password_current", "qwer1234")
                        .param("password_new", "asdf1234")
                        .param("password_confirm", "zxcv1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("members/updatePasswordForm"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"));
//                .andExpect(model().attributeExists("member"));
    }

    @Test
    @DisplayName("패스워드 수정: 입력값 에러(길이)")
    @WithMember("hongik")
    void updatePasswordWithLengthError() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("email", "hongik")
                        .param("password_current", "qwer1234")
                        .param("password_new", "abc123")
                        .param("password_confirm", "abc123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("members/updatePasswordForm"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"));
//                .andExpect(model().attributeExists("member"));
    }
}