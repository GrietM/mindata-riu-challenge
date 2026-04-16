package com.grietm.challenge.domain.model;

import com.grietm.challenge.domain.exception.DomainValidationException;

public record SearchId(String value) {

	public SearchId {
		if (value == null || value.isBlank()) {
			throw new DomainValidationException("searchId must not be blank");
		}
	}

}
