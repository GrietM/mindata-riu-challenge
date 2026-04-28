package com.grietm.challenge.infrastructure.config;

import com.grietm.challenge.application.port.in.GetSearchCountUseCase;
import com.grietm.challenge.application.service.GetSearchCountService;
import com.grietm.challenge.domain.port.out.SearchReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GetSearchCountConfiguration {

	@Bean
	GetSearchCountUseCase getSearchCountUseCase(SearchReadRepository searchReadRepository) {
		return new GetSearchCountService(searchReadRepository);
	}

}
