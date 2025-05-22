package com.nectopoint.backend.dtos;

import lombok.Data;

@Data
public class DashboardDTO {
    Integer de_folga = 0;
    Integer de_ferias = 0;
    Integer trabalhando = 0;
    Integer no_intervalo = 0;
    Integer nao_iniciado = 0;

    public void incrementDeFolga() {
        this.de_folga++;
    }

    public void incrementDeFerias() {
        this.de_ferias++;
    }

    public void incrementTrabalhando() {
        this.trabalhando++;
    }

    public void incrementNoIntervalo() {
        this.no_intervalo++;
    }

    public void incrementNaoIniciado() {
        this.nao_iniciado++;
    }
}
