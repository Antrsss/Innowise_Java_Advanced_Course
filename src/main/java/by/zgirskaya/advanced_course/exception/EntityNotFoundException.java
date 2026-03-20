package by.zgirskaya.advanced_course.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseServiceException {
  public EntityNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }
}