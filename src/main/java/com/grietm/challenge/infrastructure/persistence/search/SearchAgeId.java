package com.grietm.challenge.infrastructure.persistence.search;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SearchAgeId implements Serializable {

	@Column(name = "search_id", nullable = false, length = 64)
	private String searchId;

	@Column(name = "age_order", nullable = false)
	private Integer ageOrder;

	protected SearchAgeId() {
	}

	public SearchAgeId(String searchId, Integer ageOrder) {
		this.searchId = Objects.requireNonNull(searchId, "searchId must not be null");
		this.ageOrder = Objects.requireNonNull(ageOrder, "ageOrder must not be null");
	}

	public String getSearchId() {
		return searchId;
	}

	public Integer getAgeOrder() {
		return ageOrder;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SearchAgeId that)) {
			return false;
		}
		return Objects.equals(searchId, that.searchId) && Objects.equals(ageOrder, that.ageOrder);
	}

	@Override
	public int hashCode() {
		return Objects.hash(searchId, ageOrder);
	}

}
