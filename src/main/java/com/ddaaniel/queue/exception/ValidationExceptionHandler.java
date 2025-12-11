package com.ddaaniel.queue.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValidationExceptionHandler {

  /*
   * @param ex RecursoNaoEncontradoException exception class captured
   *
   * @return status HTTP 404 NOT FOUND.
   */
  @ExceptionHandler(RecursoNaoEncontradoException.class)
  public ResponseEntity<String> handleRecursonNotFoundException(RecursoNaoEncontradoException ex) {
    // Retorna o status 404 e a mensagem da exceção no corpo da resposta
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  /*
   * @param ex ConflitoDeStatusException excepetion class captured
   *
   * @return status HTTP 409 CONFLICT
   */
  @ExceptionHandler(ConflitoDeStatusException.class)
  public ResponseEntity<String> handleConflitoDeStatusException(ConflitoDeStatusException ex) {
    // Retorna o status 409 e a mensagem da exceção no corpo da resposta
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /*
   * @param ex IllegalStateException exceprion class captured
   *
   * @return status HTTP 400 BAD_REQUEST
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /*
   * @param ex MethodArgumentNotValidException exception class captured in methods with annotation '@Valid' or '@Validated'
   * that his Object in requestBody or model attribute has Bean Validation annotations (@NotNull, @NotEmpty, @Size, @Pattern, @CPF, etc.)
   *
   * @return Map<String, String> with status HTTP 400 BAD_REQUEST
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {

    Map<String, String> errorsMap = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      errorsMap.put(fieldName, errorMessage);
    });
    return errorsMap;
  }

}
