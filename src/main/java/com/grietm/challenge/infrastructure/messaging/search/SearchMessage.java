package com.grietm.challenge.infrastructure.messaging.search;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record SearchMessage(
	String searchId,
	String hotelId,
	@JsonFormat(pattern = "dd/MM/yyyy")
	LocalDate checkIn,
	@JsonFormat(pattern = "dd/MM/yyyy")
	LocalDate checkOut,
	List<Integer> ages
) {

	public SearchMessage {
		Objects.requireNonNull(searchId, "searchId must not be null");
		Objects.requireNonNull(hotelId, "hotelId must not be null");
		Objects.requireNonNull(checkIn, "checkIn must not be null");
		Objects.requireNonNull(checkOut, "checkOut must not be null");
		ages = List.copyOf(Objects.requireNonNull(ages, "ages must not be null"));
	}

}
