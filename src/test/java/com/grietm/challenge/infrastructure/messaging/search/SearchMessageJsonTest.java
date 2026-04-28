package com.grietm.challenge.infrastructure.messaging.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class SearchMessageJsonTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldSerializeDatesUsingDayMonthYearFormat() throws Exception {
		SearchMessage message = new SearchMessage(
			"search-123",
			"hotel-456",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		);

		String json = objectMapper.writeValueAsString(message);

		assertAll(
			() -> assertTrue(json.contains("\"checkIn\":\"29/12/2026\"")),
			() -> assertTrue(json.contains("\"checkOut\":\"31/12/2026\""))
		);
	}

	@Test
	void shouldDeserializeDatesUsingDayMonthYearFormat() throws Exception {
		SearchMessage message = objectMapper.readValue("""
			{
			  "searchId": "search-123",
			  "hotelId": "hotel-456",
			  "checkIn": "29/12/2026",
			  "checkOut": "31/12/2026",
			  "ages": [30, 29, 1, 3]
			}
			""", SearchMessage.class);

		assertAll(
			() -> assertEquals(LocalDate.of(2026, 12, 29), message.checkIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), message.checkOut())
		);
	}

}
