package com.formation.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice // définit une classe qui sera 
	// à l'écoute de toutes les exceptions lancées par les controlleurs rest
public class GestionnaireExceptions {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String handleMethodArgumentNotValideException(MethodArgumentNotValidException ex) {
		
		// TODO catcher l'exception et transformer en json plus parlant au client
		return "Les arguments ne sont pas conformes !";
		
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public String handleMethodConstraintViolationException(ConstraintViolationException ex) {
		
		// TODO catcher l'exception et transformer en json plus parlant au client
		return "Violation d'une contrainte !";
		
	}


}
