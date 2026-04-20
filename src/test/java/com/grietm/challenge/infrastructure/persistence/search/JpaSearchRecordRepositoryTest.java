package com.grietm.challenge.infrastructure.persistence.search;

import com.grietm.challenge.domain.model.Search;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.domain.model.SearchId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(JpaSearchRecordRepository.class)
class JpaSearchRecordRepositoryTest {

	@Autowired
	private JpaSearchRecordRepository jpaSearchRecordRepository;

	@Autowired
	private SpringDataSearchRecordRepository springDataSearchRecordRepository;

	@Test
	void shouldPersistSearchWithOrderedAges() {
		Search search = new Search(
			new SearchId("search-123"),
			new SearchCriteria(
				"hotel-456",
				LocalDate.of(2026, 12, 29),
				LocalDate.of(2026, 12, 31),
				List.of(7, 2, 7, 1)
			)
		);

		jpaSearchRecordRepository.save(search);

		SearchRecordJpaEntity entity = springDataSearchRecordRepository.findById("search-123").orElseThrow();

		assertAll(
			() -> assertEquals("search-123", entity.getSearchId()),
			() -> assertEquals("hotel-456", entity.getHotelId()),
			() -> assertEquals(LocalDate.of(2026, 12, 29), entity.getCheckIn()),
			() -> assertEquals(LocalDate.of(2026, 12, 31), entity.getCheckOut()),
			() -> assertEquals(List.of(7, 2, 7, 1), entity.getAges().stream().map(SearchAgeJpaEntity::getAge).toList()),
			() -> assertEquals(List.of(0, 1, 2, 3), entity.getAges().stream().map(age -> age.getId().getAgeOrder()).toList())
		);
	}

}
