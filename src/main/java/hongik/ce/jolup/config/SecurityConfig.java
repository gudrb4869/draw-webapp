package hongik.ce.jolup.config;

import hongik.ce.jolup.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    // 인증을 무시할 경로 설정
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/error");
//        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "h2-console/**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    // http 관련 인증 설정 기능
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().headers().frameOptions().disable()// h2-console 화면 사용하기 위함
                    .and()
                .authorizeRequests()
                    .antMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/images/**", "/error").permitAll() // 누구나 접근 가능
                    .anyRequest().authenticated() // 나머지는 권한이 있기만 하면 접근 가능
                    .and()
                .formLogin() // 로그인에 대한 설정
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/") // 로그인 성공시 연결되는 주소
                    .and()
                .logout() // 로그아웃 관련 설정
                    .logoutSuccessUrl("/") // 로그아웃 성공시 연결되는 주소
                    .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                    .invalidateHttpSession(true); // 로그아웃시 저장해 둔 세션 날리기
                /*.and()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/")
                .maxSessionsPreventsLogin(false)*/
    }

    @Override
    // 로그인 시 필요한 정보를 가져옴
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService) // 유저 정보는 memberService 에서 가져온다
                .passwordEncoder(bCryptPasswordEncoder()); // 패스워드 인코더는 passwordEncoder(BCrypt 사용)
    }
}
