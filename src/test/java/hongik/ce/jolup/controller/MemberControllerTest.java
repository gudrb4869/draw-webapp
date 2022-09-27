package hongik.ce.jolup.controller;

import hongik.ce.jolup.domain.member.Member;
import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.repository.MemberRepository;
import hongik.ce.jolup.service.MemberService;
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
class MemberControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;

    @BeforeEach
    void beforeEach() {
    }

    @AfterEach()
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 화면 진입 확인")
    void signupForm() throws Exception {
        mockMvc.perform(get("/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("members/signup"))
                .andExpect(model().attributeExists("signupForm"));
    }

    @Test
    @DisplayName("회원 가입 처리: 입력값 오류")
    void signupSubmitWithError() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "a")
                        .param("password", "a1!")
                        .param("name", "1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("members/signup"));
    }

    @Test
    @DisplayName("회원 가입 처리: 입력값 정상")
    void signupSubmit() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "hongik")
                        .param("password", "hongik1!")
                        .param("name", "홍익")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertTrue(memberRepository.existsByEmail("hongik"));
        Member member = memberRepository.findByEmail("hongik").orElse(null);
        assertNotNull(member);
        assertNotEquals(member.getPassword(), "hongik1!");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        SignupForm signupForm = new SignupForm();
        signupForm.setEmail("hongik");
        signupForm.setPassword("hongik1!");
        signupForm.setName("홍익");
        memberService.signup(signupForm);
        mockMvc.perform(post("/login")
                        .param("email", "hongik")
                        .param("password", "hongik1!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("hongik"));
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "wkrwjs")
                        .param("password", "wkrwjs1!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    /*@Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        mockMvc.perform(get("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }*/
}