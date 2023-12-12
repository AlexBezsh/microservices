package com.alexbezsh.microservices.common.aspect;

import com.alexbezsh.microservices.common.exception.NotAvailableException;
import com.alexbezsh.microservices.common.exception.NotFoundException;
import com.alexbezsh.microservices.common.exception.ValidationException;
import com.alexbezsh.microservices.common.model.ErrorResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import static com.alexbezsh.microservices.common.utils.JsonUtils.fromJson;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toCollection(ArrayList::new));
        e.getBindingResult().getGlobalErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .forEach(errors::add);
        return badRequest(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentTypeMismatchException e) {
        return badRequest(List.of(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(ConstraintViolationException e) {
        List<String> errors = e.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList();
        return badRequest(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(ValidationException e) {
        return badRequest(List.of(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(NotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception e) {
        String message = "Unexpected error. Reason: " + e.getMessage();
        return new ErrorResponse(INTERNAL_SERVER_ERROR, message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handle(NotAvailableException e) {
        String message = "Currently service is unavailable. Reason: " + e.getMessage();
        return new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, message);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(FeignException e) {
        int status = e.status();
        ErrorResponse response = e.responseBody()
            .map(ByteBuffer::array)
            .map(String::new)
            .map(s -> parse(s, status))
            .orElse(new ErrorResponse(resolve(status), "Unknown client error"));
        return ResponseEntity.status(status)
            .body(response);
    }

    private ErrorResponse badRequest(List<String> errors) {
        String message = String.join("; ", errors);
        return new ErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    private static ErrorResponse parse(String body, int status) {
        try {
            return fromJson(body, ErrorResponse.class);
        } catch (Exception e) {
            return new ErrorResponse(resolve(status), body);
        }
    }

    private static HttpStatus resolve(int status) {
        return Optional.ofNullable(HttpStatus.resolve(status))
            .orElse(INTERNAL_SERVER_ERROR);
    }

}
