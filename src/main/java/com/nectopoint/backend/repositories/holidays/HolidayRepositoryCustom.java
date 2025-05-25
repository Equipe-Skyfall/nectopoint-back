package com.nectopoint.backend.repositories.holidays;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nectopoint.backend.modules.holidays.HolidayEntity;

public interface HolidayRepositoryCustom {
    Page<HolidayEntity> findByParamsDynamic(String name, LocalDate date, Boolean repeatsYearly, Pageable pageable);
    
    List<HolidayEntity> findHolidaysForDate(LocalDate date);
}