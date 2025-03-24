package com.nectopoint.backend.validators.tickets;

import com.nectopoint.backend.dtos.TicketDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TicketValidator implements ConstraintValidator<ValidTicket, TicketDTO>{
    @Override
    public boolean isValid(TicketDTO ticketDto, ConstraintValidatorContext context) {
        if (ticketDto.getTipo_ticket() == null) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        switch (ticketDto.getTipo_ticket()) {
            case PONTOS_IMPAR:
                if (ticketDto.getId_aviso() == null || ticketDto.getId_registro() == null) {
                    context.buildConstraintViolationWithTemplate("id_aviso e id_registro é obrigatório nesse tipo de ticket")
                    .addPropertyNode("id_aviso").addPropertyNode("id_registro").addConstraintViolation();
                    return false;
                } else if (ticketDto.getHorario_saida() == null) {
                    context.buildConstraintViolationWithTemplate("Um horário de saída deve ser informado nesse tipo de ticket")
                    .addPropertyNode("horario_saida").addConstraintViolation();
                    return false;
                }
                break;
            case SEM_ALMOCO:
                if (ticketDto.getId_aviso() == null || ticketDto.getId_registro() == null) {
                    context.buildConstraintViolationWithTemplate("id_aviso e id_registro é obrigatório nesse tipo de ticket")
                    .addPropertyNode("id_aviso").addPropertyNode("id_registro").addConstraintViolation();
                    return false;
                } else if (ticketDto.getInicio_intervalo() == null || ticketDto.getFim_intervalo() == null) {
                    context.buildConstraintViolationWithTemplate("Horários de intervalo devem ser informados nesse tipo de ticket")
                    .addPropertyNode("inicio_intervalo").addPropertyNode("fim_intervalo").addConstraintViolation();
                    return false;
                }
                break;
            case PEDIR_FERIAS:
                if (ticketDto.getData_inicio_ferias() == null) {
                    context.buildConstraintViolationWithTemplate("Você deve escolher uma data de início para esse tipo de ticket")
                    .addPropertyNode("data_inicio_ferias").addConstraintViolation();
                    return false;
                } else if (ticketDto.getDias_ferias() == null) {
                    context.buildConstraintViolationWithTemplate("Você deve informar a quantidade de dias de férias para esse tipo de ticket")
                    .addPropertyNode("dias_ferias").addConstraintViolation();
                    return false;
                }
                break;
            case PEDIR_ABONO:
                if (ticketDto.getMotivo_abono() == null) {
                    context.buildConstraintViolationWithTemplate("Dia ou dias para abonar devem ser informados nesse tipo de ticket")
                    .addPropertyNode("motivo_abono").addConstraintViolation();
                    return false;
                }
                if (ticketDto.getDias_abono() == null) {
                    context.buildConstraintViolationWithTemplate("Dia ou dias para abonar devem ser informados nesse tipo de ticket")
                    .addPropertyNode("dias_abono").addConstraintViolation();
                    return false;
                }
                if (ticketDto.getAbono_inicio() == null && ticketDto.getAbono_final() == null) {
                    context.buildConstraintViolationWithTemplate("Dia ou dias para abonar devem ser informados nesse tipo de ticket")
                    .addPropertyNode("abono_inicio").addPropertyNode("abono_final").addConstraintViolation();
                    return false;
                }
                break;
        }
        return true;
    }
}
