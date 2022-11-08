package hongik.ce.jolup.module.account.endpoint;

import hongik.ce.jolup.module.account.endpoint.form.SignupForm;
import hongik.ce.jolup.module.account.application.AccountService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    public WithAccountSecurityContextFactory(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        String[] emails = annotation.value();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        for (String email : emails) {
            SignupForm signupForm = new SignupForm();
            signupForm.setEmail(email);
            signupForm.setPassword("1234");
            signupForm.setName(email + "123");
            accountService.signup(signupForm);
            UserDetails principal = accountService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            context.setAuthentication(authentication);
        }
        // 테스트 주체는 마지막에 등록되는 email임.
        return context;
    }
}
