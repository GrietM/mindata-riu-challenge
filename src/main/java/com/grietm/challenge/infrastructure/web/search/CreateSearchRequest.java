package com.grietm.challenge.infrastructure.web.search;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public record CreateSearchRequest(
	@NotBlank(message = "hotelId must not be blank")
	String hotelId,
	@NotNull(message = "checkIn must not be null")
	LocalDate checkIn,
	@NotNull(message = "checkOut must not be null")
	LocalDate checkOut,
	@NotEmpty(message = "ages must not be empty")
	List<@NotNull(message = "ages must not contain null values") @PositiveOrZero(message = "ages must contain only values greater than or equal to 0") Integer> ages
) {
}
