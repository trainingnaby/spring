package com.formation.web;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.formation.exception.DuplicataNotFoundException;
import com.formation.exception.InvalidSearchCriteriaException;
import com.formation.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Gestion globale des exceptions pour les controles REST.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody.
 * Les objets retournes sont donc serialises en JSON.
 *
 * Ici, on utilise ProblemDetail, introduit dans Spring 6, pour produire une
 * reponse compatible RFC 9457 : type, title, status, detail, instance.
 */
@RestControllerAdvice(basePackages = "com.formation.web")
public class RestExceptionHandler {

    @ExceptionHandler(DuplicataNotFoundException.class)
    public ProblemDetail handleDuplicataNotFound(DuplicataNotFoundException exception, HttpServletRequest request) {
        ProblemDetail problem = creerProblemDetail(HttpStatus.NOT_FOUND,
                "Duplicata introuvable",
                exception.getMessage(),
                request,
                "duplicata-not-found");
        problem.setProperty("duplicataId", exception.getDuplicataId());
        return problem;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        ProblemDetail problem = creerProblemDetail(HttpStatus.NOT_FOUND,
                "Utilisateur fiscal introuvable",
                exception.getMessage(),
                request,
                "user-not-found");
        problem.setProperty("userId", exception.getUserId());
        return problem;
    }

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    public ProblemDetail handleInvalidSearchCriteria(InvalidSearchCriteriaException exception,
            HttpServletRequest request) {
        return creerProblemDetail(HttpStatus.BAD_REQUEST,
                "Critere de recherche invalide",
                exception.getMessage(),
                request,
                "invalid-search-criteria");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBodyValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .toList();

        ProblemDetail problem = creerProblemDetail(HttpStatus.BAD_REQUEST,
                "Erreur de validation du corps JSON",
                "Le corps de la requete contient des donnees invalides.",
                request,
                "body-validation-error");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + " : " + violation.getMessage())
                .toList();

        ProblemDetail problem = creerProblemDetail(HttpStatus.BAD_REQUEST,
                "Erreur de validation des parametres",
                "Un ou plusieurs parametres de la requete sont invalides.",
                request,
                "parameter-validation-error");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleHandlerMethodValidation(HandlerMethodValidationException exception,
            HttpServletRequest request) {
        ProblemDetail problem = creerProblemDetail(HttpStatus.BAD_REQUEST,
                "Erreur de validation des parametres",
                "Un ou plusieurs parametres de la requete sont invalides.",
                request,
                "method-validation-error");
        problem.setProperty("errors", List.of(exception.getMessage()));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception exception, HttpServletRequest request) {
        ProblemDetail problem = creerProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur technique inattendue",
                "Une erreur technique est survenue. Contactez le support si le probleme persiste.",
                request,
                "technical-error");
        problem.setProperty("exception", exception.getClass().getSimpleName());
        return problem;
    }

    private ProblemDetail creerProblemDetail(HttpStatus status, String title, String detail,
            HttpServletRequest request, String typeSuffix) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://formation.example/problems/" + typeSuffix));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}
