package hongik.ce.jolup.app;

import hongik.ce.jolup.model.SignupForm;
import hongik.ce.jolup.service.MemberService;
import hongik.ce.jolup.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMemberSecurityContextFactory implements WithSecurityContextFactory<WithMember> {

    private final MemberService memberService;
    private final UserDetailsServiceImpl userDetailsService;

    public WithMemberSecurityContextFactory(MemberService memberService, UserDetailsServiceImpl userDetailsService) {
        this.memberService = memberService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public SecurityContext createSecurityContext(WithMember annotation) {
        String email = annotation.value();

        SignupForm signupForm = new SignupForm();
        signupForm.setEmail(email);
        signupForm.setPassword("qwer1234");
        signupForm.setName(email);
        memberService.signup(signupForm);

        UserDetails principal = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
