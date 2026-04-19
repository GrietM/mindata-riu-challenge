package com.grietm.challenge.infrastructure.messaging.search;

import com.grietm.challenge.application.port.out.SearchPublisher;
import com.grietm.challenge.domain.model.Search;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KafkaSearchPublisher implements SearchPublisher {

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

		SearchMessage message = new SearchMessage(
			search.id().value(),
			search.criteria().hotelId(),
			search.criteria().checkIn(),
			search.criteria().checkOut(),
			search.criteria().ages()

		);

		kafkaTemplate.send(topic, search.id().value(), message);
	}

}
