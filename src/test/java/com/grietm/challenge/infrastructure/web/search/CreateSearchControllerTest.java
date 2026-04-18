package com.grietm.challenge.infrastructure.web.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grietm.challenge.application.port.in.CreateSearchCommand;
import com.grietm.challenge.application.port.in.CreateSearchResult;
import com.grietm.challenge.application.port.in.CreateSearchUseCase;
import com.grietm.challenge.domain.exception.DomainValidationException;
import com.grietm.challenge.infrastructure.web.error.RestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreateSearchController.class)
@Import(RestExceptionHandler.class)
class CreateSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CreateSearchUseCase createSearchUseCase;

	@Test
	void shouldCreateSearchAndReturnCreatedResponse() throws Exception {
		when(createSearchUseCase.create(any(CreateSearchCommand.class)))
			.thenReturn(new CreateSearchResult("search-123"));

		mockMvc.perform(post("/search")
				.contentType(APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(new CreateSearchRequest(
					"hotel-123",
					LocalDate.of(2026, 12, 29),
					LocalDate.of(2026, 12, 31),
					List.of(30, 29, 1, 3)
				))))
			.andExpect(status().isCreated())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.searchId").value("search-123"));

		CreateSearchCommand expectedCommand = new CreateSearchCommand(
			"hotel-123",
			LocalDate.of(2026, 12, 29),
			LocalDate.of(2026, 12, 31),
			List.of(30, 29, 1, 3)
		);

		verify(createSearchUseCase).create(expectedCommand);
	}

	@Test
	void shouldReturnBadRequestWhenRequestValidationFails() throws Exception {
		mockMvc.perform(post("/search")
				.contentType(APPLICATION_JSON)
				.content("""
					{
					  "hotelId": " ",
					  "checkIn": null,
					  "checkOut": "2026-12-31",
					  "ages": [-1]
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("Request validation failed"))
			.andExpect(jsonPath("$.errors[0]").exists())
			.andExpect(jsonPath("$.errors[*]").value(org.hamcrest.Matchers.hasItems(
				"hotelId: hotelId must not be blank",
				"checkIn: checkIn must not be null",
				"ages[0]: ages must contain only values greater than or equal to 0"
			)));

		verify(createSearchUseCase, never()).create(any(CreateSearchCommand.class));
	}

	@Test
	void shouldReturnBadRequestWhenDomainValidationFails() throws Exception {
		when(createSearchUseCase.create(any(CreateSearchCommand.class)))
			.thenThrow(new DomainValidationException("checkIn must be earlier than checkOut"));

		mockMvc.perform(post("/search")
				.contentType(APPLICATION_JSON)
				.content("""
					{
					  "hotelId": "hotel-123",
					  "checkIn": "2026-12-31",
					  "checkOut": "2026-12-29",
					  "ages": [30]
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("Request validation failed"))
			.andExpect(jsonPath("$.errors[0]").value("checkIn must be earlier than checkOut"));
	}

	@Test
	void shouldReturnBadRequestWhenRequestBodyIsMalformed() throws Exception {
		mockMvc.perform(post("/search")
				.contentType(APPLICATION_JSON)
				.content("""
					{
					  "hotelId": "hotel-123",
					  "checkIn": "invalid-date",
					  "checkOut": "2026-12-31",
					  "ages": [30]
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("Request body is malformed"))
			.andExpect(jsonPath("$.errors[0]").value("Ensure the JSON structure and field formats are valid"));
	}

}
