package com.nectopoint.backend.services;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import com.nectopoint.backend.repositories.holidays.HolidayRepository;
import com.nectopoint.backend.controllers.sse.SseController;
import com.nectopoint.backend.enums.TipoAbono;
import com.nectopoint.backend.enums.TipoEscala;
import com.nectopoint.backend.enums.TipoPonto;
import com.nectopoint.backend.enums.TipoStatusTurno;
import com.nectopoint.backend.enums.TipoStatusUsuario;
import com.nectopoint.backend.modules.holidays.HolidayEntity;
import com.nectopoint.backend.modules.user.UserEntity;
import com.nectopoint.backend.modules.user.UserSessionEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Abono;
import com.nectopoint.backend.modules.usersRegistry.PointRegistryEntity.Ponto;
import com.nectopoint.backend.repositories.UserRepository;
import com.nectopoint.backend.repositories.pointRegistry.PointRegistryRepository;
import com.nectopoint.backend.repositories.userSession.UserSessionRepository;
import com.nectopoint.backend.utils.DataTransferHelper;

@Service
public class PointRegistryService {

    private final DataTransferHelper dataTransferHelper;

    @Autowired
    private SseController sseController;
    
    @Autowired
    private PointRegistryRepository registryRepo;
    @Autowired
    private UserSessionRepository userSessionRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private HolidayRepository holidayRepository;

    @Autowired
    private UserSessionService userSessionService;

    public PointRegistryService (DataTransferHelper dataTransferHelper) {
        this.dataTransferHelper = dataTransferHelper;
    }

    public PointRegistryEntity postPunch(Long id_colaborador) {
        UserSessionEntity currentUser = userSessionRepo.findByColaborador(id_colaborador);

        PointRegistryEntity currentShift = processPostPunch(currentUser);

        sseController.sendPing("Ponto batido");
        return currentShift;
    }

    public PointRegistryEntity processPostPunch(UserSessionEntity currentUser) {
        PointRegistryEntity currentShift;

        Instant data_hora = Instant.now();
        
        if (currentUser.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.NAO_INICIADO)) {
            currentShift = new PointRegistryEntity();
            currentShift.setId_colaborador(currentUser.getId_colaborador());
            currentShift.setNome_colaborador(currentUser.getDados_usuario().getNome());
            currentShift.setCpf_colaborador(currentUser.getDados_usuario().getCpf());

            currentShift.setInicio_turno(data_hora);
            currentShift.getPontos_marcados().add(processNewEntry(null, data_hora));
            currentShift.setStatus_turno(TipoStatusTurno.TRABALHANDO);
        } else {
            currentShift = dataTransferHelper.toPointRegistryEntity(
                currentUser.getId_colaborador(),
                currentUser.getDados_usuario().getNome(),
                currentUser.getDados_usuario().getCpf(),
                currentUser.getJornada_atual()
            );
            
            int last_index = currentShift.getPontos_marcados().size()-1;
            Ponto last_entry = currentShift.getPontos_marcados().get(last_index);
            Ponto new_entry = processNewEntry(last_entry, data_hora);

            currentShift.getPontos_marcados().add(new_entry);
            currentShift = updateEntity(currentShift, last_index, new_entry);
        }

        registryRepo.save(currentShift);

        currentUser.setJornada_atual(dataTransferHelper.toPointRegistryStripped(currentShift));
        userSessionRepo.save(currentUser);

