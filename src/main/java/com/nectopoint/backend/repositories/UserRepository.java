package com.nectopoint.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nectopoint.backend.modules.user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

}
