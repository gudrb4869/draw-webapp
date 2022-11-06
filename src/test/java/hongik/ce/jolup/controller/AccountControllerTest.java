package hongik.ce.jolup.controller;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.endpoint.form.SignupForm;
import hongik.ce.jolup.module.account.infra.repository.AccountRepository;
import hongik.ce.jolup.module.account.application.AccountService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @BeforeEach
    void beforeEach() {
    }

    @AfterEach()
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 화면 진입 확인")
    void signupForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("signupForm"));
    }

    @Test
    @DisplayName("회원 가입 처리: 입력값 오류(최소 길이)")
    void signupSubmitWithLengthError() throws Exception {
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
    @DisplayName("회원 가입 처리: 입력값 오류(비밀번호 불일치)")
    void signupSubmitWithNotMatchedError() throws Exception {
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
    @DisplayName("회원 가입 처리: 입력값 정상")
    void signupSubmit() throws Exception {
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
        Account account = accountRepository.findByEmail("gudrb").orElse(null);
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "1234");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        SignupForm signupForm = new SignupForm();
        signupForm.setEmail("gudrb");
        signupForm.setPassword("1234");
        signupForm.setPasswordConfirm("1234");
        signupForm.setName("형규");
        accountService.signup(signupForm);
        mockMvc.perform(post("/login")
                        .param("username", "gudrb")
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("형규"));
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "gudrb123")
                        .param("password", "1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}