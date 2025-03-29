package com.myproject.tasksystem.advices;

import com.myproject.tasksystem.annotations.UserExceptionHandler;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.util.ErrorItem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice(annotations = UserExceptionHandler.class)
public class UserAdvice {

    //Если данные не прошли валидацию @Valid, @NotNull, @Size, @Pattern и т.д.
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity <ErrorItem> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorItem error = new ErrorItem();
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList())
                .toString();
        error.setMessage(errors);
        error.setTimestamp(formatDate());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class}) //Если сохранять объект с некорректными данными (например, name = null или password короче 5 символов)
    public ResponseEntity <ErrorItem> handleValidationException(ConstraintViolationException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //Не пропустит одинаковый email => UniqueConstraint(columnNames = "email")
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity <ErrorItem> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity <ErrorItem> handleNotFoundException(NotFoundException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity <ErrorItem> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity <ErrorItem> handleNoSuchElementException(NoSuchElementException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({AuthorizationDeniedException.class})
    public ResponseEntity <ErrorItem> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ErrorItem error = generateMessage(e);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    public ErrorItem generateMessage(Exception e){
        ErrorItem error = new ErrorItem();
        error.setTimestamp(formatDate());
        error.setMessage(e.getMessage());
        return error;
    }

    public String formatDate(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String data = dateTimeFormatter.format( LocalDateTime.now() );
        return data;
    }
}