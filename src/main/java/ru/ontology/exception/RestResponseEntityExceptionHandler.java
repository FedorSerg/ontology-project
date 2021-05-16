package ru.ontology.exception;

import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handle400BadRequestException(RuntimeException ex,
                                                                  WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<Object> handle404NotFoundException(RuntimeException ex,
                                                                WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({SQLException.class, NonTransientDataAccessException.class})
    protected ResponseEntity<Object> handle500PsqlException(RuntimeException ex, WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(
                new RuntimeException("SQL exception"), "SQL exception", new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request
        );
    }

    @ExceptionHandler(ParsingException.class)
    protected ResponseEntity<Object> handle500ParsingException(RuntimeException ex,
                                                               WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(
                ex, "File parsing exception", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handle500InternalServerErrorException(RuntimeException ex,
                                                                           WebRequest request) {
        ex.printStackTrace();
        return handleExceptionInternal(
                ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request
        );
    }
}
