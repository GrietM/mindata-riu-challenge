package com.grietm.challenge.infrastructure.persistence.search;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "search_record_ages")
public class SearchAgeJpaEntity {

	@EmbeddedId
	private SearchAgeId id;

	@MapsId("searchId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "search_id", nullable = false)
	private SearchRecordJpaEntity search;

	@Column(name = "age", nullable = false)
	private Integer age;

	protected SearchAgeJpaEntity() {
	}

	public SearchAgeJpaEntity(SearchAgeId id, SearchRecordJpaEntity search, Integer age) {
		this.id = id;
		this.search = search;
		this.age = age;
	}

	public SearchAgeId getId() {
		return id;
	}

	public SearchRecordJpaEntity getSearch() {
		return search;
	}

	public Integer getAge() {
		return age;
	}

}
