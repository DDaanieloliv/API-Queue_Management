// package com.ddaaniel.queue.controller;
//
// import com.ddaaniel.queue.domain.model.*;
// import com.ddaaniel.queue.domain.model.dto.AgendamentoDTO;
// import com.ddaaniel.queue.domain.model.dto.EspecialistaRecordDtoResponce;
// import com.ddaaniel.queue.domain.model.enuns.Prioridade;
// import com.ddaaniel.queue.domain.model.enuns.Role;
// import com.ddaaniel.queue.domain.model.enuns.StatusAgendamento;
// import com.ddaaniel.queue.domain.model.enuns.TipoEspecialista;
// import com.ddaaniel.queue.domain.repository.*;
// import com.ddaaniel.queue.service.*;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.enums.ParameterIn;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.ExampleObject;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
//
// import org.eclipse.angus.mail.handlers.text_plain;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.bind.annotation.RequestBody;
//
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.Executor;
// import java.util.stream.Collectors;
//
// @CrossOrigin
// @RestController
// @RequestMapping("/fila")
// @Tag(name = "Fila", description = "Queue endpoints")
// public class QueueController {
//
//   @Autowired
//   private FilaDePacientesService queueService;
//
//   @Autowired
//   private EspecialistaRepository especialistaRepository;
//
//   @Autowired
//   private AgendamentoService agendamentoService;
//
//   @Autowired
//   private EspecialistaService especialistaService;
//
//   @Autowired
//   private PacienteRepository pacienteRepository;
//
//   @Autowired
//   private ContaRepository contaRepository;
//
//   @Autowired
//   private AgendamentoRepository agendamentoRepository;
//
//   @PostMapping("/criarEspecialista")
//   @Operation(summary = "Create new Especialista / Médico.", description = """
//     Cria um especialista com os dados passados e cria uma
//     conta_especialista para ele com a respecitva especialidade.
//
//     ## Fluxo:
//     1. Salva as possíveis Indisponibilidades
//     2. Sistema cria a conta_especialista
//     3. Adiciona o especialista
//
//     ## Restrições:
//     - Só funciona se os campos do requestBody especialista forem preenchidos corretamente
//     - Bloqueia se houver campo null
//     """, tags = { "Especialista" }, operationId = "AdicionaEspecialista",
//     responses = {
//       @ApiResponse(responseCode = "201", description = "Especialista added with success.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso." ), examples = @ExampleObject("Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso."))),
//       @ApiResponse(responseCode = "400", description = "Field [name_field] is required.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("Campo [field_name] é obrigatório.")))
//     }
//   )
//   public ResponseEntity<?> criarEspecialista(
//       @io.swagger.v3.oas.annotations.parameters.RequestBody(
//         description = "Dados completos do novo especialista a ser criado.",
//         required = true,
//         content = @Content(schema = @Schema(implementation = Especialista.class)))
//
//       @Valid @RequestBody Especialista especialistaRequest
//   ) {
//     // Chama o serviço para criar o especialista
//     especialistaService.criarEspecialista(especialistaRequest);
//     return ResponseEntity.status(HttpStatus.CREATED)
//         .body("Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso.");
//   }
//
//   @PostMapping("/agendar")
//   @Operation(
//     summary = "Creating scheduling and registering patient.",
//     description = """
//       Adiciona o agendamento e atualiza as informações do paciente.
//
//       ## Fluxo:
//       1. Busca o paciente pelo CPF
//       2. Sistema busca o paciente pelo CPF
//       3. Se paciente já existe atualiza as informações do paciente
//       4. Se paciente não existe, adiciona-se um novo paciente com o agendamento e conta_paciente
//
//       ## Restrições:
//       - Só funciona se os campos do requestBody agendamento forem preenchidos corretamente
//       - Bloqueia se houver campo null
//     """, tags = { "Agendamento" }, operationId = "CriaAgendamento",
//     responses = {
//       @ApiResponse( responseCode = "201", description = "Create Schedule and update Patiente if exist.", content = @Content(mediaType = "text/plain", examples = @ExampleObject("Agendamento feito com sucesso."))),
//       @ApiResponse( responseCode = "400", description = "Field [field_name] is required.", content = @Content(mediaType = "application/json", examples = @ExampleObject("Campo [nome_do_campo] é obrigatório.")))
//     }
//   )
//   public ResponseEntity<?> adicionarAgendamento(
//     @io.swagger.v3.oas.annotations.parameters.RequestBody(
//       required = true,
//       description = "Dados completos do novo especialista a ser criado."
//     )
//     @Valid @RequestBody Agendamento agendamento) {
//       // Chama o serviço para processar o agendamento
//       agendamentoService.adicionarAgendamento(agendamento);
//       return ResponseEntity.status(HttpStatus.CREATED).body("Agendamento feito com sucesso.");
//   }
//
//
//
//   @GetMapping("/findEspecialista")
//   @Operation(
//     summary = "Seeking all registered specialists.",
//     description = """
//       Find All Especialistas in database with pagination.
//
//       ## Fluxo:
//       1. Busca todos os especialistas com paginação
//       2. Mapea os resultados para EspecialistaRecordDtoResponce.java
//
//     """, tags = { "Especialistas" }, operationId = "AcharEspecialistas",
//     responses = {
//       @ApiResponse( responseCode = "200", description = "List all Especialistas.")
//     }
//   )
//   public List<EspecialistaRecordDtoResponce> getAllEspecialista(
//       @RequestParam(name = "page", defaultValue = "0") Integer page,
//       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
//
//     Page<Especialista> especialistas = especialistaService.findAllEspecialistas(page, pageSize);
//
//     return especialistas.stream()
//         .map( (especialista) -> new EspecialistaRecordDtoResponce(especialista))
//         .collect(Collectors.toList());
//   }
//
//   @GetMapping("/pegarAgendamentos")
//   @Operation(summary = "Taking all the scheduled schedules for today.",
//     description = """
//     Find All Agendamentos in database based in Paciente code.
//
//     ## Fluxo:
//     1. Busca paciente pelo parametro codigoCodigo
//     2. Busca todos os seus agendamentos para o dia atual
//
//     ## Restrições:
//     - Só funciona se o campo codigoCodigo for não nulo e válido
//     """, tags = { "Agendamento" }, operationId = "AcharAgendamentos",
//     parameters = {
//       @Parameter(in = ParameterIn.QUERY, name = "codigoCodigo", description = "Código do paciente.")
//     }
//   )
//   public List<AgendamentoDTO> getAgendaamentosByCodigoCodigo(@RequestParam String codigoCodigo) {
//     return agendamentoService.getAllAgendamentosByCodigoCodigo(codigoCodigo);
//   }
//
//
//
//   @GetMapping("/login")
//   @Operation(summary = "Seeking authorization according to credentials.",
//     description = """
//     Find the user role based in the login.
//
//     ## Fluxo:
//     1. Busca usuário
//     2. Confirma as credenciais de login
//     2. Retorna a Role do usuário
//
//     """, tags = { "login", "Paciente", "Especialista" }, operationId = "fazerLogin",
//     parameters = {
//       @Parameter(name = "emailOrCpf", description = "User Email"),
//       @Parameter(name = "password", description = "User Password")
//     },
//     responses = {
//       @ApiResponse(responseCode = "200", description = "Login efetuado com sucesso.")
//     }
//   )
//   public ResponseEntity<?> getRoleByLogin(
//       @RequestParam String emailOrCpf,
//       @RequestParam String password) {
//
//     var response = queueService.findRoleByLogin(emailOrCpf, password);
//     return ResponseEntity.ok(response);
//   }
//
//
//   @PutMapping("/marcarPresenca")
//   @Operation(summary = "Confirming the presence of the patient for a certain appointment.", description = """
//       Marca a presença do paciente na clínica, alterando o status do agendamento
//       de AGUARDANDO_CONFIRMACAO para EM_ESPERA.
//
//       ## Fluxo:
//       1. Sistema valida se paciente existe
//       2. verifica se o agendamento existe
//       3. Verifica se já está em espera (evita duplicidade)
//       4. Valida estado do agendamento
//       5. Atualiza status e horário de chegada
//
//       ## Restrições:
//       - Só funciona se status atual for AGUARDANDO_CONFIRMACAO
//       - Bloqueia se paciente já tiver outro agendamento EM_ESPERA
//       """, tags = { "Fila", "Paciente" }, operationId = "confirmarPresenca",
//     parameters = {
//       @Parameter(in = ParameterIn.QUERY, name = "codigoCodigo", description = "Patient recognition code."),
//       @Parameter(in = ParameterIn.QUERY, name = "id_agendamento", description = "Patient-related scheduling ID")
//     }, responses = {
//       @ApiResponse(responseCode = "200", description = "Attendance was successfully confirmed.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("Sua presença foi confirmada com Sucesso!"))),
//       @ApiResponse(responseCode = "409", description = "Schedule already have status EM_ESPERA.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("O paciente já possui um agendamento com o status EM_ESPERA."))),
//       @ApiResponse(responseCode = "404", description = "Patient or Schedule not found.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = {
//           @ExampleObject(name = "paciente_nao_encontrado", summary = "Quando paciente não existe", value = "Paciente não encontrado."),
//           @ExampleObject(name = "agendamento_nao_encontrado", summary = "Quando agendamento não existe", value = "Agendamento não encontrado.")
//       }))
//   })
//   public ResponseEntity<String> marcandoPresenca(
//       @RequestParam String codigoCodigo,
//       @RequestParam Long id_agendamento) {
//
//     queueService.marcarPresenca(codigoCodigo, id_agendamento);
//     return ResponseEntity.ok("Sua presença foi confirmada com Sucesso!");
//   }
//
// }
