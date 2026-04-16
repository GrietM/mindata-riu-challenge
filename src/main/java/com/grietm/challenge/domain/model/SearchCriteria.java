package com.grietm.challenge.domain.model;

import com.grietm.challenge.domain.exception.DomainValidationException;

import java.time.LocalDate;
import java.util.List;

public record SearchCriteria(
	String hotelId,
	LocalDate checkIn,
	LocalDate checkOut,
	List<Integer> ages
) {

	public SearchCriteria {
		validateHotelId(hotelId);
		validateDates(checkIn, checkOut);
		ages = validateAges(ages);
	}

	private static void validateHotelId(String hotelId) {
		if (hotelId == null || hotelId.isBlank()) {
			throw new DomainValidationException("hotelId must not be blank");
		}
	}

	private static void validateDates(LocalDate checkIn, LocalDate checkOut) {
		if (checkIn == null) {
			throw new DomainValidationException("checkIn must not be null");
		}
		if (checkOut == null) {
			throw new DomainValidationException("checkOut must not be null");
		}
		if (!checkIn.isBefore(checkOut)) {
			throw new DomainValidationException("checkIn must be earlier than checkOut");
		}
	}

	private static List<Integer> validateAges(List<Integer> ages) {
		if (ages == null) {
			throw new DomainValidationException("ages must not be null");
		}
		if (ages.isEmpty()) {
			throw new DomainValidationException("ages must not be empty");
		}
		if (ages.stream().anyMatch(age -> age == null || age < 0)) {
			throw new DomainValidationException("ages must contain only values greater than or equal to 0");
		}
		return List.copyOf(ages);
	}

}
