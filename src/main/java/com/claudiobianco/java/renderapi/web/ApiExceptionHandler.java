package com.claudiobianco.java.renderapi.web;

import com.claudiobianco.java.renderapi.domain.ItemNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ItemNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse("ITEM_NOT_FOUND", ex.getMessage()));
    }
}
