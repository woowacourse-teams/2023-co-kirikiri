package co.kirikiri.controller;

import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException exception) {
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {
        final List<ErrorResponse> errorResponses = makeErrorResponses(exception);
        return ResponseEntity.badRequest().body(errorResponses);
    }

    private List<ErrorResponse> makeErrorResponses(final MethodArgumentNotValidException exception) {
        return exception.getFieldErrors()
                .stream()
                .map(it -> new ErrorResponse(it.getDefaultMessage()))
                .toList();
    }
}
