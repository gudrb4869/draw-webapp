package hongik.ce.jolup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JolupApplication {

	public static void main(String[] args) {
		SpringApplication.run(JolupApplication.class, args);
	}

}
