package com.grietm.challenge.infrastructure.web.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record GetSearchCountResponse(
	@Schema(description = "Identifier of the persisted search used as reference", example = "6e289fdc-0190-41d7-a25d-32850c687195")
	String searchId,
	@Schema(description = "Original persisted search data associated with the given searchId")
	SearchDetails search,
	@Schema(description = "Number of persisted searches exactly equal to the reference search", example = "2")
	long count
) {

	public record SearchDetails(
		@Schema(description = "Hotel identifier", example = "4521")
		String hotelId,
		@JsonFormat(pattern = "dd/MM/yyyy")
		@Schema(description = "Check-in date in dd/MM/yyyy format", example = "29/12/2026", pattern = "dd/MM/yyyy", type = "string")
		LocalDate checkIn,
		@JsonFormat(pattern = "dd/MM/yyyy")
		@Schema(description = "Check-out date in dd/MM/yyyy format", example = "31/12/2026", pattern = "dd/MM/yyyy", type = "string")
		LocalDate checkOut,
		@ArraySchema(
			schema = @Schema(description = "Guest age", example = "7"),
			arraySchema = @Schema(description = "Ordered guest ages", example = "[7, 2, 7, 1]")
		)
		List<Integer> ages
	) {
	}

}