        return currentShift;
    }

    public void editShift(String id_registro, List<Instant> lista_horas) {
        PointRegistryEntity targetShift = registryRepo.findById(id_registro).get();
        
        targetShift.getPontos_marcados().clear();
        targetShift.setTempo_trabalhado_min((long)0);
        targetShift.setTirou_almoco(false);
        targetShift.setInicio_turno(lista_horas.get(0));

        for (int i = 0; i < lista_horas.size(); i++ ) {
            if (i==0) {
                targetShift.getPontos_marcados().add(processNewEntry(null, lista_horas.get(i)));
            } else {
                targetShift.getPontos_marcados().add(
                    processNewEntry(targetShift.getPontos_marcados().get(i-1), lista_horas.get(i))
                );
                targetShift = updateEntity(targetShift, i-1, targetShift.getPontos_marcados().get(i));
            }
        }

        userSessionService.finishShift(targetShift);
    }

    public void processExcusedAbsence(Long id_colaborador, TipoAbono motivo_abono, List<Instant> dias_abono) {
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
        List<Criteria> criteriaList = new ArrayList<>();

        UserSessionEntity targetUser = userSessionRepo.findByColaborador(id_colaborador);
        Long banco_atual = targetUser.getJornada_trabalho().getBanco_de_horas();
        Long horas_diarias = (long)targetUser.getJornada_trabalho().getHoras_diarias() * 60;

        UserEntity targetUserSql = userRepo.findById(id_colaborador).get();

        for (Instant date : dias_abono) {
            LocalDate localDate = date.atZone(zoneId).toLocalDate();
            Instant startOfDay = localDate.atStartOfDay(zoneId).toInstant();
            Instant endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).toInstant().minusNanos(1);
    
            Criteria dateCriteria = Criteria.where("inicio_turno").gte(startOfDay).lte(endOfDay);
            criteriaList.add(dateCriteria);
        }

        List<PointRegistryEntity> registryList = registryRepo.findByDateCriterias(criteriaList);

        registryList.forEach(registry -> {
            registry.setAbono(new Abono());
            registry.getAbono().setMotivo_abono(motivo_abono);
            registry.getAbono().setHorarios_abono("Abonado pelo dia.");

            targetUser.getJornada_trabalho().setBanco_de_horas(banco_atual + horas_diarias);
            targetUserSql.setBankOfHours(banco_atual + horas_diarias);
            targetUser.updateRegistry(dataTransferHelper.toPointRegistryStripped(registry));
        });

        registryRepo.saveAll(registryList);
        userSessionRepo.save(targetUser);
        userRepo.save(targetUserSql);
    }

    //    @Transactional
 public void startDayShifts() {
    // pega todos os user sessions
    List<UserSessionEntity> userSessions = userSessionRepo.findAll();
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();
    
    // pega os feriados pra hoje
    List<HolidayEntity> todayHolidays = holidayRepository.findHolidaysForDate(today);
    
    List<HolidayEntity> globalHolidays = todayHolidays.stream()
        .filter(HolidayEntity::isGlobalHoliday)
        .collect(Collectors.toList());
        
    List<HolidayEntity> userSpecificHolidays = todayHolidays.stream()
        .filter(h -> !h.isGlobalHoliday())
        .collect(Collectors.toList());
        
    System.out.println("Today has " + globalHolidays.size() + " global holidays and " +
                      userSpecificHolidays.size() + " user-specific holidays");
 
    boolean hasGlobalHoliday = !globalHolidays.isEmpty();
    
    // Lista todos os usuários com folga hoje
    List<Long> userIdsWithHoliday = new ArrayList<>();
    for (HolidayEntity holiday : userSpecificHolidays) {
        userIdsWithHoliday.addAll(holiday.getUserIds());
    }
    
    System.out.println("Users with specific holidays today: " + userIdsWithHoliday);
    
    int updatedCount = 0;
    int skippedCount = 0;
    int holidayCount = 0;
    int weekendCount = 0;
    
    // são setados meia noite ou as dez -> ambos setam feriado
    if (hasGlobalHoliday) {
        System.out.println("Global holiday today - all users stay in FOLGA status");
        return;
    }
    
    // processa cada usuário
    for (UserSessionEntity user : userSessions) {
        Long userId = user.getId_colaborador();
        TipoStatusUsuario currentStatus = user.getDados_usuario().getStatus();
        TipoEscala userSchedule = user.getJornada_trabalho().getTipo_escala();
        
        System.out.println("Processing user ID: " + userId + 
                          ", Name: " + user.getDados_usuario().getNome() + 
                          ", Current Status: " + currentStatus +
                          ", Schedule: " + userSchedule);
      
        // pula usuários em férias ou inativos
        if (currentStatus == TipoStatusUsuario.FERIAS || currentStatus == TipoStatusUsuario.INATIVO) {
            System.out.println("Skipping user " + userId + " with status " + currentStatus);
            skippedCount++;
            continue;
        }

       
        if (userIdsWithHoliday.contains(userId)) {
            if (currentStatus != TipoStatusUsuario.FOLGA) {
                user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
                userSessionRepo.save(user);
                System.out.println("User " + userId + " has holiday today - set to FOLGA status");
            } else {
                System.out.println("User " + userId + " already in FOLGA status for holiday");
            }
            holidayCount++;
            continue; 
        }
        
        // Checar se hoje é fim de semana na escala do usuário
        boolean isWeekendForUser = 
            (userSchedule == TipoEscala.CINCO_X_DOIS && 
             (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) ||
            (userSchedule == TipoEscala.SEIS_X_UM && 
             dayOfWeek == DayOfWeek.SUNDAY);
        
    
        if (isWeekendForUser) {
            if (currentStatus != TipoStatusUsuario.FOLGA) {
                user.getDados_usuario().setStatus(TipoStatusUsuario.FOLGA);
                userSessionRepo.save(user);
                System.out.println("User " + userId + " has weekend day - set to FOLGA status");
            } else {
                System.out.println("User " + userId + " already in FOLGA status for weekend");
            }
            weekendCount++;
        } 
        
        else {
            if (currentStatus != TipoStatusUsuario.ESCALADO) {
                user.getDados_usuario().setStatus(TipoStatusUsuario.ESCALADO);
                userSessionRepo.save(user);
                updatedCount++;
                System.out.println("Updated user " + userId + " status to ESCALADO");
            } else {
                System.out.println("User " + userId + " already in ESCALADO status");
            }
        }
    }
    
    System.out.println("Start day process complete. Updated: " + updatedCount + 
                       ", Skipped: " + skippedCount + 
                       ", Holiday users: " + holidayCount +
                       ", Weekend users: " + weekendCount);
}

    public void endDayShifts() {
        //Termina o turno para os funcionário que não estão de FOLGA ou Férias
        List<UserSessionEntity> userSessions = userSessionRepo.findEmployeesNotOnLeave(TipoStatusUsuario.FOLGA);

        for (UserSessionEntity user : userSessions) {
            Long id_colaborador = user.getId_colaborador();
            String nome_colaborador = user.getDados_usuario().getNome();
            String cpf_colaborador = user.getDados_usuario().getCpf();

            user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
            userSessionRepo.save(user);
            PointRegistryEntity entity = dataTransferHelper.toPointRegistryEntity(id_colaborador, nome_colaborador, cpf_colaborador, user.getJornada_atual());
            userSessionService.finishShift(entity);
        }
    }

    // Termina apenas o turno de um usuario
    public void endDayShift(Long userId){
        UserSessionEntity user = userSessionRepo.findByColaborador(userId);
        String nome_colaborador = user.getDados_usuario().getNome();
        String cpf_colaborador = user.getDados_usuario().getCpf();
        PointRegistryEntity entity;

        if(!user.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.NAO_INICIADO)){
            if(user.getJornada_atual().getStatus_turno().equals(TipoStatusTurno.TRABALHANDO)){
                user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
                userSessionRepo.save(user);
                entity = processPostPunch(user);
                userSessionService.finishShift(entity);
            }else{
                user.getDados_usuario().setStatus(TipoStatusUsuario.FORA_DO_EXPEDIENTE);
                userSessionRepo.save(user);
                entity = dataTransferHelper.toPointRegistryEntity(userId, nome_colaborador, cpf_colaborador, user.getJornada_atual());
                userSessionService.finishShift(entity);
            }
        }
    }

    public Ponto processNewEntry(Ponto last_entry, Instant date_time) {
        Ponto newPoint = new Ponto();

        newPoint.setData_hora(date_time);
        if (last_entry == null) {
            newPoint.setTipo_ponto(TipoPonto.ENTRADA);
        } else {
            newPoint.setTipo_ponto(last_entry.getTipo_ponto().invert());

            Instant time_last_entry = last_entry.getData_hora();
            Long time_between = Duration.between(time_last_entry, date_time).toMinutes();
            newPoint.setTempo_entre_pontos(time_between);
        }

        return newPoint;
    }

    private PointRegistryEntity updateEntity(PointRegistryEntity targetEntity, int last_index, Ponto new_entry) {
        if (new_entry.getTipo_ponto() == TipoPonto.SAIDA) {
            targetEntity.setTempo_trabalhado_min(targetEntity.getTempo_trabalhado_min()+new_entry.getTempo_entre_pontos());
            targetEntity.setStatus_turno(TipoStatusTurno.INTERVALO);
        } else {
            targetEntity.setStatus_turno(TipoStatusTurno.TRABALHANDO);
            if (targetEntity.getTirou_almoco() == false && new_entry.getTempo_entre_pontos() >= 60) {
                targetEntity.setTirou_almoco(true);
                targetEntity.getPontos_marcados().get(last_index).setAlmoco(true);
            }
        }

        return targetEntity;
    }

    //Deletar TODOS os registros de pontos de um usuário
    //ESSA FUNÇÃO É APENAS PARA SINCRONIZAR OS BANCOS DURANTE DESENVOLVIMENTO
    public void deleteAllByColaborador (Long id) {
        List<PointRegistryEntity> records = registryRepo.findAllByIdColaborador(id);
        registryRepo.deleteAll(records);
    }

}
