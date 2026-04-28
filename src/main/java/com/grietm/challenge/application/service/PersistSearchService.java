package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.PersistSearchCommand;
import com.grietm.challenge.application.port.in.PersistSearchUseCase;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import com.grietm.challenge.domain.port.out.SearchPersistenceRepository;

import java.util.Objects;

public class PersistSearchService implements PersistSearchUseCase {

	private final SearchPersistenceRepository searchPersistenceRepository;

	public PersistSearchService(SearchPersistenceRepository searchPersistenceRepository) {
		this.searchPersistenceRepository = Objects.requireNonNull(
			searchPersistenceRepository,
			"searchPersistenceRepository must not be null"
		);
	}

	@Override
	public void persist(PersistSearchCommand command) {
		Objects.requireNonNull(command, "command must not be null");

		Search search = new Search(
			new SearchId(command.searchId()),
			new SearchCriteria(
				command.hotelId(),
				command.checkIn(),
				command.checkOut(),
				command.ages()
			)
		);

		searchPersistenceRepository.save(search);
	}

}
