package by.zgirskaya.advanced_course.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseServiceException extends Exception {
  private final HttpStatus status;

  BaseServiceException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}
