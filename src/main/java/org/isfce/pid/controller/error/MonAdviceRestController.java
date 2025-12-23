package org.isfce.pid.controller.error;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MonAdviceRestController {

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> gestionErreurNotFound(NoSuchElementException exc) {

		return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
		 * Permet de capturer les problèmes de doublons et de retourner un objet avec les erreurs
		 * @param exc
		 * @return
		 */
	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<Map<String, String>> gestionErreurDupplicate(DuplicateException exc) {

		return ResponseEntity.badRequest().body(Map.of(exc.getChamp(), exc.getMessage()));
	}

	/**
	 * Permet de capturer les erreurs de validation et de retourner un objet avec les erreurs
	 * @param exc
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> gestionErreurValidationExceptions(MethodArgumentNotValidException exc) {
		Map<String, String> errors = new HashMap<>();

		exc.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.badRequest().body(errors);
	}

}
