package ru.nsu.ccfit.muratov.hello.there.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.nsu.ccfit.muratov.hello.there.entity.ErrorDto;
import ru.nsu.ccfit.muratov.hello.there.exception.AccessDeniedException;
import ru.nsu.ccfit.muratov.hello.there.exception.BadRequestException;
import ru.nsu.ccfit.muratov.hello.there.exception.ResourceNotFoundException;

import java.util.Date;

@ControllerAdvice
public class ErrorControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(ResourceNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDto(new Date(), exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException exception) {
        return new ResponseEntity<>(new ErrorDto(new Date(), exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(BadRequestException exception) {
        return new ResponseEntity<>(new ErrorDto(new Date(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
