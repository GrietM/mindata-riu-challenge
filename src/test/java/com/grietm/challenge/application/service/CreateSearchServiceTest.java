package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.CreateSearchCommand;
import com.grietm.challenge.application.port.in.CreateSearchResult;
import com.grietm.challenge.application.port.out.SearchIdGenerator;
import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.domain.exception.DomainValidationException;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSearchServiceTest {

	@Test
	void shouldCreateAndPublishSearch() {
		CapturingSearchPublisher publisher = new CapturingSearchPublisher();
		FixedSearchIdGenerator idGenerator = new FixedSearchIdGenerator(new SearchId("search-001"));
		CreateSearchService service = new CreateSearchService(idGenerator, publisher);

		CreateSearchResult result = service.create(new CreateSearchCommand(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		));

		assertAll(
			() -> assertEquals("search-001", result.searchId()),
			() -> assertEquals("search-001", publisher.publishedSearch.id().value()),
			() -> assertEquals("hotel-123", publisher.publishedSearch.criteria().hotelId()),
			() -> assertEquals(LocalDate.of(2026, 12, 29), publisher.publishedSearch.criteria().checkIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), publisher.publishedSearch.criteria().checkOut()),
			() -> assertEquals(List.of(30, 29, 1, 3), publisher.publishedSearch.criteria().ages())
		);
	}

	@Test
	void shouldPreserveAgesOrderWhenCreatingSearch() {
		CapturingSearchPublisher publisher = new CapturingSearchPublisher();
		CreateSearchService service = new CreateSearchService(
			new FixedSearchIdGenerator(new SearchId("search-ordered")),
			publisher
		);

		service.create(new CreateSearchCommand(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(7, 2, 7, 1)
		));

		assertEquals(List.of(7, 2, 7, 1), publisher.publishedSearch.criteria().ages());
	}

	@Test
	void shouldUseGeneratedSearchIdForEachCreation() {
		CapturingSearchPublisher publisher = new CapturingSearchPublisher();
		IncrementalSearchIdGenerator idGenerator = new IncrementalSearchIdGenerator();
		CreateSearchService service = new CreateSearchService(idGenerator, publisher);

		CreateSearchResult first = service.create(validCommand());
		CreateSearchResult second = service.create(validCommand());

		assertAll(
			() -> assertEquals("search-1", first.searchId()),
			() -> assertEquals("search-2", second.searchId())
		);
	}

	@Test
	void shouldFailWhenCommandIsNull() {
		CreateSearchService service = new CreateSearchService(
			new FixedSearchIdGenerator(new SearchId("search-001")),
			new CapturingSearchPublisher()
		);

		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> service.create(null)
		);

		assertEquals("command must not be null", exception.getMessage());
	}

	@Test
	void shouldPropagateDomainValidationFailures() {
		CapturingSearchPublisher publisher = new CapturingSearchPublisher();
		CreateSearchService service = new CreateSearchService(
			new FixedSearchIdGenerator(new SearchId("search-001")),
			publisher
		);

		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> service.create(new CreateSearchCommand(
				" ",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30)
			))
		);

		assertAll(
			() -> assertEquals("hotelId must not be blank", exception.getMessage()),
			() -> assertNull(publisher.publishedSearch)
		);
	}

	private static CreateSearchCommand validCommand() {
		return new CreateSearchCommand(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		);
	}

	private static final class FixedSearchIdGenerator implements SearchIdGenerator {

		private final SearchId searchId;

		private FixedSearchIdGenerator(SearchId searchId) {
			this.searchId = searchId;
		}

		@Override
		public SearchId nextId() {
			return searchId;
		}

	}

	private static final class IncrementalSearchIdGenerator implements SearchIdGenerator {

		private int counter;

		@Override
		public SearchId nextId() {
			counter++;
			return new SearchId("search-" + counter);
		}

	}

	private static final class CapturingSearchPublisher implements SearchPublisher {

		private Search publishedSearch;

		@Override
		public void publish(Search search) {
			this.publishedSearch = search;
		}

	}

}
