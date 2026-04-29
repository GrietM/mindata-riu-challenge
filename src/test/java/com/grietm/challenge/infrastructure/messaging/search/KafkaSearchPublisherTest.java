package com.grietm.challenge.infrastructure.messaging.search;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class KafkaSearchPublisherTest {

	private static final String SEARCH_TOPIC = "hotel_availability_searches";

	@Test
	void shouldPublishSearchUsingSearchIdAsKafkaKey() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);
		SendResult<String, SearchMessage> sendResult = successfulSendResult();
		when(kafkaTemplate.send(SEARCH_TOPIC, "search-123", expectedMessage()))
			.thenReturn(CompletableFuture.completedFuture(sendResult));

		Search search = sampleSearch();

		publisher.publish(search);

		verify(kafkaTemplate).send(
			SEARCH_TOPIC,
			"search-123",
			expectedMessage()
		);
	}

	@Test
	void shouldLogAsyncPublishSuccess() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);
		ListAppender<ILoggingEvent> logAppender = attachLogAppender();
		SendResult<String, SearchMessage> sendResult = successfulSendResult();
		when(kafkaTemplate.send(SEARCH_TOPIC, "search-123", expectedMessage()))
			.thenReturn(CompletableFuture.completedFuture(sendResult));

		try {
			publisher.publish(sampleSearch());

			assertTrue(
				logAppender.list.stream().anyMatch(event ->
					event.getLevel() == Level.INFO &&
						event.getFormattedMessage().contains("Published search message with id search-123") &&
						event.getFormattedMessage().contains("partition 2 offset 15")
				)
			);
		} finally {
			detachLogAppender(logAppender);
		}
	}

	@Test
	void shouldLogAsyncPublishFailure() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);
		ListAppender<ILoggingEvent> logAppender = attachLogAppender();
		CompletableFuture<SendResult<String, SearchMessage>> failedFuture = new CompletableFuture<>();
		failedFuture.completeExceptionally(new IllegalStateException("broker unavailable"));
		when(kafkaTemplate.send(SEARCH_TOPIC, "search-123", expectedMessage()))
			.thenReturn(failedFuture);

		try {
			publisher.publish(sampleSearch());

			assertTrue(
				logAppender.list.stream().anyMatch(event ->
					event.getLevel() == Level.ERROR &&
						event.getFormattedMessage().contains("Failed to publish search message with id search-123") &&
						event.getThrowableProxy() != null &&
						event.getThrowableProxy().getMessage().contains("broker unavailable")
				)
			);
		} finally {
			detachLogAppender(logAppender);
		}
	}

	@Test
	void shouldWrapSynchronousPublishFailureWithContext() {
		KafkaTemplate<String, SearchMessage> kafkaTemplate = mock(KafkaTemplate.class);
		KafkaSearchPublisher publisher = new KafkaSearchPublisher(kafkaTemplate, SEARCH_TOPIC);
		Search search = sampleSearch();
		when(kafkaTemplate.send(SEARCH_TOPIC, "search-123", expectedMessage()))
			.thenThrow(new IllegalStateException("serializer failure"));

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> publisher.publish(search)
		);

		assertAll(
			() -> assertEquals(
				"Failed to start Kafka publish for search id search-123 to topic hotel_availability_searches",
				exception.getMessage()
			),
			() -> assertEquals(IllegalStateException.class, exception.getCause().getClass()),
			() -> assertEquals("serializer failure", exception.getCause().getMessage())
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
		List<Integer> copiedAges = message.ages();

		assertAll(
			() -> assertEquals(List.of(7, 2, 7, 1), copiedAges),
			() -> assertThrows(UnsupportedOperationException.class, () -> copiedAges.add(9))
		);
	}

	private static Search sampleSearch() {
		return new Search(
			new SearchId("search-123"),
			new SearchCriteria(
				"hotel-456",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(30, 29, 1, 3)
			)
		);
	}

	private static SearchMessage expectedMessage() {
		return new SearchMessage(
			"search-123",
			"hotel-456",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		);
	}

	private static SendResult<String, SearchMessage> successfulSendResult() {
		SendResult<String, SearchMessage> sendResult = mock(SendResult.class);
		org.apache.kafka.clients.producer.RecordMetadata recordMetadata =
			mock(org.apache.kafka.clients.producer.RecordMetadata.class);
		when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
		when(recordMetadata.partition()).thenReturn(2);
		when(recordMetadata.offset()).thenReturn(15L);
		return sendResult;
	}

	private static ListAppender<ILoggingEvent> attachLogAppender() {
		ch.qos.logback.classic.Logger logger =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(KafkaSearchPublisher.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.setContext(logger.getLoggerContext());
		listAppender.setName("kafka-search-publisher-test-" + System.nanoTime());
		listAppender.start();
		logger.addAppender(listAppender);
		return listAppender;
	}

	private static void detachLogAppender(ListAppender<ILoggingEvent> listAppender) {
		ch.qos.logback.classic.Logger logger =
			(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(KafkaSearchPublisher.class);
		logger.detachAppender(listAppender);
		listAppender.stop();
	}

}
