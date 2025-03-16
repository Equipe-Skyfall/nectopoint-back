package com.nectopoint.backend.controllers.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nectopoint.backend.dtos.UserDetailsDTO;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.UserSessionRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Ao fazer Login "startSession" inicializa o usuário com os dados atualizados pegos do banco MySQL
// porém da maneira que está implementado no momento o FRONT deve mandar o userID para /iniciar

// Caso esse processo seja feito somente no BACKEND mova a lógica de "startSession" para "UserSessionService.java" no pacote "services"

@RestController
@RequestMapping("/sessao/usuario")
public class UserSessionController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;

    @PostMapping("/iniciar")
    public UserSessionEntity startSession(@RequestBody Long id) {
        UserDetailsDTO userDetails = userRepo.findUserDetailsById(id);
        UserSessionEntity userSession = userSessionRepo.findByColaborador(id);

        if (userSession == null) {
            userSession = new UserSessionEntity();
            userSession.setId_colaborador(id);
        }
        userSession.getDados_usuario().setCargo(userDetails.getTitle());
        userSession.getDados_usuario().setDepartamento(userDetails.getDepartment());

        userSession.getJornada_trabalho().setBanco_de_horas(userDetails.getBankOfHours());
        userSession.getJornada_trabalho().setHoras_diarias(userDetails.getDailyHours());
        userSession.getJornada_trabalho().setTipo_jornada(userDetails.getWorkJourneyType());

        return userSessionRepo.save(userSession);
    }

    @GetMapping("/")
    public UserSessionEntity getUserSession(@RequestParam Long id) {
        return userSessionRepo.findByColaborador(id);
    }

}
