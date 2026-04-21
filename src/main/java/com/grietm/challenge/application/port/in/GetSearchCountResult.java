package com.grietm.challenge.application.port.in;

import com.grietm.challenge.domain.model.SearchCriteria;

public record GetSearchCountResult(
	String searchId,
	SearchCriteria search,
	long count
) {
}
