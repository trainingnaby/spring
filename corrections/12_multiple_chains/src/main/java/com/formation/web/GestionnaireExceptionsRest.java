package com.formation.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.formation.exception.UserException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GestionnaireExceptionsRest {

	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST) // code qui sera renvoyé par le serveur
	@ExceptionHandler(MethodArgumentNotValidException.class) // ecoute des exceptions de type MethodArgumentNotValidException
	public String processMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		// Construire un object json avec tous les details de l"exception
		String result = "Erreur de type MethodArgumentNotValidException :"+ex.getMessage();
		return result;
	}
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST) // code qui sera renvoyé par le serveur
	@ExceptionHandler(ConstraintViolationException.class) // ecoute des exceptions de type MethodArgumentNotValidException
	public String processMethodArgumentConstraintViolationException(ConstraintViolationException ex) {
		// Construire un object json avec tous les details de l"ConstraintViolationException
		return "Erreur de type ConstraintViolationException :"+ex.getMessage();
	}
	
	@ResponseStatus(code = HttpStatus.BAD_REQUEST) // code qui sera renvoyé par le serveur
	@ExceptionHandler(UserException.class) // ecoute des exceptions de type MethodArgumentNotValidException
	public String processUserException(UserException ex) {
		// Construire un object json avec tous les details de l"exception
		return "Erreur de type UserException :"+ex.getMessage();
	}
	
}
