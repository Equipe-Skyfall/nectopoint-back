package com.nectopoint.backend.services;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.exceptions.DuplicateException;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.utils.DataTransferHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final DataTransferHelper dataTransferHelper;

    @Autowired
    private UserSessionRepository userSessionRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService (DataTransferHelper dataTransferHelper) {
        this.dataTransferHelper = dataTransferHelper;
    }

    // Cria Usuário
    public UserEntity createUser(UserEntity userEntity) {
        userRepository.findByCpf(userEntity.getCpf())
            .ifPresent((_) -> { throw new DuplicateException("Cpf já cadastrado"); });
        userRepository.findByEmail(userEntity.getEmail())
            .ifPresent((_) -> { throw new DuplicateException("Email já cadastrado"); });
        userRepository.findByEmployeeNumber(userEntity.getEmployeeNumber())
            .ifPresent((_) -> { throw new DuplicateException("Número de funcionário já cadastrado"); });

        var encodedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodedPassword);
        
        UserEntity newUser = this.userRepository.save(userEntity);

        UserDetailsDTO userDetailsDTO = dataTransferHelper.toUserDetailsDTO(newUser);
        this.userSessionService.createSession(userDetailsDTO);

        return newUser;
    }

    // Deleta Usuário
    public void deleteUser(Long id) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        if (userOptional.isPresent()) {

            // Update the user session to mark as inactive
            // We can leverage your existing UserSessionService methods
            userSessionService.updateUserStatus(id, TipoStatusUsuario.INATIVO);
            
            return;
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
            existingUser.setCpf(userDetails.getCpf());
            existingUser.setTitle(userDetails.getTitle());
            existingUser.setDepartment(userDetails.getDepartment());
            existingUser.setWorkJourneyType(userDetails.getWorkJourneyType());
            existingUser.setBankOfHours(userDetails.getBankOfHours());
            existingUser.setDailyHours(userDetails.getDailyHours());
            existingUser.setEmployeeNumber(userDetails.getEmployeeNumber());
            // var encodedPassword = passwordEncoder.encode(userDetails.getPassword());
            // existingUser.setPassword(encodedPassword);
          //update na user session
            UserDetailsDTO userDetailsDTO = dataTransferHelper.toUserDetailsDTO(userDetails);
            userSessionService.updateUser(userDetailsDTO);
            
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Optional<UserEntity> userOptional = userRepository.findById(id);
        
        boolean hasUppercase = newPassword.matches(".*[A-Z].*");
    boolean hasLowercase = newPassword.matches(".*[a-z].*");
    boolean hasDigit = newPassword.matches(".*\\d.*");
    boolean hasSpecial = newPassword.matches(".*[^A-Za-z0-9].*");
    boolean hasMinLength = newPassword.length() >= 8;
    
    System.out.println("Password validation:");
    System.out.println("Has Uppercase: " + hasUppercase);
    System.out.println("Has Lowercase: " + hasLowercase);
    System.out.println("Has Digit: " + hasDigit);
    System.out.println("Has Special: " + hasSpecial);
    System.out.println("Min Length (8): " + hasMinLength);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            
            // Verify old password
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("Senha atual incorreta");
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
