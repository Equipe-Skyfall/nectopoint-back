package com.nectopoint.backend.dtos;

import lombok.Data;

@Data
public class DashboardDTO {
    Integer de_folga;
    Integer de_ferias;
    Integer trabalhando;
    Integer no_intervalo;
    Integer nao_iniciado;

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
