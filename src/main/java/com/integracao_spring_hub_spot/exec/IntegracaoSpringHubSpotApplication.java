package com.integracao_spring_hub_spot.exec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class IntegracaoSpringHubSpotApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegracaoSpringHubSpotApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
