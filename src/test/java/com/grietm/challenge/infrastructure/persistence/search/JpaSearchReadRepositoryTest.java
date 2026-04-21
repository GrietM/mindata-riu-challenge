package com.grietm.challenge.infrastructure.persistence.search;

import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({JpaSearchRecordRepository.class, JpaSearchReadRepository.class})
class JpaSearchReadRepositoryTest {

	@Autowired
	private JpaSearchRecordRepository jpaSearchRecordRepository;

	@Autowired
	private JpaSearchReadRepository jpaSearchReadRepository;

	@Test
	void shouldReturnAgesInTheSamePersistedOrderWhenFindingById() {
		persist("search-123", "hotel-456", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(7, 2, 7, 1));

		Search foundSearch = jpaSearchReadRepository.findById(new SearchId("search-123")).orElseThrow();

		assertAll(
			() -> assertEquals("search-123", foundSearch.id().value()),
			() -> assertEquals("hotel-456", foundSearch.criteria().hotelId()),
			() -> assertEquals(LocalDate.of(2026, 12, 29), foundSearch.criteria().checkIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), foundSearch.criteria().checkOut()),
			() -> assertEquals(List.of(7, 2, 7, 1), foundSearch.criteria().ages())
		);
	}

	@Test
	void shouldCountSearchesWithExactlyTheSameCriteria() {
		persist("search-1", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, 29, 1, 3));
		persist("search-2", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, 29, 1, 3));

		long count = jpaSearchReadRepository.countEqualSearches(new SearchCriteria(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		));

		assertEquals(2L, count);
	}

	@Test
	void shouldNotCountSearchesWithTheSameAgesInDifferentOrder() {
		persist("search-1", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, 29, 1, 3));
		persist("search-2", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(3, 29, 30, 1));

		long count = jpaSearchReadRepository.countEqualSearches(new SearchCriteria(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		));

		assertEquals(1L, count);
	}

	@Test
	void shouldNotCountSearchesWithDifferentNumberOfAges() {
		persist("search-1", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, 29, 1, 3));
		persist("search-2", "hotel-123", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, 29, 1));

		long count = jpaSearchReadRepository.countEqualSearches(new SearchCriteria(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		));

		assertEquals(1L, count);
	}

	@Test
	void shouldReturnEmptyWhenSearchIdDoesNotExist() {
		assertTrue(jpaSearchReadRepository.findById(new SearchId("missing-search")).isEmpty());
	}

	private void persist(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {
		jpaSearchRecordRepository.save(new Search(
			new SearchId(searchId),
			new SearchCriteria(hotelId, checkIn, checkOut, ages)
		));
	}

}
