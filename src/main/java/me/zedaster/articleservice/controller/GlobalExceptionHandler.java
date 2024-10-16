package me.zedaster.articleservice.controller;

import me.zedaster.articleservice.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handlers for controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles {@link IllegalArgumentException}
     * @param exception The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
