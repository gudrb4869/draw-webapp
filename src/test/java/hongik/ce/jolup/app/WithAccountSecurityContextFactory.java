package hongik.ce.jolup.app;

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
        String email = annotation.value();

        SignupForm signupForm = new SignupForm();
        signupForm.setEmail(email);
        signupForm.setPassword("1234");
        signupForm.setName(email);
        accountService.signup(signupForm);

        UserDetails principal = accountService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
