package com.grietm.challenge.application.service;

import com.grietm.challenge.application.port.in.PersistSearchCommand;
import com.grietm.challenge.domain.exception.DomainValidationException;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.port.out.SearchPersistenceRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersistSearchServiceTest {

	@Test
	void shouldRebuildDomainSearchAndSaveIt() {
		CapturingSearchPersistenceRepository repository = new CapturingSearchPersistenceRepository();
		PersistSearchService service = new PersistSearchService(repository);

		service.persist(new PersistSearchCommand(
			"search-001",
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		));

		assertAll(
			() -> assertEquals("search-001", repository.savedSearch.id().value()),
			() -> assertEquals("hotel-123", repository.savedSearch.criteria().hotelId()),
			() -> assertEquals(LocalDate.of(2026, 12, 29), repository.savedSearch.criteria().checkIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), repository.savedSearch.criteria().checkOut()),
			() -> assertEquals(List.of(30, 29, 1, 3), repository.savedSearch.criteria().ages())
		);
	}

	@Test
	void shouldPreserveAgesOrderWhenSavingSearch() {
		CapturingSearchPersistenceRepository repository = new CapturingSearchPersistenceRepository();
		PersistSearchService service = new PersistSearchService(repository);

		service.persist(new PersistSearchCommand(
			"search-ordered",
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(7, 2, 7, 1)
		));

		assertEquals(List.of(7, 2, 7, 1), repository.savedSearch.criteria().ages());
	}

	@Test
	void shouldFailWhenCommandIsNull() {
		PersistSearchService service = new PersistSearchService(new CapturingSearchPersistenceRepository());

		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> service.persist(null)
		);

		assertEquals("command must not be null", exception.getMessage());
	}

	@Test
	void shouldPropagateDomainValidationFailures() {
		CapturingSearchPersistenceRepository repository = new CapturingSearchPersistenceRepository();
		PersistSearchService service = new PersistSearchService(repository);

		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> service.persist(new PersistSearchCommand(
				"search-001",
				" ",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30)
			))
		);

		assertAll(
			() -> assertEquals("hotelId must not be blank", exception.getMessage()),
			() -> assertNull(repository.savedSearch)
		);
	}

	private static final class CapturingSearchPersistenceRepository implements SearchPersistenceRepository {

		private Search savedSearch;

		@Override
		public void save(Search search) {
			this.savedSearch = search;
		}

	}

}
