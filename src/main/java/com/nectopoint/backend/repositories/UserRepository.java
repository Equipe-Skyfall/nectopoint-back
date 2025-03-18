package com.nectopoint.backend.repositories;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.modules.user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    
    Optional<UserEntity> findByCpf(String cpf);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByEmployeeNumber(String employeeNumber);

    @Query("SELECT new com.nectopoint.backend.dtos.UserDetailsDTO(u.title, u.department, u.workJourneyType, u.bankOfHours, u.dailyHours) FROM UserEntity u WHERE u.id = :id")
    UserDetailsDTO findUserDetailsById(@Param("id") Long id);
    
}
