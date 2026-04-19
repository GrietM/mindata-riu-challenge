package com.grietm.challenge.infrastructure.config;

import com.grietm.challenge.application.port.in.CreateSearchUseCase;
import com.grietm.challenge.application.port.out.SearchIdGenerator;
import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.application.service.CreateSearchService;
import com.grietm.challenge.infrastructure.id.UuidSearchIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateSearchConfiguration {

	@Bean
	CreateSearchUseCase createSearchUseCase(SearchIdGenerator searchIdGenerator, SearchPublisher searchPublisher) {
		return new CreateSearchService(searchIdGenerator, searchPublisher);
	}

	@Bean
	SearchIdGenerator searchIdGenerator() {
		return new UuidSearchIdGenerator();
	}

}
