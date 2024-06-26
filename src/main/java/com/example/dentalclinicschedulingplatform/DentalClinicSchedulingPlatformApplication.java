package com.example.dentalclinicschedulingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
public class DentalClinicSchedulingPlatformApplication {
	public static void main(String[] args) {
		SpringApplication.run(DentalClinicSchedulingPlatformApplication.class, args);
	}
}
