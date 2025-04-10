package com.nectopoint.backend.repositories.userSession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.dtos.UserSessionDTO;
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
            query.addCriteria(Criteria.where("dados_usuario.nome").in(nome_colaborador));
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
public List<UserSessionEntity> findEmployeesNotOnLeave() {
    Query query = new Query();
   
    List<TipoStatusUsuario> leaveStatuses = Arrays.asList(
        TipoStatusUsuario.FERIAS,
        TipoStatusUsuario.INATIVO
    );
   

    //Buscando usuário que não tem os status acima
    query.addCriteria(Criteria.where("dados_usuario.status").nin(leaveStatuses));
    
    
    query.with(Sort.by(Sort.Order.asc("dados_usuario.nome")));
    

    return mongoTemplate.find(query, UserSessionEntity.class);
}

}
