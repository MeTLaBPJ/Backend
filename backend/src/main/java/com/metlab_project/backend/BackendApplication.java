package com.metlab_project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
		scanBasePackages = "com.metlab_project",
		exclude = {SecurityAutoConfiguration.class}
)
@ComponentScan(basePackages = {"com.metlab_project"})
@EntityScan("com.metlab_project.backend.domain.entity")
public class BackendApplication {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BackendApplication.class);
		application.setLazyInitialization(true);
		application.run(args);
	}
}