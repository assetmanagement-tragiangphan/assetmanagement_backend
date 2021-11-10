package com.nashtech.rookies.AssetManagement.exception;

import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {


    @ExceptionHandler(NotFoundException.class)

    public ResponseEntity<Object> notFoundHandler(NotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Entity not found!");
        body.put("status",HttpStatus.NOT_FOUND);
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
//    @ExceptionHandler(EntityPrimaryKeyExistsException.class)
//
//    public ResponseEntity<Object> foundHandler(EntityPrimaryKeyExistsException ex) {
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("error", "Primary key has existed!");
//        body.put("status",HttpStatus.FOUND);
//        body.put("message", ex.getMessage());
//        return new ResponseEntity<>(body, HttpStatus.FOUND);
//    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
            , HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        ex.getFieldErrors().forEach(e -> details.add(e.getField() + " " + e.getDefaultMessage()));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Validation fail");
        body.put("status",status);
        body.put("message", details);
        //body.put("errors", details);
        return new ResponseEntity(body, status);
    }

    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<Object> hibernateHandler(HibernateException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "SQL error!");
        body.put("status",HttpStatus.CONFLICT);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> hibernateHandler(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("error", "Constraint Violation Error!");
        body.put("status",HttpStatus.CONFLICT);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(NumberErrorException.class)
    public ResponseEntity<Object> numberHandler(NumberErrorException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status",HttpStatus.NOT_ACCEPTABLE);
        body.put("error", "Number format error!");
        return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
    }
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex
            , HttpHeaders headers, HttpStatus status, WebRequest request) {


        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("errors", ex.getPropertyName() + " require " + ex.getRequiredType());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

}