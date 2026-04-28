package com.grietm.challenge.domain.port.out;

import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;

import java.util.Optional;

public interface SearchReadRepository {

	Optional<Search> findById(SearchId searchId);

	long countEqualSearches(SearchCriteria criteria);

}
