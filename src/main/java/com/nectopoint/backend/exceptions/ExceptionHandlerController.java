package com.nectopoint.backend.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class ExceptionHandlerController {

    private MessageSource messageSource;


    public ExceptionHandlerController(MessageSource message){
        this.messageSource = message;
    } 

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorMessageDTO>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        
        List<ErrorMessageDTO> dto = new ArrayList<>();



        exception.getBindingResult().getFieldErrors().forEach(err ->{
            String message = messageSource.getMessage(err, LocaleContextHolder.getLocale());
            ErrorMessageDTO error = new ErrorMessageDTO(message, err.getField());
            dto.add(error);
        });

        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorMessageDTO> handleInvalidEnumValue(InvalidFormatException ex) {
        String fieldName = ex.getPath().get(0).getFieldName();
        String invalidValue = ex.getValue().toString();

        String message = "O valor '" + invalidValue + "' não é válido para o campo '" + fieldName + "'. Os valores permitidos são: ENTRY, LEAVE.";

        return new ResponseEntity<>(new ErrorMessageDTO(message, fieldName), HttpStatus.BAD_REQUEST);
    }

}
