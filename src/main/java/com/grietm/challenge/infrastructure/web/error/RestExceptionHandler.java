package com.grietm.challenge.infrastructure.web.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.grietm.challenge.domain.exception.DomainValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class RestExceptionHandler {

	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String REQUEST_VALIDATION_FAILED = "Request validation failed";

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

		return new ApiErrorResponse(REQUEST_VALIDATION_FAILED, errors);
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleBindException(BindException exception) {
		List<String> errors = exception.getBindingResult()
			.getAllErrors()
			.stream()
			.map(error -> error instanceof FieldError fieldError
				? fieldError.getField() + ": " + error.getDefaultMessage()
				: error.getDefaultMessage())
			.toList();

		return new ApiErrorResponse(REQUEST_VALIDATION_FAILED, errors);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleMissingServletRequestParameter(MissingServletRequestParameterException exception) {
		return new ApiErrorResponse(
			REQUEST_VALIDATION_FAILED,
			List.of(exception.getParameterName() + ": " + exception.getParameterName() + " is required")
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		Optional<String> invalidDateField = findInvalidDateField(exception);
		if (invalidDateField.isPresent()) {
			return new ApiErrorResponse(
				REQUEST_VALIDATION_FAILED,
				List.of(invalidDateField.get() + ": invalid date format. Expected format: " + DATE_FORMAT)
			);
		}

		return new ApiErrorResponse(
			"Request body is malformed",
			List.of("Ensure the JSON structure is valid")
		);
	}

	@ExceptionHandler(DomainValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrorResponse handleDomainValidation(DomainValidationException exception) {
		return new ApiErrorResponse(
			REQUEST_VALIDATION_FAILED,
			List.of(exception.getMessage())
		);
	}

	private Optional<String> findInvalidDateField(Throwable throwable) {
		Throwable current = throwable;
		while (current != null) {
			if (current instanceof InvalidFormatException invalidFormatException
				&& invalidFormatException.getTargetType() != null
				&& LocalDate.class.isAssignableFrom(invalidFormatException.getTargetType())) {
				return invalidFormatException.getPath().stream()
					.map(JsonMappingException.Reference::getFieldName)
					.filter(fieldName -> fieldName != null && !fieldName.isBlank())
					.findFirst();
			}
			current = current.getCause();
		}
		return Optional.empty();
	}

}
