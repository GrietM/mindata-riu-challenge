package com.grietm.challenge.domain.model;

import com.grietm.challenge.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SearchTest {

	@Test
	void shouldCreateSearchWhenIdAndCriteriaAreValid() {
		Search search = new Search(
			new SearchId("search-123"),
			new SearchCriteria(
				"1234aBc",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30, 29, 1, 3)
			)
		);

		assertAll(
			() -> assertEquals("search-123", search.id().value()),
			() -> assertEquals("1234aBc", search.criteria().hotelId()),
			() -> assertEquals(LocalDate.of(2026, 12, 29), search.criteria().checkIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), search.criteria().checkOut()),
			() -> assertEquals(List.of(30, 29, 1, 3), search.criteria().ages())
		);
	}

	@Test
	void shouldKeepAgesImmutable() {
		SearchCriteria criteria = new SearchCriteria(
			"1234aBc",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		);

		assertThrows(UnsupportedOperationException.class, () -> criteria.ages().add(10));
	}

	@Test
	void shouldRejectBlankSearchId() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchId(" ")
		);

		assertEquals("searchId must not be blank", exception.getMessage());
	}

	@Test
	void shouldRejectNullSearchIdValue() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchId(null)
		);

		assertEquals("searchId must not be blank", exception.getMessage());
	}

	@Test
	void shouldRejectSearchWhenCriteriaIsNull() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new Search(new SearchId("search-123"), null)
		);

		assertEquals("searchCriteria must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectSearchWhenIdIsNull() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new Search(null, new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30)))
		);

		assertEquals("searchId must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectSearchWhenCheckInIsNotEarlierThanCheckOut() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 31), LocalDate.of(2026, 12, 31), List.of(30))
		);

		assertEquals("checkIn must be earlier than checkOut", exception.getMessage());
	}

	@Test
	void shouldRejectBlankHotelId() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria(" ", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30))
		);

		assertEquals("hotelId must not be blank", exception.getMessage());
	}

	@Test
	void shouldRejectNullCheckIn() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", null, LocalDate.of(2026, 12, 31), List.of(30))
		);

		assertEquals("checkIn must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectNullCheckOut() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), null, List.of(30))
		);

		assertEquals("checkOut must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectNullAges() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), null)
		);

		assertEquals("ages must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectNullAgeElement() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), Arrays.asList(30, null))
		);

		assertEquals("ages must contain only values greater than or equal to 0", exception.getMessage());
	}

	@Test
	void shouldRejectNegativeAges() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of(30, -1))
		);

		assertEquals("ages must contain only values greater than or equal to 0", exception.getMessage());
	}

	@Test
	void shouldRejectEmptyAges() {
		DomainValidationException exception = assertThrows(
			DomainValidationException.class,
			() -> new SearchCriteria("1234aBc", LocalDate.of(2026, 12, 29), LocalDate.of(2026, 12, 31), List.of())
		);

		assertEquals("ages must not be empty", exception.getMessage());
	}

}
