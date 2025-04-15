package com.nectopoint.backend.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    // Store codes with expiration (in a production app, you'd use Redis or a database)
    private final Map<String, CodeInfo> codes = new ConcurrentHashMap<>();
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 10;

    private static class CodeInfo {
        String code;
        LocalDateTime expiration;

        //informações sobre o código de verificação
        CodeInfo(String code) {
            this.code = code; //código
            this.expiration = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);//data de expiração (10min)
        }

        boolean isValid(String attemptedCode) {
            return code.equals(attemptedCode) && LocalDateTime.now().isBefore(expiration);
        }
    }

    //Gera o código aleatório que fica salvo na memória do backend
    public String generateCode(String userId) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        
        String code = sb.toString();
        codes.put(userId, new CodeInfo(code));// adiciona o código e o id do usuário em um hashmap na MEMÓRIA DO BACKEND
        
        return code;
    }

    public boolean verifyCode(String userId, String code) {
        CodeInfo codeInfo = codes.get(userId);// PEGA O CÓDIGO NA MEMÓRIA DO BACKEND
        
        if (codeInfo != null && codeInfo.isValid(code)) {
            codes.remove(userId); //  remove o código após a autenticação
            return true;
        }
        
        return false;
    }
}