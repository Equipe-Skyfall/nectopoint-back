package com.nectopoint.backend.exceptions;


//Sendo utilizado para evitar CPF,EMAIL e Número de funcionário duplicados
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
