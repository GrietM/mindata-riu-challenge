package com.grietm.challenge.infrastructure.web.search;

import java.time.LocalDate;
import java.util.List;

public record GetSearchCountResponse(
	String searchId,
	SearchDetails search,
	long count
) {

	public record SearchDetails(
		String hotelId,
		LocalDate checkIn,
		LocalDate checkOut,
		List<Integer> ages
	) {
	}

}
