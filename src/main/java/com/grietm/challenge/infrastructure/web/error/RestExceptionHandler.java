package com.grietm.challenge.infrastructure.web.error;

import com.grietm.challenge.domain.exception.DomainValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<String> errors = exception.getBindingResult()
			.getAllErrors()
			.stream()
			.map(error -> error instanceof FieldError fieldError
				? fieldError.getField() + ": " + error.getDefaultMessage()
				: error.getDefaultMessage())
			.toList();

		return new ApiErrorResponse("Request validation failed", errors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleHttpMessageNotReadable() {
		return new ApiErrorResponse(
			"Request body is malformed",
			List.of("Ensure the JSON structure and field formats are valid")
		);
	}

	@ExceptionHandler(DomainValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleDomainValidation(DomainValidationException exception) {
		return new ApiErrorResponse(
			"Request validation failed",
			List.of(exception.getMessage())
		);
	}

}
