package com.nectopoint.backend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;

public interface PointRegistryRepository extends MongoRepository<PointRegistryEntity, String> {
    
}
