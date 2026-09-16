package org.devbank.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiErrorResponse> handleBadRequest(
          BadRequestException exception,
          HttpServletRequest request) {

    return buildResponse(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            request.getRequestURI()
    );
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiErrorResponse> handleUnauthorized(
          UnauthorizedException exception,
          HttpServletRequest request) {

    return buildResponse(
            HttpStatus.UNAUTHORIZED,
            exception.getMessage(),
            request.getRequestURI()
    );
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiErrorResponse> handleForbidden(
          ForbiddenException exception,
          HttpServletRequest request) {

    return buildResponse(
            HttpStatus.FORBIDDEN,
            exception.getMessage(),
            request.getRequestURI()
    );
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
          NotFoundException exception,
          HttpServletRequest request) {

    return buildResponse(
            HttpStatus.NOT_FOUND,
            exception.getMessage(),
            request.getRequestURI()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
          MethodArgumentNotValidException exception,
          HttpServletRequest request) {

    String message = exception
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(error ->
                    error.getField()
                            + ": "
                            + error.getDefaultMessage()
            )
            .orElse("Validation failed");

    return buildResponse(
            HttpStatus.BAD_REQUEST,
            message,
            request.getRequestURI()
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneralException(
          Exception exception,
          HttpServletRequest request) {

    return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            request.getRequestURI()
    );
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(
          HttpStatus status,
          String message,
          String path) {

    ApiErrorResponse response =
            new ApiErrorResponse(
                    LocalDateTime.now(),
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    path
            );

    return ResponseEntity
            .status(status)
            .body(response);
  }
}