package com.grietm.challenge.domain.model;

import com.grietm.challenge.domain.exception.DomainValidationException;

public record Search(
	SearchId id,
	SearchCriteria criteria
) {

	public Search {
		if (id == null) {
			throw new DomainValidationException("searchId must not be null");
		}
		if (criteria == null) {
			throw new DomainValidationException("searchCriteria must not be null");
		}
	}

}
