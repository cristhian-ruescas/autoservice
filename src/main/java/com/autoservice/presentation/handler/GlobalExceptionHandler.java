package com.autoservice.presentation.handler;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.presentation.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class, ServletException.class})
    public ResponseEntity<?> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(java.util.Map.of("error", "Credenciais inválidas"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.add(new ErrorResponse.FieldError(fieldName, errorMessage));
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getRequestURI(),
                LocalDateTime.now(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex,
            HttpServletRequest request) {

        final var errors = ex.getErrors().stream()
                .map(error -> new ErrorResponse.FieldError(null, error.message()))
                .toList();
        final var message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Erro de domínio"
                : ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            final HttpMessageNotReadableException ex,
            final HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Corpo da requisição JSON inválido",
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            final MethodArgumentTypeMismatchException ex,
            final HttpServletRequest request
    ) {
        final var message = ex.getName() == null
                ? "Parâmetro de requisição inválido"
                : "Parâmetro inválido: " + ex.getName();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() == null ? "Requisição inválida" : ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        final var message = resolveDataIntegrityMessage(ex);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                message,
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of(new ErrorResponse.FieldError(null, message))
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private String resolveDataIntegrityMessage(final DataIntegrityViolationException ex) {
        final var rootMessage = ex.getMostSpecificCause() == null
                ? null
                : ex.getMostSpecificCause().getMessage();

        if (rootMessage != null && rootMessage.toLowerCase().contains("duplicate")) {
            return "Registro duplicado para os dados informados";
        }
        if (rootMessage != null && rootMessage.toLowerCase().contains("foreign key")) {
            return "Operação inválida por dependência de outro registro";
        }

        return "Dados inválidos para gravação";
    }
}
