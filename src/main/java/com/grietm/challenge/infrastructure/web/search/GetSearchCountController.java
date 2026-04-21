package com.grietm.challenge.infrastructure.web.search;

import com.grietm.challenge.application.port.in.GetSearchCountResult;
import com.grietm.challenge.application.port.in.GetSearchCountUseCase;
import com.grietm.challenge.domain.model.SearchId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Tag(name = "Searches", description = "Register hotel searches and count identical persisted searches")
@RestController
@RequestMapping("/count")
public class GetSearchCountController {

	private static final Logger log = LoggerFactory.getLogger(GetSearchCountController.class);

	private final GetSearchCountUseCase getSearchCountUseCase;

	public GetSearchCountController(GetSearchCountUseCase getSearchCountUseCase) {
		this.getSearchCountUseCase = getSearchCountUseCase;
	}

	@Operation(
		summary = "Count identical persisted searches",
		description = """
			Loads the persisted search identified by searchId and returns how many persisted searches
			are exactly equal to it, including the same ages in the same order.
			"""
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Persisted search found and counted successfully",
			content = @Content(schema = @Schema(implementation = GetSearchCountResponse.class))
		),
		@ApiResponse(responseCode = "400", description = "Request validation failed", content = @Content),
		@ApiResponse(responseCode = "404", description = "No persisted search found for the given searchId", content = @Content)
	})
	@GetMapping
	public ResponseEntity<GetSearchCountResponse> count(
		@Parameter(
			description = "Unique search identifier returned by POST /search",
			required = true,
			example = "6e289fdc-0190-41d7-a25d-32850c687195"
		)
		@RequestParam @NotBlank(message = "searchId must not be blank") String searchId
	) {
		log.info("Received count request for searchId {}", searchId);

		return getSearchCountUseCase.count(new SearchId(searchId))
			.map(result -> {
				log.info("Returning count {} for searchId {}", result.count(), searchId);
				return result;
			})
			.map(this::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> {
				log.info("No persisted search found for searchId {}", searchId);
				return ResponseEntity.notFound().build();
			});
	}

	private GetSearchCountResponse toResponse(GetSearchCountResult result) {
		return new GetSearchCountResponse(
			result.searchId(),
			new GetSearchCountResponse.SearchDetails(
				result.search().hotelId(),
				result.search().checkIn(),
				result.search().checkOut(),
				result.search().ages()
			),
			result.count()
		);
	}

}
