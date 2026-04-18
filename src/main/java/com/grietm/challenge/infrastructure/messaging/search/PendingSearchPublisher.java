package com.grietm.challenge.infrastructure.messaging.search;

import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.domain.model.Search;

public class PendingSearchPublisher implements SearchPublisher {

	@Override
	public void publish(Search search) {
		// Kafka publication will be implemented in a later branch.
	}

}
