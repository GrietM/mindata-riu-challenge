package com.grietm.challenge.infrastructure.web.error;

import java.util.List;

public record ApiErrorResponse(
	String message,
	List<String> errors
) {
}
