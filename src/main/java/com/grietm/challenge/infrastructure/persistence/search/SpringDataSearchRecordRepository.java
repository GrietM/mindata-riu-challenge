package com.grietm.challenge.infrastructure.persistence.search;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataSearchRecordRepository extends JpaRepository<SearchRecordJpaEntity, String> {
}
