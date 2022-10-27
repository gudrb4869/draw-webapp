package hongik.ce.jolup.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    // 인증을 무시할 경로 설정
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/node_modules/**", "/images/**", "/tournament.css")
                .antMatchers("/h2-console/**", "/error");
    }

    @Override
    // http 관련 인증 설정 기능
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/signup", "/search/room", "/search").permitAll() // 누구나 접근 가능
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated(); // 나머지는 권한이 있기만 하면 접근 가능
        http.formLogin() // 로그인에 대한 설정
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/") // 로그인 성공시 연결되는 주소
                .permitAll();
        http.logout() // 로그아웃 관련 설정
                .logoutSuccessUrl("/") // 로그아웃 성공시 연결되는 주소
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .invalidateHttpSession(true) // 로그아웃시 저장해 둔 세션 날리기
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/")
                .maxSessionsPreventsLogin(false);
    }

    /*@Override
    // 로그인 시 필요한 정보를 가져옴
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService) // 유저 정보는 userDetailsService 에서 가져온다
                .passwordEncoder(passwordEncoder()); // 패스워드 인코더는 passwordEncoder(BCrypt 사용)
    }*/
}
