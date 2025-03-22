package com.nectopoint.backend.validators.tickets;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TicketValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTicket {
    String message() default "Campos inválidos para o tipo de ticket!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
