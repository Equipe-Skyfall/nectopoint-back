package com.nectopoint.backend.modules.user;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity(name="user")
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    private String name;

    @Email(message="Email inválido")
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=()])[A-Za-z\\d@#$%^&+=()]{8,}$",
        message = "Senha deve conter pelo menos 1 letra maiúscula, 1 letra minúscula, 1 número, 1 caractere especial e ter no mínimo 8 caracteres"
    )
    private String password;
    
    private String cpf;
    @Length(max=20)
    private String title;
    private String department;
    private String work_journey_type;
    private String status;
    private Float bank_of_hours;

    
    private Integer daily_hours;
}
