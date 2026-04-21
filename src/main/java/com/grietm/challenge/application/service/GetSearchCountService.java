package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.GetSearchCountResult;
import com.grietm.challenge.application.port.in.GetSearchCountUseCase;
import com.grietm.challenge.application.port.out.SearchReadRepository;
import com.grietm.challenge.domain.model.SearchId;

import java.util.Objects;
import java.util.Optional;

public class GetSearchCountService implements GetSearchCountUseCase {

	private final SearchReadRepository searchReadRepository;

	public GetSearchCountService(SearchReadRepository searchReadRepository) {
		this.searchReadRepository = Objects.requireNonNull(
			searchReadRepository,
			"searchReadRepository must not be null"
		);
	}

	@Override
	public Optional<GetSearchCountResult> count(SearchId searchId) {
		Objects.requireNonNull(searchId, "searchId must not be null");

		return searchReadRepository.findById(searchId)
			.map(search -> new GetSearchCountResult(
				search.id().value(),
				search.criteria(),
				searchReadRepository.countEqualSearches(search.criteria())
			));
	}

}
