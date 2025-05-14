package com.nectopoint.backend.repositories.holidays;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.nectopoint.backend.modules.holidays.HolidayEntity;

public class HolidayRepositoryCustomImpl implements HolidayRepositoryCustom {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<HolidayEntity> findByParamsDynamic(String name, LocalDate date, Boolean repeatsYearly, Pageable pageable) {
        Query query = new Query();

        if (name != null) {
            query.addCriteria(Criteria.where("name").regex(".*" + Pattern.quote(name) + ".*", "i"));
        }

        if (date != null) {
            // Find holidays that include the specified date
            query.addCriteria(
                new Criteria().orOperator(
                    // Find holidays where date is between startDate and endDate
                    Criteria.where("startDate").lte(date).and("endDate").gte(date),
                    
                    // Find yearly holidays where month/day match
                    Criteria.where("repeatsYearly").is(true)
                        .and("startDate").lte(date.withYear(date.getYear()))
                        .and("endDate").gte(date.withYear(date.getYear()))
                )
            );
        }

        if (repeatsYearly != null) {
            query.addCriteria(Criteria.where("repeatsYearly").is(repeatsYearly));
        }

        query.with(Sort.by(Sort.Order.asc("startDate")));

        long total = mongoTemplate.count(query, HolidayEntity.class);

        query.with(pageable);

        List<HolidayEntity> holidays = mongoTemplate.find(query, HolidayEntity.class);
        return new PageImpl<>(holidays, pageable, total);
    }

    @Override
    public List<HolidayEntity> findHolidaysForDate(LocalDate date) {
        System.out.println("Finding holidays for date: " + date);
        
        // Get all holidays
        List<HolidayEntity> allHolidays = mongoTemplate.findAll(HolidayEntity.class);
        System.out.println("Total holidays found: " + allHolidays.size());
        
        List<HolidayEntity> result = new ArrayList<>();
        
        for (HolidayEntity holiday : allHolidays) {
            System.out.println("Checking holiday: " + holiday.getName() + ", startDate: " + holiday.getStartDate() + ", endDate: " + holiday.getEndDate());
            
            boolean matches = false;
            
            if (holiday.getRepeatsYearly()) {
                // For yearly holidays, we need to check if the month/day falls within the range
                LocalDate currentYearStart = holiday.getStartDate().withYear(date.getYear());
                LocalDate currentYearEnd = holiday.getEndDate().withYear(date.getYear());
                
                // Handle case where holiday spans across year boundary (e.g. Dec 30 - Jan 2)
                if (holiday.getStartDate().getMonthValue() > holiday.getEndDate().getMonthValue()) {
                    if (date.getMonthValue() >= holiday.getStartDate().getMonthValue()) {
                        // For months after or equal to start month in same year
                        matches = !date.isBefore(currentYearStart);
                    } else if (date.getMonthValue() <= holiday.getEndDate().getMonthValue()) {
                        // For months before or equal to end month in next year
                        matches = !date.isAfter(currentYearEnd);
                    }
                } else {
                    // Normal case when start and end are in same year
                    matches = !date.isBefore(currentYearStart) && !date.isAfter(currentYearEnd);
                }
                
                System.out.println("Yearly comparison (current year range: " + currentYearStart + " to " + currentYearEnd + "): " + matches);
            } else {
                // For regular holidays, check if date is within the date range
                matches = !date.isBefore(holiday.getStartDate()) && !date.isAfter(holiday.getEndDate());
                System.out.println("Date range comparison: " + matches);
            }
            
            if (matches) {
                System.out.println("MATCH FOUND for holiday: " + holiday.getName());
                result.add(holiday);
            }
        }
        
        System.out.println("Total matching holidays: " + result.size());
        return result;
    }
}