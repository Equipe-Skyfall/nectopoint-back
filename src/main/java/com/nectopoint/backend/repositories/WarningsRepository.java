package com.nectopoint.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nectopoint.backend.modules.usersRegistry.WarningsEntity;

public interface WarningsRepository extends MongoRepository<WarningsEntity, String>{
    
}
