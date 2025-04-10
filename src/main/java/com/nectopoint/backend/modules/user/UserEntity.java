package com.nectopoint.backend.modules.user;


import java.time.LocalDate;

import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.enums.TipoEscala;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity(name="user")
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O campo nome não pode ser nulo ou vazio")
    private String name;

    @Column(unique = true, nullable = false)
    @Email(message = "Email inválido")
    @NotBlank(message = "O email não pode ser nulo ou vazio")
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,}$",
        message = "Senha deve conter pelo menos 1 letra maiúscula, 1 letra minúscula, 1 número, 1 caractere especial e ter no mínimo 8 caracteres"
    )
    private String password;
    
    @Column(unique = true,nullable = false)
    @NotBlank(message = "O campo cpf não pode ser nulo ou vazio")
    private String cpf;
   
    @Column(nullable = false)
    @NotNull(message = "O campo cargo não pode ser nulo")
    private TipoCargo title;

    @Column(nullable = false)
    @NotBlank(message = "O campo departamento não pode ser nulo ou vazio")
    private String department;
    
    @Column(nullable = false)
    @NotBlank(message = "O campo jornada de trabalho não pode ser nulo ou vazio")
    private String workJourneyType;
    
    @Column(nullable = false,unique=true)
    @NotBlank(message = "O campo número do funcionário não pode ser nulo ou vazio")
    private String employeeNumber;
    
    @Column(nullable = false)
    @NotNull(message = "O campo horas diárias não pode ser nulo")
    private Integer dailyHours;

    @Column(nullable = false)
    @NotNull(message = "A escala é obrigatória")
    private TipoEscala tipo_escala;
    
    private Long bankOfHours = (long)0;

    @Column(nullable = false)
    @NotNull(message = "A data de nascimento não pode ser nula")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate birthDate;

    public void missedWorkDay() {
        this.bankOfHours = this.bankOfHours - this.dailyHours;
    }
}
