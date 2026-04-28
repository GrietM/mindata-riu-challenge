package com.grietm.challenge.infrastructure.persistence.search;

import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.port.out.SearchPersistenceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
public class JpaSearchRecordRepository implements SearchPersistenceRepository {

	private final SpringDataSearchRecordRepository repository;

	public JpaSearchRecordRepository(SpringDataSearchRecordRepository repository) {
		this.repository = Objects.requireNonNull(repository, "repository must not be null");
	}

	@Override
	@Transactional
	public void save(Search search) {
		Objects.requireNonNull(search, "search must not be null");

		SearchRecordJpaEntity entity = new SearchRecordJpaEntity(
			search.id().value(),
			search.criteria().hotelId(),
			search.criteria().checkIn(),
			search.criteria().checkOut()
		);
		entity.replaceAges(search.criteria().ages());

		repository.save(entity);
	}

}
