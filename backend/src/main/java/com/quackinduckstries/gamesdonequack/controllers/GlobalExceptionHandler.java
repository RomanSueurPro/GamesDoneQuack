package com.quackinduckstries.gamesdonequack.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.quackinduckstries.gamesdonequack.exceptions.AlreadyExistingRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.DuplicateUsernameException;
import com.quackinduckstries.gamesdonequack.exceptions.ExistingPermissionDoesNotExistException;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NewPermissionAlreadyExistsException.class)
    public ResponseEntity<?> handlePermissionExists(NewPermissionAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AlreadyExistingRoleNameException.class)
    public ResponseEntity<?> handlePermissionExists(AlreadyExistingRoleNameException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ExistingPermissionDoesNotExistException.class)
    public ResponseEntity<?> handlePermissionExists(ExistingPermissionDoesNotExistException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handlePermissionExists(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handlePermissionExists(IllegalStateException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<?> handlePermissionExists(DuplicateUsernameException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
