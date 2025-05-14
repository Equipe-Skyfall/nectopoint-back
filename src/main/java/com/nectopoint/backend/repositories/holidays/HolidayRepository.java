package com.nectopoint.backend.repositories.holidays;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.nectopoint.backend.modules.holidays.HolidayEntity;

public interface HolidayRepository extends MongoRepository<HolidayEntity, String>, HolidayRepositoryCustom {
    
    // Updated to use startDate instead of date
    List<HolidayEntity> findByStartDate(LocalDate startDate);
    
    // Find holidays with this endDate
    List<HolidayEntity> findByEndDate(LocalDate endDate);
    
    // Find holidays that contain this date (date falls between startDate and endDate)
    @Query("{ 'startDate' : { $lte: ?0 }, 'endDate' : { $gte: ?0 } }")
    List<HolidayEntity> findByDateBetween(LocalDate date);
    
    @Query("{ 'repeatsYearly' : true }")
    List<HolidayEntity> findAllYearlyHolidays();
}