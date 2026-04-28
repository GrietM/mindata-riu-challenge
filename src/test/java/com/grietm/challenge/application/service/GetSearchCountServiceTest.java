package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.GetSearchCountResult;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import com.grietm.challenge.domain.port.out.SearchReadRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GetSearchCountServiceTest {

	@Test
	void shouldReturnSearchAndCountWhenSearchExists() {
		Search search = new Search(
			new SearchId("search-123"),
			new SearchCriteria(
				"hotel-456",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30, 29, 1, 3)
			)
		);
		InMemorySearchReadRepository repository = new InMemorySearchReadRepository(search, 7L);
		GetSearchCountService service = new GetSearchCountService(repository);

		Optional<GetSearchCountResult> result = service.count(new SearchId("search-123"));

		assertAll(
			() -> assertEquals(Optional.of(search), repository.searchRequested()),
			() -> assertEquals(Optional.of(search.criteria()), repository.criteriaRequested()),
			() -> assertEquals("search-123", result.orElseThrow().searchId()),
			() -> assertEquals(search.criteria(), result.orElseThrow().search()),
			() -> assertEquals(7L, result.orElseThrow().count())
		);
	}

	@Test
	void shouldReturnEmptyWhenSearchDoesNotExist() {
		InMemorySearchReadRepository repository = new InMemorySearchReadRepository(null, 0L);
		GetSearchCountService service = new GetSearchCountService(repository);

		Optional<GetSearchCountResult> result = service.count(new SearchId("missing-search"));

		assertAll(
			() -> assertEquals(Optional.empty(), repository.searchRequested()),
			() -> assertEquals(Optional.empty(), repository.criteriaRequested()),
			() -> assertFalse(result.isPresent())
		);
	}

	private static final class InMemorySearchReadRepository implements SearchReadRepository {

		private final Search storedSearch;
		private final long count;
		private Search searchRequested;
		private SearchCriteria criteriaRequested;

		private InMemorySearchReadRepository(Search storedSearch, long count) {
			this.storedSearch = storedSearch;
			this.count = count;
		}

		@Override
		public Optional<Search> findById(SearchId searchId) {
			if (storedSearch != null && storedSearch.id().equals(searchId)) {
				searchRequested = storedSearch;
				return Optional.of(storedSearch);
			}
			return Optional.empty();
		}

		@Override
		public long countEqualSearches(SearchCriteria criteria) {
			criteriaRequested = criteria;
			return count;
		}

		private Optional<Search> searchRequested() {
			return Optional.ofNullable(searchRequested);
		}

		private Optional<SearchCriteria> criteriaRequested() {
			return Optional.ofNullable(criteriaRequested);
		}

	}

}
