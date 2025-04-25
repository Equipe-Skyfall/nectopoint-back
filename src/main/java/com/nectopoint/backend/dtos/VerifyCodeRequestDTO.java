package com.nectopoint.backend.dtos;



import lombok.Data;

@Data
public class VerifyCodeRequestDTO {
    private String userId;
    private String verificationCode;
    

}