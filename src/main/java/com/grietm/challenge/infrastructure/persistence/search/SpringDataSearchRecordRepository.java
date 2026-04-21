package com.grietm.challenge.infrastructure.persistence.search;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataSearchRecordRepository extends JpaRepository<SearchRecordJpaEntity, String> {

	@Query("""
		SELECT DISTINCT s
		FROM SearchRecordJpaEntity s
		LEFT JOIN FETCH s.ages
		WHERE s.searchId = :searchId
		""")
	Optional<SearchRecordJpaEntity> findBySearchIdWithAges(@Param("searchId") String searchId);
}
