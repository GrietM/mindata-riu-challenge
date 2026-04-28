package com.grietm.challenge.infrastructure.persistence.search;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchAgeIdTest {

	@Test
	void shouldCreateSearchAgeIdWhenValuesAreValid() {
		SearchAgeId searchAgeId = new SearchAgeId("search-123", 2);

		assertAll(
			() -> assertEquals("search-123", searchAgeId.getSearchId()),
			() -> assertEquals(2, searchAgeId.getAgeOrder())
		);
	}

	@Test
	void shouldRejectNullSearchId() {
		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> new SearchAgeId(null, 2)
		);

		assertEquals("searchId must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectNullAgeOrder() {
		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> new SearchAgeId("search-123", null)
		);

		assertEquals("ageOrder must not be null", exception.getMessage());
	}

	@Test
	void shouldImplementEqualsAndHashCode() {
		SearchAgeId base = new SearchAgeId("search-123", 2);
		SearchAgeId same = new SearchAgeId("search-123", 2);
		SearchAgeId differentSearchId = new SearchAgeId("search-456", 2);
		SearchAgeId differentAgeOrder = new SearchAgeId("search-123", 3);

		assertAll(
			() -> assertTrue(base.equals(base)),
			() -> assertEquals(base, same),
			() -> assertEquals(base.hashCode(), same.hashCode()),
			() -> assertNotEquals(base, null),
			() -> assertNotEquals(base, "search-123"),
			() -> assertNotEquals(base, differentSearchId),
			() -> assertNotEquals(base, differentAgeOrder)
		);
	}

}
