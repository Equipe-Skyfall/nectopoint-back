package com.nectopoint.backend.repositories.warnings;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public interface WarningsRepository extends MongoRepository<WarningsEntity, String>, WarningsRepositoryCustom{

    @Query("{ 'id_colaborador' : ?0 }")
    List<WarningsEntity> findAllByIdColaborador(Long id_colaborador);

}
