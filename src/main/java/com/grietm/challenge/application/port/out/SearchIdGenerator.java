package com.grietm.challenge.application.port.out;

import com.grietm.challenge.domain.model.SearchId;

public interface SearchIdGenerator {

	SearchId nextId();

}
