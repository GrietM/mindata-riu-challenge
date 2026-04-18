package com.grietm.challenge.infrastructure.id;

import com.grietm.challenge.application.port.out.SearchIdGenerator;
import com.grietm.challenge.domain.model.SearchId;

import java.util.UUID;

public class UuidSearchIdGenerator implements SearchIdGenerator {

	@Override
	public SearchId nextId() {
		return new SearchId(UUID.randomUUID().toString());
	}

}
