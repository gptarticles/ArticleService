package me.zedaster.articleservice.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import me.zedaster.articleservice.dto.error.ErrorDto;
import me.zedaster.articleservice.dto.error.ValidationErrorDto;
import me.zedaster.articleservice.service.ArticleServiceException;
import me.zedaster.articleservice.service.CreatorServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

/**
 * Exception handlers for controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles validation exceptions.
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ConstraintViolationException e) {
        Map<String, String> errorsByField = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
                    Spliterator<Path.Node> spliterator = violation.getPropertyPath().spliterator();
                    String name = StreamSupport.stream(spliterator, false).findFirst().get().getName();
                    errorsByField.put(name, violation.getMessage());
                }
        );
        ValidationErrorDto errorDto = new ValidationErrorDto(errorsByField);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles not found exceptions.
     * @param exception The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions that can be shown with a simple error message.
     * @param exception The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler({ArticleServiceException.class, CreatorServiceException.class})
    public ResponseEntity<ErrorDto> handleSimpleExceptions(Exception exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
