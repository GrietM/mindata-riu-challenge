package com.grietm.challenge.infrastructure.persistence.search;

import com.grietm.challenge.application.port.out.SearchReadRepository;
import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class JpaSearchReadRepository implements SearchReadRepository {

	private final SpringDataSearchRecordRepository repository;
	private final EntityManager entityManager;

	public JpaSearchReadRepository(SpringDataSearchRecordRepository repository, EntityManager entityManager) {
		this.repository = Objects.requireNonNull(repository, "repository must not be null");
		this.entityManager = Objects.requireNonNull(entityManager, "entityManager must not be null");
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Search> findById(SearchId searchId) {
		Objects.requireNonNull(searchId, "searchId must not be null");

		return repository.findBySearchIdWithAges(searchId.value())
			.map(this::toDomain);
	}

	@Override
	@Transactional(readOnly = true)
	public long countEqualSearches(SearchCriteria criteria) {
		Objects.requireNonNull(criteria, "criteria must not be null");

		Query query = entityManager.createNativeQuery(buildCountQuery(criteria.ages().size()));
		bindCommonParameters(query, criteria);
		bindAgeParameters(query, criteria.ages());

		return ((Number) query.getSingleResult()).longValue();
	}

	private String buildCountQuery(int agesSize) {
		StringBuilder sql = new StringBuilder(baseCountQuery());
		appendAgeMatchClauses(sql, agesSize);
		return sql.toString();
	}

	private String baseCountQuery() {
		return """
			SELECT COUNT(*)
			FROM search_records sr
			WHERE sr.hotel_id = :hotelId
			  AND sr.check_in = :checkIn
			  AND sr.check_out = :checkOut
			  AND (
			    SELECT COUNT(*)
			    FROM search_record_ages sra_count
			    WHERE sra_count.search_id = sr.search_id
			  ) = :agesSize
			""";
	}

	private void appendAgeMatchClauses(StringBuilder sql, int agesSize) {
		for (int index = 0; index < agesSize; index++) {
			sql.append("""
				
				  AND EXISTS (
				    SELECT 1
				    FROM search_record_ages sra_match
				    WHERE sra_match.search_id = sr.search_id
				      AND sra_match.age_order = :ageOrder""").append(index).append("""
				
				      AND sra_match.age = :ageValue""").append(index).append("""
				
				  )
				""");
		}
	}

	private void bindCommonParameters(Query query, SearchCriteria criteria) {
		query.setParameter("hotelId", criteria.hotelId());
		query.setParameter("checkIn", criteria.checkIn());
		query.setParameter("checkOut", criteria.checkOut());
		query.setParameter("agesSize", criteria.ages().size());
	}

	private void bindAgeParameters(Query query, List<Integer> ages) {
		for (int index = 0; index < ages.size(); index++) {
			query.setParameter("ageOrder" + index, index);
			query.setParameter("ageValue" + index, ages.get(index));
		}
	}

	private Search toDomain(SearchRecordJpaEntity entity) {
		return new Search(
			new SearchId(entity.getSearchId()),
			new SearchCriteria(
				entity.getHotelId(),
				entity.getCheckIn(),
				entity.getCheckOut(),
				entity.getAges().stream().map(SearchAgeJpaEntity::getAge).toList()
			)
		);
	}

}
