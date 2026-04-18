package com.grietm.challenge.infrastructure.web.search;

import com.grietm.challenge.application.port.in.CreateSearchCommand;
import com.grietm.challenge.application.port.in.CreateSearchResult;
import com.grietm.challenge.application.port.in.CreateSearchUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class CreateSearchController {

	private final CreateSearchUseCase createSearchUseCase;

	public CreateSearchController(CreateSearchUseCase createSearchUseCase) {
		this.createSearchUseCase = createSearchUseCase;
	}

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
