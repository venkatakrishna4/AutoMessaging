package com.krish.automessaging.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.krish.automessaging.datamodel.pojo.exception.ApiError;
import com.krish.automessaging.datamodel.pojo.exception.ApiSubErrors;
import com.krish.automessaging.exception.custom.EmailExistsException;
import com.krish.automessaging.exception.custom.RecordNotFoundException;
import com.krish.automessaging.exception.custom.TooManyRequestsException;

import jakarta.validation.ConstraintViolationException;

/**
 * The Class GlobalExceptionHandler.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /** The Constant TIME_STAMP. */
    private static final String TIME_STAMP = new Date().toString();

    /**
     * Handle http request method not supported.
     *
     * @param ex
     *            the ex
     * @param headers
     *            the headers
     * @param status
     *            the status
     * @param request
     *            the request
     *
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    /**
     * Handle http media type not supported.
     *
     * @param ex
     *            the ex
     * @param headers
     *            the headers
     * @param status
     *            the status
     * @param request
     *            the request
     *
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }

    /**
     * Handle http media type not acceptable.
     *
     * @param ex
     *            the ex
     * @param headers
     *            the headers
     * @param status
     *            the status
     * @param request
     *            the request
     *
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
    }

    /**
     * Handle email exists exception.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Object> handleEmailExistsException(EmailExistsException ex) {
        ApiError apiError = new ApiError("Email already in use. Please use different Email address",
                HttpStatus.CONFLICT, TIME_STAMP, null);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Handle method argument not valid.
     *
     * @param ex
     *            the ex
     * @param headers
     *            the headers
     * @param status
     *            the status
     * @param request
     *            the request
     *
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ApiSubErrors> subErrors = new ArrayList<>();
        ex.getFieldErrors().forEach(
                fieldError -> subErrors.add(new ApiSubErrors(fieldError.getField(), fieldError.getDefaultMessage())));
        ApiError apiError = new ApiError("Please check the sub errors", HttpStatus.BAD_REQUEST, TIME_STAMP, subErrors);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(apiError);
    }

    /**
     * Handle too many requests exception.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsException ex) {
        ApiError apiError = new ApiError("Too Many Requests", HttpStatus.TOO_MANY_REQUESTS, TIME_STAMP, null);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(apiError);
    }

    /**
     * Handle record not found exception.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.NOT_FOUND, TIME_STAMP, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    /**
     * Handle constraint violation exception.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.BAD_REQUEST, TIME_STAMP, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    /**
     * Handle IO exception.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, TIME_STAMP, null);
        return ResponseEntity.internalServerError().body(apiError);
    }

    /**
     * Handle all exceptions.
     *
     * @param ex
     *            the ex
     *
     * @return the response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        ApiError apiError = new ApiError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, TIME_STAMP, null);
        return ResponseEntity.internalServerError().body(apiError);
    }
}
