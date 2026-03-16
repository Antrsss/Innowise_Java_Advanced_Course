package by.zgirskaya.advanced_course.controller;

import by.zgirskaya.advanced_course.exception.CardServiceException;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({UserServiceException.class, CardServiceException.class})
  public ResponseEntity<Object> handleCustomExceptions(Exception ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("message", ex.getMessage());

    HttpStatus status = ex.getMessage().contains("not found")
        ? HttpStatus.NOT_FOUND
        : HttpStatus.BAD_REQUEST;

    return new ResponseEntity<>(body, status);
  }
}
