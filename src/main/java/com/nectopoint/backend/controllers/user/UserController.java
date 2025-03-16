package com.nectopoint.backend.controllers.user;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.exceptions.DuplicateException;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.services.UserSessionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionService userSessionService;

    //Cria Usuário
    @PostMapping("/")
    public UserEntity create(@Valid @RequestBody UserEntity userEntity){
        this.userRepository.findByCpf(userEntity.getCpf())
        .ifPresent((_) -> { throw new DuplicateException("Cpf já cadastrado");});
        this.userRepository.findByEmail(userEntity.getEmail())
        .ifPresent((_) -> { throw new DuplicateException("Email já cadastrado");});
        this.userRepository.findByEmployeeNumber(userEntity.getEmployeeNumber())
        .ifPresent((_) -> { throw new DuplicateException("Número de funcionário já cadastrado");});
        return this.userRepository.save(userEntity);
    }

    //Deleta Usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        //Optional -> dá uma flag que pode retornar vazio, "similar" ao (?) no Typescript"
        Optional<UserEntity> userOptional = this.userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            this.userRepository.deleteById(id);
            this.userSessionService.deleteUserData(id);
            // ResponseEntity é para retornar os 202,404,etc
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Retorna todos os usuários
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = this.userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    // Retorna o usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        Optional<UserEntity> userOptional = this.userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Edita usuário
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @Valid @RequestBody UserEntity userDetails) {
        Optional<UserEntity> userOptional = this.userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            UserEntity existingUser = userOptional.get();
          
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setCpf(userDetails.getCpf());

            existingUser.setTitle(userDetails.getTitle());
            TipoCargo title = userDetails.getTitle();
            existingUser.setDepartment(userDetails.getDepartment());
            String department = userDetails.getDepartment();

            existingUser.setWorkJourneyType(userDetails.getWorkJourneyType());
            String workJourneyType = userDetails.getWorkJourneyType();
            existingUser.setBankOfHours(userDetails.getBankOfHours());
            Float bankOfHours = userDetails.getBankOfHours();
            existingUser.setDailyHours(userDetails.getDailyHours());
            Integer dailyHours = userDetails.getDailyHours();

            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(title, department, workJourneyType, bankOfHours, dailyHours);
            
            existingUser.setEmployeeNumber(userDetails.getEmployeeNumber());
            
            this.userSessionService.updateUser(id, userDetailsDTO);
            UserEntity updatedUser = this.userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
