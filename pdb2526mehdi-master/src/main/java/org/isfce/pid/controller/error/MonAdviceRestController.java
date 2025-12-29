package org.isfce.pid.controller.error;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Intercepteur global des exceptions pour l'API REST.
 * Capture les erreurs techniques et fonctionnelles pour les transformer
 * en réponses HTTP normalisées.
 */
@Slf4j
@RestControllerAdvice
public class MonAdviceRestController {

    /**
     * Traite les exceptions de type "Ressource non trouvée" (404).
     *
     * @param exc L'exception levée (NoSuchElementException ou EntityNotFoundException).
     * @return Une ResponseEntity contenant le message d'erreur et le statut 404 NOT FOUND.
     */
    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<String> gestionErreurNotFound(Exception exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Traite les erreurs de validation des arguments (@Valid).
     *
     * @param exc L'exception contenant la liste des violations de contraintes.
     * @return Une Map associant chaque champ en erreur à son message, avec le statut 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> gestionErreurValidationExceptions(MethodArgumentNotValidException exc) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Traite les exceptions de doublons métiers.
     *
     * @param exc L'exception DuplicateException indiquant le champ conflictuel.
     * @return Une Map contenant le champ et le message d'erreur, avec le statut 400 BAD REQUEST.
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, String>> gestionErreurDupplicate(DuplicateException exc) {
        return ResponseEntity.badRequest().body(Map.of(exc.getChamp(), exc.getMessage()));
    }

    /**
     * Traite les erreurs de logique métier ou d'état invalide.
     *
     * @param exc L'exception runtime (IllegalArgumentException ou IllegalStateException).
     * @return Le message d'erreur explicatif avec le statut 400 BAD REQUEST.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> gestionErreurLogique(RuntimeException exc) {
        return ResponseEntity.badRequest().body(exc.getMessage());
    }

    /**
     * Traite les tentatives d'accès non autorisées ou interdites.
     * Cette méthode loggue l'incident comme une alerte de sécurité.
     *
     * @param ex L'exception de sécurité levée.
     * @return Un message d'accès interdit avec le statut 403 FORBIDDEN.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        log.error("ALERTE SÉCURITÉ : Access denied - {}", ex.getMessage());
        return new ResponseEntity<>("Accès interdit : " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}