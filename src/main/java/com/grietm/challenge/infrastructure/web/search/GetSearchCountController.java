package com.grietm.challenge.infrastructure.web.search;

import com.grietm.challenge.application.port.in.GetSearchCountResult;
import com.grietm.challenge.application.port.in.GetSearchCountUseCase;
import com.grietm.challenge.domain.model.SearchId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/count")
public class GetSearchCountController {

	private final GetSearchCountUseCase getSearchCountUseCase;

	public GetSearchCountController(GetSearchCountUseCase getSearchCountUseCase) {
		this.getSearchCountUseCase = getSearchCountUseCase;
	}

	@GetMapping
	public ResponseEntity<GetSearchCountResponse> count(@Valid @ModelAttribute GetSearchCountRequest request) {
		return getSearchCountUseCase.count(new SearchId(request.searchId()))
			.map(this::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
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
