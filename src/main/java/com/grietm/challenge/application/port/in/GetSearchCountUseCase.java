package com.grietm.challenge.application.port.in;

import com.grietm.challenge.domain.model.SearchId;

import java.util.Optional;

public interface GetSearchCountUseCase {

	Optional<GetSearchCountResult> count(SearchId searchId);

}
