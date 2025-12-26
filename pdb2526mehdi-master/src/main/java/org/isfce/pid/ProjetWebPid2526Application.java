package org.isfce.pid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // <--- Indispensable pour activer les listeners
@SpringBootApplication
public class ProjetWebPid2526Application {

	public static void main(String[] args) {
		SpringApplication.run(ProjetWebPid2526Application.class, args);
	}

}
