package com.formation.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
// Ce gestionnaire d'exceptions s'appliquera pour toutes les classes annotées avec  @Controllers ou @RestControllers 
public class GestionnaireExceptionsGlobal {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String handlemethodArgumentNotValid(MethodArgumentNotValidException exception) { // 
		// TODO vous pouvez retourner un objet custom qui sera transformée en json ou xml
		return "La requête n'a pas les bonnes paramètres ! " + exception.getMessage();
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public String handleConstraintViolation(ConstraintViolationException exception) { // 
		// TODO vous pouvez retourner un objet custom qui sera transformée en json ou xml
		return "La requête n'a pas les bonnes paramètres !" + exception.getMessage();
	}
}