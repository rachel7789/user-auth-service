package com.example.rachelklein.userauth.exception;

import com.example.rachelklein.userauth.dto.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(400);
        body.setErrorCode("DUPLICATE_EMAIL");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotVerified(AccountNotVerifiedException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(403);
        body.setErrorCode("ACCOUNT_NOT_VERIFIED");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(401);
        body.setErrorCode("INVALID_CREDENTIALS");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {

        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(401);
        error.setErrorCode("INVALID_TOKEN");
        error.setErrorMessage("Invalid or expired token");
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {

        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(404);
        error.setErrorCode("USER_NOT_FOUND");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidResetToken(InvalidResetTokenException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(400);
        error.setErrorCode("INVALID_TOKEN");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ResetTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleResetTokenExpired(ResetTokenExpiredException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(400);
        error.setErrorCode("EXPIRED_TOKEN");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingAuthHeader(MissingAuthorizationHeaderException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(401);
        error.setErrorCode("MISSING_AUTHORIZATION_HEADER");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationToken(InvalidVerificationTokenException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(400);
        error.setErrorCode("INVALID_TOKEN");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpired(VerificationTokenExpiredException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(400);
        error.setErrorCode("EXPIRED_TOKEN");
        error.setErrorMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactive(AccountInactiveException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(403);
        body.setErrorCode("ACCOUNT_INACTIVE");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {

        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(400);
        error.setErrorCode("INVALID_REQUEST");
        error.setErrorMessage("Malformed JSON or invalid field format");
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(400);
        body.setErrorCode("BAD_REQUEST");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = "Validation error";
        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getField() + ": " +
                    ex.getBindingResult().getFieldError().getDefaultMessage();
        }

        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(400);
        body.setErrorCode("VALIDATION_ERROR");
        body.setErrorMessage(message);
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(400);
        body.setErrorCode("VALIDATION_ERROR");
        body.setErrorMessage(ex.getMessage());
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ex.printStackTrace();

        ErrorResponse body = new ErrorResponse();
        body.setStatusCode(500);
        body.setErrorCode("SERVER_ERROR");
        body.setErrorMessage("Unexpected server error");
        body.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
