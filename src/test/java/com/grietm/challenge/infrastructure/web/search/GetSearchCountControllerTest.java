package com.grietm.challenge.infrastructure.web.search;

import com.grietm.challenge.application.port.in.GetSearchCountResult;
import com.grietm.challenge.application.port.in.GetSearchCountUseCase;
import com.grietm.challenge.domain.exception.DomainValidationException;
import com.grietm.challenge.domain.model.SearchId;
import com.grietm.challenge.domain.model.SearchCriteria;
import com.grietm.challenge.infrastructure.web.error.RestExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetSearchCountController.class)
@Import(RestExceptionHandler.class)
class GetSearchCountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GetSearchCountUseCase getSearchCountUseCase;

	@Test
	void shouldReturnSearchAndCount() throws Exception {
		when(getSearchCountUseCase.count(any(SearchId.class)))
			.thenReturn(Optional.of(new GetSearchCountResult(
				"search-123",
				new SearchCriteria(
					"hotel-456",
					LocalDate.of(2026, 12, 29),
					LocalDate.of(2026, 12, 31),
					List.of(30, 29, 1, 3)
				),
				2L
			)));

		mockMvc.perform(get("/count")
				.param("searchId", "search-123"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.searchId").value("search-123"))
			.andExpect(jsonPath("$.search.hotelId").value("hotel-456"))
			.andExpect(jsonPath("$.search.checkIn").value("29/12/2026"))
			.andExpect(jsonPath("$.search.checkOut").value("31/12/2026"))
			.andExpect(jsonPath("$.search.ages[0]").value(30))
			.andExpect(jsonPath("$.search.ages[3]").value(3))
			.andExpect(jsonPath("$.count").value(2));

		verify(getSearchCountUseCase).count(new SearchId("search-123"));
	}

	@Test
	void shouldReturnNotFoundWhenSearchDoesNotExist() throws Exception {
		when(getSearchCountUseCase.count(any(SearchId.class))).thenReturn(Optional.empty());

		mockMvc.perform(get("/count")
				.param("searchId", "missing-search"))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldReturnBadRequestWhenSearchIdIsMissing() throws Exception {
		mockMvc.perform(get("/count"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("Request validation failed"))
			.andExpect(jsonPath("$.errors[0]").value("searchId: searchId is required"));

		verify(getSearchCountUseCase, never()).count(any(SearchId.class));
	}

	@Test
	void shouldReturnBadRequestWhenDomainValidationFails() throws Exception {
		when(getSearchCountUseCase.count(any(SearchId.class)))
			.thenThrow(new DomainValidationException("searchId must not be blank"));

		mockMvc.perform(get("/count")
				.param("searchId", "search-123"))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value("Request validation failed"))
			.andExpect(jsonPath("$.errors[0]").value("searchId must not be blank"));
	}

}
