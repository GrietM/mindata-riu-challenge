package com.grietm.challenge.infrastructure.config;

import com.grietm.challenge.application.port.in.PersistSearchUseCase;
import com.grietm.challenge.application.service.PersistSearchService;
import com.grietm.challenge.domain.port.out.SearchPersistenceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistSearchConfiguration {

	@Bean
	PersistSearchUseCase persistSearchUseCase(SearchPersistenceRepository searchPersistenceRepository) {
		return new PersistSearchService(searchPersistenceRepository);
	}

}
