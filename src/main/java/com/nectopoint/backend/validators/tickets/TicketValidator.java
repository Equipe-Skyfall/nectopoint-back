package com.nectopoint.backend.validators.tickets;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.nectopoint.backend.dtos.TicketDTO;
import com.nectopoint.backend.dtos.TicketDTO.Pares;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.utils.DateTimeHelper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TicketValidator implements ConstraintValidator<ValidTicket, TicketDTO>{

    private final DateTimeHelper dateTimeHelper;

    public TicketValidator (DateTimeHelper dateTimeHelper) {
        this.dateTimeHelper = dateTimeHelper;
    }

    @Override
    public boolean isValid(TicketDTO ticketDto, ConstraintValidatorContext context) {
        if (ticketDto.getTipo_ticket() == null) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        switch (ticketDto.getTipo_ticket()) {
            case ALTERAR_PONTOS:
                if (ticketDto.getId_registro() == null) {
                    context.buildConstraintViolationWithTemplate("id_registro é obrigatório nesse tipo de ticket")
                    .addPropertyNode("id_registro").addConstraintViolation();
                    return false;
                } else if (ticketDto.getPontos_anterior().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("A data e horário de início do turno deve ser informada nesse tipo de ticket")
                    .addPropertyNode("pontos_anterior").addConstraintViolation();
                    return false;
                } else if (ticketDto.getPontos_ajustado().isEmpty() && ticketDto.getNovos_pontos().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Os pontos à serem alterados/inseridos devem ser informados nesse tipo de ticket")
                    .addPropertyNode("novos_pontos").addConstraintViolation();
                    return false;
                } else if (ticketDto.getPontos_ajustado().size() % 2 != 0) {
                    context.buildConstraintViolationWithTemplate("Turnos precisam terminar com uma saída")
                    .addPropertyNode("pontos_ajustado").addConstraintViolation();
                    return false;
                }

                Instant shiftDay = ticketDto.getPontos_anterior().get(0).getData_hora();
                List<Pares> novos_pontos = ticketDto.getNovos_pontos();

                Instant inicio_turno = dateTimeHelper.joinDateTime(
                    shiftDay,
                    ticketDto.getPontos_ajustado().get(0).getData_hora()
                );
                Instant fim_turno = dateTimeHelper.joinDateTime(
                    shiftDay,
                    ticketDto.getPontos_ajustado().get(ticketDto.getPontos_ajustado().size() - 1).getData_hora()
                );

                List<Pares> pares_pontos = new ArrayList<>();
                
                // Validando os pontos editados e inserindo-os na lista de pares, excluindo o primeiro e último índice
                if (ticketDto.getPontos_ajustado().size() > 2) {
                    List<Ponto> pontos_ajustado_sublist = ticketDto.getPontos_ajustado().subList(1, ticketDto.getPontos_ajustado().size()-1);

                    for (int i = 0; i < pontos_ajustado_sublist.size() - 1; i++) {
                        Instant saida = dateTimeHelper.joinDateTime(shiftDay, pontos_ajustado_sublist.get(i).getData_hora());
                        if (isNotBefore(inicio_turno, saida)) { // Checando se saida é menor que o início do turno
                            context.buildConstraintViolationWithTemplate("Você está inserindo uma saída antes do início do turno")
                            .addPropertyNode("pontos_ajustado")
                            .addConstraintViolation();
                            return false;
                        }

                        Instant entrada = dateTimeHelper.joinDateTime(shiftDay, pontos_ajustado_sublist.get(i+1).getData_hora());
                        if (isNotBefore(entrada, fim_turno)) { // Checando se entrada é maior que o fim do turno
                            context.buildConstraintViolationWithTemplate("Você está inserindo uma entrada depois do fim do turno")
                            .addPropertyNode("pontos_ajustado")
                            .addConstraintViolation();
                            return false;
                        }

                        if (isNotBefore(saida, entrada)) { // Checando se saida está vindo antes da entrada
                            context.buildConstraintViolationWithTemplate("Uma saída está registrada após a entrada correspondente")
                            .addPropertyNode("pontos_ajustado")
                            .addConstraintViolation();
                            return false;
                        }

                        // Montando par (SAIDA + ENTRADA) e inserindo na lista de pares
                        Pares par = new Pares();
                        par.setHorario_saida(saida);
                        par.setHorario_entrada(entrada);
                        pares_pontos.add(par);

                        i++;
                    }
                }

                // Validando novos pontos e inserindo-os na lista de pares
                if (novos_pontos != null && !novos_pontos.isEmpty()) {
                    List<Pares> novosSorted = novos_pontos.stream()
                    .sorted(Comparator.comparing(Pares::getHorario_saida))
                    .toList();

                    Instant primeira_saida = dateTimeHelper.joinDateTime(shiftDay, novosSorted.get(0).getHorario_saida());
                    Instant ultima_entrada = dateTimeHelper.joinDateTime(shiftDay, novosSorted.get(novosSorted.size()-1).getHorario_entrada());
                    
                    if (isNotBefore(inicio_turno, primeira_saida)) { // Checando se os novos pontos sendo inseridos tem horarios antes do inicio do turno
                        context.buildConstraintViolationWithTemplate("Você está inserindo uma saída antes do início do turno")
                        .addPropertyNode("novos_pontos")
                        .addConstraintViolation();
                        return false;
                    } else if (isNotBefore(ultima_entrada, fim_turno)) { // Checando se os novos pontos sendo inseridos tem horarios depois do fim do turno
                        context.buildConstraintViolationWithTemplate("Você está inserindo uma entrada depois do fim do turno")
                        .addPropertyNode("novos_pontos")
                        .addConstraintViolation();
                        return false;
                    }

                    pares_pontos.addAll(novosSorted);
                }

                // Organizando lista de pares pelo horário dos pontos de saída
                List<Pares> paresSorted = pares_pontos.stream()
                .sorted(Comparator.comparing(Pares::getHorario_saida)).toList();
                
                // Validando novos intervalos de pontos para não intercalarem
                for (int i = 0; i < paresSorted.size() - 1; i++) {
                    Instant currentSaida = dateTimeHelper.joinDateTime(
                        shiftDay,
                        paresSorted.get(i).getHorario_saida()
                    );
                    Instant currentEntrada = dateTimeHelper.joinDateTime(
                        shiftDay,
                        paresSorted.get(i).getHorario_entrada()
                    );
                    Instant nextSaida = dateTimeHelper.joinDateTime(
                        shiftDay,
                        paresSorted.get(i + 1).getHorario_saida()
                    );

                    if (isNotBefore(currentSaida, currentEntrada) || isNotBefore(currentEntrada, nextSaida)) {
                        context.buildConstraintViolationWithTemplate("Horários de pontos novos se sobrepõem entre si")
                            .addPropertyNode("novos_pontos")
                            .addConstraintViolation();
                        return false;
                    }
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
                break;
            case SOLICITAR_FOLGA:
                if(ticketDto.getDia_folga() == null) {
                    context.buildConstraintViolationWithTemplate("Você deve escolher um dia de folga para esse tipo de ticket")
                    .addPropertyNode("dia_folga").addConstraintViolation();
                    return false;
                }
            case PEDIR_HORA_EXTRA:
                if(ticketDto.getStatus_usuario() != TipoStatusUsuario.ESCALADO) {
                    context.buildConstraintViolationWithTemplate("Esse usuário não pode pedir hora extra.")
                    .addPropertyNode("status_usuario").addConstraintViolation();
                    return false;
                }
        }
        return true;
    }

    private boolean isNotBefore(Instant first, Instant second) {
        return first.isAfter(second);
    }
}
