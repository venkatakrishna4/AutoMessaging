package com.krish.automessaging.exception;

import com.krish.automessaging.datamodel.pojo.exception.ApiError;
import com.krish.automessaging.datamodel.pojo.exception.ApiSubErrors;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import org.springframework.http.*;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String TIME_STAMP = new Date().toString();

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Object> handleEmailExistsException(EmailExistsException ex) {
        ApiError apiError = ApiError.builder().message("Email already in use. Please use different Email address")
                .timestamp(TIME_STAMP).status(HttpStatus.CONFLICT).build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ApiSubErrors> subErrors = new ArrayList<>();
        ex.getFieldErrors().forEach(fieldError -> subErrors.add(
                ApiSubErrors.builder().field(fieldError.getField()).message(fieldError.getDefaultMessage()).build()));
        ApiError apiError = ApiError.builder().message("Please check the sub errors").status(status)
                .timestamp(TIME_STAMP).subErrors(subErrors).build();
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(apiError);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        ApiError apiError = ApiError.builder().status(HttpStatus.NOT_FOUND).subErrors(null).message(ex.getMessage())
                .timestamp(TIME_STAMP).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        ApiError apiError = ApiError.builder().message(ex.getMessage()).timestamp(TIME_STAMP)
                .status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.internalServerError().body(apiError);
    }
}
