package com.nectopoint.backend.repositories.pointRegistry;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepository extends MongoRepository<PointRegistryEntity, String>, PointRegistryRepositoryCustom {
    
    @Query("{ 'id_colaborador' : ?0 }")
    List<PointRegistryEntity> findAllByIdColaborador(Long id_colaborador);

}
