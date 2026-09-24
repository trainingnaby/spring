package com.formation.mvc.advice;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.formation.exception.DuplicataNotFoundException;
import com.formation.exception.InvalidSearchCriteriaException;
import com.formation.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestion globale des exceptions pour les controleurs MVC Thymeleaf.
 *
 * @ControllerAdvice permet de retourner une vue HTML.
 * Contrairement a @RestControllerAdvice, l'objet retourne n'est pas transforme
 * automatiquement en JSON.
 */
@ControllerAdvice(basePackages = "com.formation.mvc")
public class MvcExceptionHandler {

    @ExceptionHandler(DuplicataNotFoundException.class)
    public ModelAndView handleDuplicataNotFound(DuplicataNotFoundException exception, HttpServletRequest request) {
        return pageErreur("Duplicata introuvable", exception.getMessage(), request.getRequestURI(), 404);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return pageErreur("Utilisateur fiscal introuvable", exception.getMessage(), request.getRequestURI(), 404);
    }

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    public ModelAndView handleInvalidSearchCriteria(InvalidSearchCriteriaException exception, HttpServletRequest request) {
        return pageErreur("Critere invalide", exception.getMessage(), request.getRequestURI(), 400);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException(Exception exception, HttpServletRequest request) {
        return pageErreur("Erreur technique", "Une erreur technique est survenue.", request.getRequestURI(), 500);
    }

    private ModelAndView pageErreur(String titre, String message, String path, int status) {
        ModelAndView modelAndView = new ModelAndView("error/functional-error");
        modelAndView.addObject("titre", titre);
        modelAndView.addObject("message", message);
        modelAndView.addObject("path", path);
        modelAndView.addObject("status", status);
        modelAndView.addObject("timestamp", LocalDateTime.now());
        return modelAndView;
    }
}
