package hongik.ce.jolup.module.main.endpoint;

import hongik.ce.jolup.module.account.domain.entity.Account;
import hongik.ce.jolup.module.account.support.CurrentAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler
    public String handleRuntimeException(@CurrentAccount Account account, HttpServletRequest request, RuntimeException exception, Model model) {
        log.info(getNameIfExists(account) + "requested {}", request.getRequestURI());
        log.error("bad request", exception);
        model.addAttribute(account);
        model.addAttribute("message", exception.getMessage());
        log.info("Referer : {}", request.getHeader("Referer"));
        model.addAttribute("referer", request.getHeader("Referer"));
        return "error";
    }

    private String getNameIfExists(Account account) {
        return Optional.ofNullable(account)
                .map(Account::getName)
                .map(s -> s + " ")
                .orElse("");
    }
}
