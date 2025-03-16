package com.nectopoint.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nectopoint.backend.modules.user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByCpf(String cpf);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmployeeNumber(String employeeNumber);
}
