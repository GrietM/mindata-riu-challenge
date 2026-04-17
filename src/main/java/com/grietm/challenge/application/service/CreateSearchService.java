package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.CreateSearchCommand;
import com.grietm.challenge.application.port.in.CreateSearchResult;
import com.grietm.challenge.application.port.in.CreateSearchUseCase;
import com.grietm.challenge.application.port.out.SearchIdGenerator;
import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;

import java.util.Objects;

public class CreateSearchService implements CreateSearchUseCase {

	private final SearchIdGenerator searchIdGenerator;
	private final SearchPublisher searchPublisher;

	public CreateSearchService(SearchIdGenerator searchIdGenerator, SearchPublisher searchPublisher) {
		this.searchIdGenerator = Objects.requireNonNull(searchIdGenerator, "searchIdGenerator must not be null");
		this.searchPublisher = Objects.requireNonNull(searchPublisher, "searchPublisher must not be null");
	}

	@Override
	public CreateSearchResult create(CreateSearchCommand command) {
		Objects.requireNonNull(command, "command must not be null");

		SearchCriteria criteria = new SearchCriteria(
			command.hotelId(),
			command.checkIn(),
			command.checkOut(),
			command.ages()
		);
		SearchId searchId = searchIdGenerator.nextId();
		Search search = new Search(searchId, criteria);

		searchPublisher.publish(search);

		return new CreateSearchResult(searchId.value());
	}

}
