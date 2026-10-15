package com.hostel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ApiException.class)
  ResponseEntity<Map<String, Object>> api(ApiException e) { return ResponseEntity.status(e.status).body(Map.of("message", e.getMessage())); }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
    Map<String, String> errs = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach(f -> errs.putIfAbsent(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.badRequest().body(Map.of("message", "Please fix the highlighted fields", "errors", errs));
  }
  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<Map<String, Object>> dup(DataIntegrityViolationException e) { return ResponseEntity.status(409).body(Map.of("message", "Duplicate value (email or room number already exists)")); }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<Map<String, Object>> bad(HttpMessageNotReadableException e) { return ResponseEntity.badRequest().body(Map.of("message", "Malformed request")); }
  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<Map<String, Object>> other(RuntimeException e) { return ResponseEntity.status(500).body(Map.of("message", "Something went wrong")); }
}
