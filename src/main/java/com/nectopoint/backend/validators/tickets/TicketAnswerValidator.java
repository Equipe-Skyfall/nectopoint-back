package com.nectopoint.backend.validators.tickets;

import com.nectopoint.backend.dtos.TicketAnswerDTO;
import com.nectopoint.backend.enums.TipoStatusTicket;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TicketAnswerValidator implements ConstraintValidator<ValidTicketAnswer, TicketAnswerDTO>{
    @Override
    public boolean isValid(TicketAnswerDTO ticketAnswerDTO, ConstraintValidatorContext context) {
        if (ticketAnswerDTO.getNovo_status() == null) {
            return false;
        }
        
        context.disableDefaultConstraintViolation();
        if (ticketAnswerDTO.getNovo_status() == TipoStatusTicket.REPROVADO && ticketAnswerDTO.getJustificativa() == null) {
            context.buildConstraintViolationWithTemplate("Justificativa é obrigatório ao recusar um ticket")
            .addPropertyNode("justificativa").addConstraintViolation();
            return false;
        }
        if (ticketAnswerDTO.getNovo_status() == TipoStatusTicket.EM_AGUARDO) {
            context.buildConstraintViolationWithTemplate("Status inválido como resposta ao ticket")
            .addPropertyNode("novo_status").addConstraintViolation();
            return false;
        }
        return true;
    }    
}
