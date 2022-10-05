package hongik.ce.jolup.app;

import hongik.ce.jolup.module.member.endpoint.form.SignupForm;
import hongik.ce.jolup.module.member.application.MemberService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMemberSecurityContextFactory implements WithSecurityContextFactory<WithMember> {

    private final MemberService memberService;

    public WithMemberSecurityContextFactory(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public SecurityContext createSecurityContext(WithMember annotation) {
        String email = annotation.value();

        SignupForm signupForm = new SignupForm();
        signupForm.setEmail(email);
        signupForm.setPassword("qwer1234");
        signupForm.setName(email);
        memberService.signup(signupForm);

        UserDetails principal = memberService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
