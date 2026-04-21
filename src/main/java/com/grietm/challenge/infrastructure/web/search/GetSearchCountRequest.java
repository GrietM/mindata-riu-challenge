package com.grietm.challenge.infrastructure.web.search;

import jakarta.validation.constraints.NotBlank;

public record GetSearchCountRequest(
	@NotBlank(message = "searchId must not be blank")
	String searchId
) {
}
