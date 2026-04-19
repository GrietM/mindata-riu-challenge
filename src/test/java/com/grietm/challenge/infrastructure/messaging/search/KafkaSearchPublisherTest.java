package com.grietm.challenge.infrastructure.messaging.search;

import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaSearchPublisherTest {

	private static final String SEARCH_TOPIC = "hotel_availability_searches";

	@Test
	void shouldPublishSearchUsingSearchIdAsKafkaKey() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);

		Search search = new Search(
			new SearchId("search-123"),
			new SearchCriteria(
				"hotel-456",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30, 29, 1, 3)
			)
		);

		publisher.publish(search);

		verify(kafkaTemplate).send(
			SEARCH_TOPIC,
			"search-123",
			new SearchMessage(
				"search-123",
				"hotel-456",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30, 29, 1, 3)
			)
		);
	}

	@Test
	void shouldRejectNullSearch() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);

		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> publisher.publish(null)
		);

		assertEquals("search must not be null", exception.getMessage());
	}

	@Test
	void shouldDefensivelyCopyAgesWhenCreatingMessage() {
		List<Integer> originalAges = List.of(7, 2, 7, 1);
		SearchMessage message = new SearchMessage(
			"search-123",
			"hotel-456",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			originalAges
		);

		assertAll(
			() -> assertEquals(List.of(7, 2, 7, 1), message.ages()),
			() -> assertThrows(UnsupportedOperationException.class, () -> message.ages().add(9))
		);
	}

}
