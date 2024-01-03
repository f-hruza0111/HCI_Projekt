package com.example.HCIProject.controller;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<?> conflict(PSQLException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }


}
