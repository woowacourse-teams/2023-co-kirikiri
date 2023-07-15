package co.kirikiri.controller;

import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.dto.ErrorResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException exception) {
        log.warn(exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException exception) {
        log.warn(exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(final ConflictException exception) {
        log.warn(exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
        final List<ErrorResponse> errorResponses = makeErrorResponses(exception);
        return ResponseEntity.badRequest().body(errorResponses);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundException(final NotFoundException exception) {
        log.warn(exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(final Exception exception) {
        log.error(exception.getMessage(), exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private List<ErrorResponse> makeErrorResponses(final MethodArgumentNotValidException exception) {
        return exception.getFieldErrors()
            .stream()
            .map(it -> new ErrorResponse(it.getDefaultMessage()))
            .toList();
    }
}
