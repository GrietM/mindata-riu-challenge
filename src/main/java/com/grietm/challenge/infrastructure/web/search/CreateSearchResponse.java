package com.grietm.challenge.infrastructure.web.search;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateSearchResponse(
	@Schema(description = "Unique identifier assigned to the accepted search", example = "6e289fdc-0190-41d7-a25d-32850c687195")
	String searchId
) {
}
