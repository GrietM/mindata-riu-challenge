package com.grietm.challenge.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

	@Bean
	OpenAPI challengeOpenApi() {
		return new OpenAPI().info(new Info()
			.title("MinData RIU Challenge API")
			.description("""
				API for registering hotel availability searches and counting identical persisted searches.
				HTTP requests and responses expose checkIn and checkOut using dd/MM/yyyy.
				POST /search accepts and publishes searches asynchronously through Kafka.
				GET /count reads persisted searches from MySQL using a searchId.
				""")
			.version("v1"));
	}

}
