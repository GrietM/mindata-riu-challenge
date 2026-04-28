package com.grietm.challenge.infrastructure.messaging.search;

import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.domain.model.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.Objects;

@Component
public class KafkaSearchPublisher implements SearchPublisher {

	private static final Logger log = LoggerFactory.getLogger(KafkaSearchPublisher.class);

	private final KafkaTemplate<String, SearchMessage> kafkaTemplate;
	private final String topic;

	public KafkaSearchPublisher(
		KafkaTemplate<String, SearchMessage> kafkaTemplate,
		@Value("${app.kafka.search-topic}") String topic
	) {
		this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null");
		this.topic = Objects.requireNonNull(topic, "topic must not be null");
	}

	@Override
	public void publish(Search search) {
		Objects.requireNonNull(search, "search must not be null");

		String searchId = search.id().value();
		SearchMessage message = new SearchMessage(
			searchId,
			search.criteria().hotelId(),
			search.criteria().checkIn(),
			search.criteria().checkOut(),
			search.criteria().ages()

		);

		try {
			CompletableFuture<SendResult<String, SearchMessage>> publishResult =
				kafkaTemplate.send(topic, searchId, message);

			publishResult.whenComplete((result, error) -> {
				if (error != null) {
					log.error("Failed to publish search message with id {} to topic {}", searchId, topic, error);
					return;
				}

				log.info(
					"Published search message with id {} to topic {} partition {} offset {}",
					searchId,
					topic,
					result.getRecordMetadata().partition(),
					result.getRecordMetadata().offset()
				);
			});
		} catch (RuntimeException exception) {
			log.error("Failed to start Kafka publish for search id {} to topic {}", searchId, topic, exception);
			throw exception;
		}
	}

}
