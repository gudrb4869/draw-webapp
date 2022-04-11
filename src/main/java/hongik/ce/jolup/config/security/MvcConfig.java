package hongik.ce.jolup.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/signup").setViewName("signup");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/room/list").setViewName("myRoom");
        registry.addViewController("/room/create").setViewName("createRoom");
    }
}
