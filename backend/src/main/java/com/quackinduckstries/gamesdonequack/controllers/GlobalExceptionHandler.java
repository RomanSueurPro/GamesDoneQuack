package com.quackinduckstries.gamesdonequack.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.quackinduckstries.gamesdonequack.exceptions.AlreadyExistingRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.exceptions.EmptyPermissionNameException;
import com.quackinduckstries.gamesdonequack.exceptions.EmptyRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.ExistingPermissionDoesNotExistException;
import com.quackinduckstries.gamesdonequack.exceptions.MultipleErrorsException;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NewPermissionAlreadyExistsException.class)
    public ResponseEntity<?> handleNewPermissionAlreadyExists(NewPermissionAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AlreadyExistingRoleNameException.class)
    public ResponseEntity<?> handleAlreadyExistingRoleName(AlreadyExistingRoleNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ExistingPermissionDoesNotExistException.class)
    public ResponseEntity<?> handleExistingPermissionDoesNotExist(ExistingPermissionDoesNotExistException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<?> handleDuplicateUsername(DuplicateUsernameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
    
    @ExceptionHandler(EmptyRoleNameException.class)
    public ResponseEntity<?> handleEmptyRoleName(EmptyRoleNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
    
    @ExceptionHandler(EmptyPermissionNameException.class)
    public ResponseEntity<?> handleEmptyPermissionName(EmptyPermissionNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MultipleErrorsException.class)
    public ResponseEntity<Map<String, Object>> handleMultipleErrors(MultipleErrorsException e) {
    	
    	Map<String, Object> body = new HashMap<>();
    	body.put("errors", e.getErrors());
    	return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
