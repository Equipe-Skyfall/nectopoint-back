package com.nectopoint.backend.dtos;

import com.nectopoint.backend.modules.user.UserSessionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVacationDTO {
    private UserSessionEntity user;
    private boolean startVacation;
}
