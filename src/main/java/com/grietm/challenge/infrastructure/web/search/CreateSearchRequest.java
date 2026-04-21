package com.grietm.challenge.infrastructure.web.search;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public record CreateSearchRequest(
	@Schema(description = "Hotel identifier", example = "4521")
	@NotBlank(message = "hotelId must not be blank")
	String hotelId,
	@Schema(description = "Check-in date", example = "2026-12-29")
	@NotNull(message = "checkIn must not be null")
	LocalDate checkIn,
	@Schema(description = "Check-out date", example = "2026-12-31")
	@NotNull(message = "checkOut must not be null")
	LocalDate checkOut,
	@ArraySchema(
		schema = @Schema(description = "Guest age", example = "7"),
		arraySchema = @Schema(description = "Ordered guest ages", example = "[7, 2, 7, 1]")
	)
	@NotEmpty(message = "ages must not be empty")
	List<@NotNull(message = "ages must not contain null values") @PositiveOrZero(message = "ages must contain only values greater than or equal to 0") Integer> ages
) {
}
