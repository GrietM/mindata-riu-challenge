package com.grietm.challenge.infrastructure.web.search;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Schema(description = "Check-in date in dd/MM/yyyy format", example = "29/12/2026", pattern = "dd/MM/yyyy", type = "string")
	@NotNull(message = "checkIn must not be null")
	LocalDate checkIn,
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Schema(description = "Check-out date in dd/MM/yyyy format", example = "31/12/2026", pattern = "dd/MM/yyyy", type = "string")
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
