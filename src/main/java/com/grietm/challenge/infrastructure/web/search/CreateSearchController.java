package com.grietm.challenge.infrastructure.web.search;

import com.grietm.challenge.application.port.in.CreateSearchCommand;
import com.grietm.challenge.application.port.in.CreateSearchResult;
import com.grietm.challenge.application.port.in.CreateSearchUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Searches", description = "Register hotel searches and count identical persisted searches")
@RestController
@RequestMapping("/search")
public class CreateSearchController {

	private final CreateSearchUseCase createSearchUseCase;

	public CreateSearchController(CreateSearchUseCase createSearchUseCase) {
		this.createSearchUseCase = createSearchUseCase;
	}

	@Operation(
		summary = "Register a hotel search",
		description = """
			Validates a hotel availability search request, generates a unique searchId,
			and publishes the accepted search asynchronously to Kafka.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Search accepted and identified successfully",
			content = @Content(schema = @Schema(implementation = CreateSearchResponse.class))
		),
		@ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content)
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CreateSearchResponse create(@Valid @RequestBody CreateSearchRequest request) {
		CreateSearchResult result = createSearchUseCase.create(new CreateSearchCommand(
			request.hotelId(),
			request.checkIn(),
			request.checkOut(),
			request.ages()
		));

		return new CreateSearchResponse(result.searchId());
	}

}
