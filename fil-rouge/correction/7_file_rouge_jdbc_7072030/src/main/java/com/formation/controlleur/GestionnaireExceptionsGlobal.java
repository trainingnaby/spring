package com.formation.controlleur;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.formation.exception.ErreurResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
// Ce gestionnaire d'exceptions s'appliquera pour toutes les classes annotées avec  @Controllers ou @RestControllers 
public class GestionnaireExceptionsGlobal {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErreurResponse handlemethodArgumentNotValid(MethodArgumentNotValidException exception,
			HttpServletRequest request) { // 
		
		String message  = exception.getBindingResult().getFieldErrors()
				.stream()
				.map(error -> error.getField() + error.getDefaultMessage())
				.collect(Collectors.joining(";"));
		
		return new ErreurResponse(HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ErreurResponse handleConstraintViolation(ConstraintViolationException exception,
			HttpServletRequest request) { // 
		String message  = exception.getConstraintViolations()
				.stream()
				.map(error -> error.getPropertyPath() + " : " + error.getMessage())
				.collect(Collectors.joining(";"));
		
		return new ErreurResponse(HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI());
	}
}