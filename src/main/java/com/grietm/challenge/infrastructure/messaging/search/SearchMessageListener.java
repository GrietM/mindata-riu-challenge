package com.grietm.challenge.infrastructure.messaging.search;

import com.grietm.challenge.application.port.in.PersistSearchCommand;
import com.grietm.challenge.application.port.in.PersistSearchUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnProperty(name = "app.kafka.consumer-enabled", havingValue = "true")
public class SearchMessageListener {

	private static final Logger log = LoggerFactory.getLogger(SearchMessageListener.class);

	private final PersistSearchUseCase persistSearchUseCase;

	public SearchMessageListener(PersistSearchUseCase persistSearchUseCase) {
		this.persistSearchUseCase = Objects.requireNonNull(
			persistSearchUseCase,
			"persistSearchUseCase must not be null"
		);
	}

	@KafkaListener(
		topics = "${app.kafka.search-topic}",
		groupId = "${app.kafka.search-consumer-group-id}"
	)
	public void onMessage(SearchMessage message) {
		Objects.requireNonNull(message, "message must not be null");

		log.info("Received search message with id {}", message.searchId());

		persistSearchUseCase.persist(new PersistSearchCommand(
			message.searchId(),
			message.hotelId(),
			message.checkIn(),
			message.checkOut(),
			message.ages()
		));

		log.info("Persisted search message with id {}", message.searchId());
	}

}
