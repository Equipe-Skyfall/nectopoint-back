package com.nectopoint.backend.controllers.registry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.HolidayDTO;
import com.nectopoint.backend.modules.holidays.HolidayEntity;
import com.nectopoint.backend.repositories.holidays.HolidayRepository;
import com.nectopoint.backend.services.HolidayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/feriados")
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;
    
    @Autowired
    private HolidayService holidayService;
    
    @PostMapping("/")
    public ResponseEntity<?> createHoliday(@Valid @RequestBody HolidayDTO holidayDTO) {
        try {
            HolidayEntity holiday = holidayService.createHoliday(holidayDTO);
            return new ResponseEntity<>(holiday, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHoliday(
            @PathVariable String id,
            @Valid @RequestBody HolidayDTO holidayDTO) {
        try {
            HolidayEntity updatedHoliday = holidayService.updateHoliday(id, holidayDTO);
            return ResponseEntity.ok(updatedHoliday);
        } catch (RuntimeException e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable String id) {
        try {
            holidayService.deleteHoliday(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HolidayEntity> getHolidayById(@PathVariable String id) {
        try {
            HolidayEntity holiday = holidayService.getHolidayById(id);
            return ResponseEntity.ok(holiday);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/")
    public ResponseEntity<List<HolidayEntity>> getAllHolidays() {
        List<HolidayEntity> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }
    
    @GetMapping("/listar")
    public ResponseEntity<Page<HolidayEntity>> getHolidaysList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
        @RequestParam(required = false) Boolean repeticaoAnual
    ) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<HolidayEntity> holidaysPage = holidayRepository.findByParamsDynamic(
            nome, data, repeticaoAnual, pageable);
            
        return new ResponseEntity<>(holidaysPage, HttpStatus.OK);
    }
    
    @GetMapping("/verificar")
    public ResponseEntity<Boolean> checkIfHoliday(@RequestParam(required = false) Instant data) {
        if (data == null) {
            data = Instant.now();
        }
        
        // Convert Instant to LocalDate
        LocalDate localDate = LocalDate.ofInstant(data, ZoneId.systemDefault());
        boolean isHoliday = holidayService.isHoliday(localDate);
        return ResponseEntity.ok(isHoliday);
    }
    
    @GetMapping("/verificar-data")
    public ResponseEntity<Boolean> checkIfHolidayByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        boolean isHoliday = holidayService.isHoliday(data);
        return ResponseEntity.ok(isHoliday);
    }
    
    @GetMapping("/verificar-usuario")
    public ResponseEntity<Boolean> checkIfHolidayForUser(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
        @RequestParam Long userId) {
        boolean isHoliday = holidayService.isHoliday(data, userId);
        return ResponseEntity.ok(isHoliday);
    }
    
    @PostMapping("/aplicar")
    public ResponseEntity<String> applyHolidayToUsers(@RequestParam(required = false) Instant data) {
        if (data == null) {
            data = Instant.now();
        }
        
        // Convert Instant to LocalDate
        LocalDate localDate = LocalDate.ofInstant(data, ZoneId.systemDefault());
        holidayService.applyHolidayStatusToUsers(localDate);
        return ResponseEntity.ok("Status de feriado aplicado aos usuários!");
    }
    
    @PostMapping("/aplicar-data")
    public ResponseEntity<String> applyHolidayToUsersByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        holidayService.applyHolidayStatusToUsers(data);
        return ResponseEntity.ok("Status de feriado aplicado aos usuários!");
    }
    
    @PostMapping("/folga-usuario/{userId}")
    public ResponseEntity<String> applyHolidayStatusToUser(@PathVariable Long userId) {
        holidayService.forceHolidayStatusForUser(userId);
        return ResponseEntity.ok("Status de folga aplicado ao usuário " + userId);
    }
}