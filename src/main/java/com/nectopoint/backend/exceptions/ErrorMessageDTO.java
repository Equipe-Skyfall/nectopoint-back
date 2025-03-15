package com.nectopoint.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO = Data transfer Object
@Data
@AllArgsConstructor
public class ErrorMessageDTO {
    private String message;
    private String field;
}
