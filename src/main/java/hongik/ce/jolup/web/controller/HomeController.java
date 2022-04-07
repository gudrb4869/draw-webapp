package hongik.ce.jolup.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    /*@GetMapping("/myroom")
    public String myroom() {
        return "myroom";
    }

    @GetMapping("/option")
    public String option() {
        return "option";
    }*/
}
