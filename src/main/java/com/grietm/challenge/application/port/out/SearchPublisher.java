package com.grietm.challenge.application.port.out;

import com.grietm.challenge.domain.model.Search;

public interface SearchPublisher {

	void publish(Search search);

}
