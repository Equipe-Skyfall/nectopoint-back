package com.nectopoint.backend.controllers.user;

import com.nectopoint.backend.dtos.PasswordChangeDTO;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuario")
public class UserController {
    
    @Autowired
    private UserService userService;

    // Cria Usuário
    @PostMapping("/")
    public ResponseEntity<Object> create(@Valid @RequestBody UserEntity userEntity) {
       try{
           var result =  this.userService.createUser(userEntity);
           return ResponseEntity.ok().body(result);
        }catch(Exception e){return ResponseEntity.badRequest().body(e.getMessage());}
    }

    // Deleta Usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Retorna todos os usuários
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        // System.out.println(users);
        return ResponseEntity.ok(users);
    }

    // Retorna o usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        try {
            UserEntity user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Edita Usuário
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserEntity userDetails) {
    try{
        UserEntity updatedUser = this.userService.updateUser(id, userDetails);
        return ResponseEntity.ok().body(updatedUser);
    }catch(Exception e){return ResponseEntity.badRequest().body(e.getMessage());}

}
    //Editar senha
    @PutMapping("/{id}/senha")
        public ResponseEntity<Object> changePassword(
        @PathVariable Long id, 
        @Valid @RequestBody PasswordChangeDTO passwordDTO,
        BindingResult bindingResult) {
    
    if (bindingResult.hasErrors()) {
        // Extract and return validation errors
        List<String> errors = bindingResult.getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
    
    try {
        userService.changePassword(id, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return ResponseEntity.ok().body("Senha atualizada com sucesso");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}
