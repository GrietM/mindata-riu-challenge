package com.grietm.challenge.infrastructure.persistence.search;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "search_records")
public class SearchRecordJpaEntity {

	@Id
	@Column(name = "search_id", nullable = false, length = 64)
	private String searchId;

	@Column(name = "hotel_id", nullable = false, length = 255)
	private String hotelId;

	@Column(name = "check_in", nullable = false)
	private LocalDate checkIn;

	@Column(name = "check_out", nullable = false)
	private LocalDate checkOut;

	@OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id.ageOrder ASC")
	private List<SearchAgeJpaEntity> ages = new ArrayList<>();

	protected SearchRecordJpaEntity() {
	}

	public SearchRecordJpaEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut) {
		this.searchId = searchId;
		this.hotelId = hotelId;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
	}

	public void replaceAges(List<Integer> orderedAges) {
		ages.clear();
		for (int index = 0; index < orderedAges.size(); index++) {
			ages.add(new SearchAgeJpaEntity(
				new SearchAgeId(searchId, index),
				this,
				orderedAges.get(index)
			));
		}
	}

	public String getSearchId() {
		return searchId;
	}

	public String getHotelId() {
		return hotelId;
	}

	public LocalDate getCheckIn() {
		return checkIn;
	}

	public LocalDate getCheckOut() {
		return checkOut;
	}

	public List<SearchAgeJpaEntity> getAges() {
		return ages;
	}

}
