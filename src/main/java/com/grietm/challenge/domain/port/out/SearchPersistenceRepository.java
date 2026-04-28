package com.grietm.challenge.domain.port.out;

import com.grietm.challenge.domain.model.Search;

public interface SearchPersistenceRepository {

	void save(Search search);

}
