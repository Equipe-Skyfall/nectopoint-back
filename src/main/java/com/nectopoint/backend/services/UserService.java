package com.nectopoint.backend.services;

import com.nectopoint.backend.BackendApplication;
import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoCargo;
import com.nectopoint.backend.exceptions.DuplicateException;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final BackendApplication backendApplication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionService userSessionService;

    UserService(BackendApplication backendApplication) {
        this.backendApplication = backendApplication;
    }

    // Cria Usuário
    public UserEntity createUser(UserEntity userEntity) {
        userRepository.findByCpf(userEntity.getCpf())
            .ifPresent((_) -> { throw new DuplicateException("Cpf já cadastrado"); });
        userRepository.findByEmail(userEntity.getEmail())
            .ifPresent((_) -> { throw new DuplicateException("Email já cadastrado"); });
        userRepository.findByEmployeeNumber(userEntity.getEmployeeNumber())
            .ifPresent((_) -> { throw new DuplicateException("Número de funcionário já cadastrado"); });

        TipoCargo title = userEntity.getTitle();
        String department = userEntity.getDepartment();

        String workJourneyType = userEntity.getWorkJourneyType();
        Float bankOfHours = userEntity.getBankOfHours();
        Integer dailyHours = userEntity.getDailyHours();

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(title, department, workJourneyType, bankOfHours, dailyHours);
        
        UserEntity newUser = this.userRepository.save(userEntity);
        this.userSessionService.createSession(newUser.getId(), userDetailsDTO);
        return newUser;
    }

    // Deleta Usuário
    public void deleteUser(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            userSessionService.deleteUserData(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // Retorna todos os usuários
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    // Retorna o usuário pelo ID
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Edita Usuário
    public UserEntity updateUser(Long id, UserEntity userDetails) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {
            UserEntity existingUser = userOptional.get();
            
            if (!existingUser.getCpf().equals(userDetails.getCpf())) {
                userRepository.findByCpf(userDetails.getCpf())
                    .ifPresent((_) -> { throw new DuplicateException("Cpf já cadastrado"); });
            }
            if(!existingUser.getEmail().equals(userDetails.getEmail())){
                userRepository.findByEmail(userDetails.getEmail())
                .ifPresent((_) -> { throw new DuplicateException("Email já cadastrado"); });
            }
            if(!existingUser.getEmployeeNumber().equals(userDetails.getEmployeeNumber())){
                
                userRepository.findByEmployeeNumber(userDetails.getEmployeeNumber())
                .ifPresent((_) -> { throw new DuplicateException("Número de funcionário já cadastrado"); });
            }

    
            // update na user entity
            existingUser.setName(userDetails.getName());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setCpf(userDetails.getCpf());
            existingUser.setTitle(userDetails.getTitle());
            existingUser.setDepartment(userDetails.getDepartment());
            existingUser.setWorkJourneyType(userDetails.getWorkJourneyType());
            existingUser.setBankOfHours(userDetails.getBankOfHours());
            existingUser.setDailyHours(userDetails.getDailyHours());
            existingUser.setEmployeeNumber(userDetails.getEmployeeNumber());
    
          //update na user session
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                userDetails.getTitle(),
                userDetails.getDepartment(),
                userDetails.getWorkJourneyType(),
                userDetails.getBankOfHours(),
                userDetails.getDailyHours()
            );
    
            userSessionService.updateUser(id, userDetailsDTO);
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
}
