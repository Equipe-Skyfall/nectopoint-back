package com.nectopoint.backend.repositories.userSession;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.dtos.DashboardDTO;
import com.nectopoint.backend.dtos.UserSessionDTO;
import com.nectopoint.backend.dtos.UserVacationDTO;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.utils.DataTransferHelper;

public class UserSessionRepositoryCustomImpl implements UserSessionRepositoryCustom {

    private final DataTransferHelper dataTransferHelper;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public UserSessionRepositoryCustomImpl (DataTransferHelper dataTransferHelper) {
        this.dataTransferHelper = dataTransferHelper;
    }

    public Page<UserSessionDTO> findByParamsDynamic(String cpf, String nome_colaborador, List<TipoStatusUsuario> lista_status, Pageable pageable) {
        Query query = new Query();

        if (cpf != null) {
            query.addCriteria(Criteria.where("dados_usuario.cpf").is(cpf));
        }
        if (nome_colaborador != null) {
            query.addCriteria(Criteria.where("dados_usuario.nome").regex(".*" + Pattern.quote(nome_colaborador) + ".*", "i"));
        }
        if (lista_status != null && !lista_status.isEmpty()) {
            query.addCriteria(Criteria.where("dados_usuario.status").in(lista_status));
        }
        query.with(Sort.by(Sort.Order.asc("dados_usuario.nome")));

        query.fields()
            .include("id_colaborador")
            .include("dados_usuario.nome")
            .include("dados_usuario.cpf")
            .include("dados_usuario.cargo")
            .include("dados_usuario.departamento")
            .include("dados_usuario.status")
            .include("jornada_trabalho.banco_de_horas")
            .include("jornada_trabalho.horas_diarias");

        long total = mongoTemplate.count(query, UserSessionEntity.class);

        query.with(pageable);

        List<UserSessionDTO> users = mongoTemplate.find(query, UserSessionEntity.class).stream()
            .map(user -> dataTransferHelper.toUserSessionDTO(user))
            .collect(Collectors.toList());

        return new PageImpl<>(users, pageable, total);
    }


    //Busca quem estiver ativo
    public List<UserSessionEntity> findEmployeesNotOnLeave(TipoStatusUsuario optionalStatus) {
        Query query = new Query();
    
        List<TipoStatusUsuario> leaveStatuses = Arrays.asList(
            TipoStatusUsuario.FERIAS,
            TipoStatusUsuario.INATIVO
        );

        if (optionalStatus != null) {
            leaveStatuses.add(optionalStatus);
        }
    
        //Buscando usuário que não tem os status acima
        query.addCriteria(Criteria.where("dados_usuario.status").nin(leaveStatuses));
        
        
        query.with(Sort.by(Sort.Order.asc("dados_usuario.nome")));
        

        return mongoTemplate.find(query, UserSessionEntity.class);
    }

    public List<UserSessionEntity> findEmployeesByWorkSchedule(TipoEscala escala) {
        Query query = new Query();
    
        List<TipoStatusUsuario> leaveStatuses = Arrays.asList(
            TipoStatusUsuario.FERIAS,
            TipoStatusUsuario.INATIVO
        );

        query.addCriteria(Criteria.where("dados_usuario.status").nin(leaveStatuses));
        
        query.addCriteria(Criteria.where("jornada_trabalho.tipo_escala").is(escala));
        
        query.with(Sort.by(Sort.Order.asc("dados_usuario.nome")));
        
        return mongoTemplate.find(query, UserSessionEntity.class);
    }

    public List<UserVacationDTO> findEmployeesStartingOrEndingVacation(Instant date) {
        Query query = new Query();

        List<TipoStatusUsuario> leaveStatuses = Arrays.asList(
            TipoStatusUsuario.INATIVO
        );
        query.addCriteria(Criteria.where("dados_usuario.status").nin(leaveStatuses));

        ZoneId zone = ZoneId.of("America/Sao_Paulo");
        LocalDate localDate = date.atZone(zone).toLocalDate();
        Instant startOfDay = localDate.atStartOfDay(zone).toInstant();
        Instant endOfDay = localDate.plusDays(1).atStartOfDay(zone).toInstant();

        Criteria iniciarFerias = Criteria.where("dados_usuario.ferias_inicio").gte(startOfDay).lt(endOfDay);
        Criteria finalizarFerias = Criteria.where("dados_usuario.ferias_final").gte(startOfDay).lt(endOfDay);
        query.addCriteria(new Criteria().orOperator(iniciarFerias, finalizarFerias));

        List<UserSessionEntity> users = mongoTemplate.find(query, UserSessionEntity.class);

        return users.stream().map(user -> {
            Instant inicio = user.getDados_usuario().getFerias_inicio();
            boolean startVacation = inicio != null && 
                !inicio.isBefore(startOfDay) && inicio.isBefore(endOfDay);
            
            return new UserVacationDTO(user, startVacation);
        }).collect(Collectors.toList());
    }

    public DashboardDTO countUserStatuses() {
        Query query = new Query();

        List<TipoStatusUsuario> leaveStatuses = Arrays.asList(
            TipoStatusUsuario.INATIVO
        );
        query.addCriteria(Criteria.where("dados_usuario.status").nin(leaveStatuses));

        DashboardDTO dashboard = new DashboardDTO();
        List<UserSessionEntity> users = mongoTemplate.find(query, UserSessionEntity.class);

        for (UserSessionEntity user : users) {
            TipoStatusUsuario status = user.getDados_usuario().getStatus();

            switch (status) {
                case FOLGA:
                    dashboard.incrementDeFolga();
                    break;
            
                case FERIAS:
                    dashboard.incrementDeFerias();
                    break;

                case ESCALADO:
                    TipoStatusTurno statusTurno = user.getJornada_atual().getStatus_turno();
                    
                    switch (statusTurno) {
                        case TRABALHANDO:
                            dashboard.incrementTrabalhando();
                            break;
                    
                        case INTERVALO:
                            dashboard.incrementNoIntervalo();
                            break;

                        case NAO_INICIADO:
                            dashboard.incrementNaoIniciado();
                            break;

                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        return dashboard;
    }
}
