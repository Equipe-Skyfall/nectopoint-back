package com.nectopoint.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO = Data transfer Object
@Data
@AllArgsConstructor
public class ErrorMessageDTO {
    private String message;
    private String field;
}
