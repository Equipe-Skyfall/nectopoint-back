package com.nectopoint.backend.validators.tickets;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TicketAnswerValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTicketAnswer {
    String message() default "Campos inválidos para resposta ao ticket!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
