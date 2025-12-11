package com.ddaaniel.queue.controller;

import com.ddaaniel.queue.domain.model.*;
import com.ddaaniel.queue.domain.model.dto.AgendamentoDTO;
import com.ddaaniel.queue.domain.model.dto.EspecialistaRecordDtoResponce;
import com.ddaaniel.queue.domain.model.enuns.Prioridade;
import com.ddaaniel.queue.domain.model.enuns.Role;
import com.ddaaniel.queue.domain.model.enuns.StatusAgendamento;
import com.ddaaniel.queue.domain.model.enuns.TipoEspecialista;
import com.ddaaniel.queue.domain.repository.*;
import com.ddaaniel.queue.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.eclipse.angus.mail.handlers.text_plain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/fila")
@Tag(name = "Fila", description = "Queue endpoints")
public class QueueController {

  @Autowired
  private FilaDePacientesService queueService;

  @Autowired
  private EspecialistaRepository especialistaRepository;

  @Autowired
  private AgendamentoService agendamentoService;

  @Autowired
  private EspecialistaService especialistaService;

  @Autowired
  private PacienteRepository pacienteRepository;

  @Autowired
  private ContaRepository contaRepository;

  @Autowired
  private AgendamentoRepository agendamentoRepository;

  @PostMapping("/criarEspecialista")
  @Operation(summary = "Create new Especialista / Médico.", description = """
    Cria um especialista com os dados passados e cria uma
    conta_especialista para ele com a respecitva especialidade.

    ## Fluxo:
    1. Salva as possíveis Indisponibilidades
    2. Sistema cria a conta_especialista
    3. Adiciona o especialista

    ## Restrições:
    - Só funciona se os campos do requestBody especialista forem preenchidos corretamente
    - Bloqueia se houver campo null
    """, tags = { "Especialista" }, operationId = "AdicionaEspecialista",
    responses = {
      @ApiResponse(responseCode = "201", description = "Especialista added with success.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso." ), examples = @ExampleObject("Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso."))),
      @ApiResponse(responseCode = "400", description = "Field [name_field] is required.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("Campo [field_name] é obrigatório.")))
    }
  )
  public ResponseEntity<?> criarEspecialista(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Dados completos do novo especialista a ser criado.",
        required = true,
        content = @Content(schema = @Schema(implementation = Especialista.class)))

      @Valid @RequestBody Especialista especialistaRequest
  ) {
    // Chama o serviço para criar o especialista
    especialistaService.criarEspecialista(especialistaRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body("Especialista criado com sucesso! Verifique o e-mail para as credenciais de acesso.");
  }

  @PostMapping("/agendar")
  @Operation(
    summary = "Creating scheduling and registering patient.",
    description = """
      Adiciona o agendamento e atualiza as informações do paciente.

      ## Fluxo:
      1. Busca o paciente pelo CPF
      2. Sistema busca o paciente pelo CPF
      3. Se paciente já existe atualiza as informações do paciente
      4. Se paciente não existe, adiciona-se um novo paciente com o agendamento e conta_paciente

      ## Restrições:
      - Só funciona se os campos do requestBody agendamento forem preenchidos corretamente
      - Bloqueia se houver campo null
    """, tags = { "Agendamento" }, operationId = "CriaAgendamento",
    responses = {
      @ApiResponse( responseCode = "201", description = "Create Schedule and update Patiente if exist.", content = @Content(mediaType = "text/plain", examples = @ExampleObject("Agendamento feito com sucesso."))),
      @ApiResponse( responseCode = "400", description = "Field [field_name] is required.", content = @Content(mediaType = "application/json", examples = @ExampleObject("Campo [nome_do_campo] é obrigatório.")))
    }
  )
  public ResponseEntity<?> adicionarAgendamento(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
      required = true,
      description = "Dados completos do novo especialista a ser criado.",
      content = @Content(schema = @Schema(implementation = Agendamento.class))
    )
    @Valid @RequestBody Agendamento agendamento) {
      // Chama o serviço para processar o agendamento
      agendamentoService.adicionarAgendamento(agendamento);
      return ResponseEntity.status(HttpStatus.CREATED).body("Agendamento feito com sucesso.");
  }



  @GetMapping("/findEspecialista")
  @Operation(
    summary = "Seeking all registered specialists.",
    description = """
      ....

      ## Fluxo:
      1.
      2.
      3.
      4.

      ## Restrições:
      - ...
      - ...
    """, tags = { "Agendamento" }, operationId = "CriaAgendamento"/* , */
    // responses = {
    //   @ApiResponse( responseCode = "201", description = "Create Schedule and update Patiente if exist.", content = @Content(mediaType = "text/plain", examples = @ExampleObject("Agendamento feito com sucesso."))),
    //   @ApiResponse( responseCode = "400", description = "Field [field_name] is required.", content = @Content(mediaType = "application/json", examples = @ExampleObject("Campo [nome_do_campo] é obrigatório.")))
    // }
  )
  public List<EspecialistaRecordDtoResponce> getAllEspecialista(
      @RequestParam(name = "page", defaultValue = "0") Integer page,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

    Page<Especialista> especialistas = especialistaService.findAllEspecialistas(page, pageSize);

    return especialistas.stream()
        .map(EspecialistaRecordDtoResponce::new)
        .collect(Collectors.toList());
  }

  @GetMapping("/pegarAgendamentos")
  @Operation(summary = "Taking all the scheduled schedules for today.")
  public List<AgendamentoDTO> getAgendaamentosByCodigoCodigo(@RequestParam String codigoCodigo) {

    return agendamentoService.getAllAgendamentosByCodigoCodigo(codigoCodigo);
  }

  @GetMapping("/login")
  @Operation(summary = "Seeking authorization according to credentials.")
  public ResponseEntity<?> getRoleByLogin(
      @RequestParam String emailOrCpf,
      @RequestParam String password) {

    Optional<Paciente> pacienteOpt = pacienteRepository.findByCodigoCodigo(password);
    // Optional<Cadastramento> cadastramentoOpt =
    // cadastramentoRepository.findByCodigoCodigo(password);
    Optional<Conta> contaOpt = contaRepository.findByPassword(password);

    if (pacienteOpt.isPresent() && emailOrCpf.equals(pacienteOpt.get().getEmail())) {
      Paciente paciente = pacienteOpt.get();
      Map<String, Object> response = new HashMap<>();
      response.put("role", paciente.getRole());
      response.put("idPaciente", paciente.getId_paciente());
      return ResponseEntity.ok(response);
      // } else if (cadastramentoOpt.isPresent() &&
      // emailOrCpf.equals(cadastramentoOpt.get().getEmail())) {
      // return ResponseEntity.ok(cadastramentoOpt.get().getRole());
    } else if (contaOpt.isPresent() && emailOrCpf.equals(contaOpt.get().getLogin())) {
        Conta conta = contaOpt.get();
        Role role = conta.getRoleEnum();

      if (role == Role.ESPECIALISTA) {
        Especialista especialista = conta.getEspecialista();
        if (especialista != null) {
          Map<String, Object> response = new HashMap<>();
          response.put("role", role);
          response.put("idEspecialista", especialista.getId());
          return ResponseEntity.ok(response);
        } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Erro: Conta do especialista não associada a nenhum especialista.");
        }
      }

      if (role == Role.PACIENTE) {
        Paciente paciente = conta.getPaciente();
        if (paciente != null) {
          Map<String, Object> response = new HashMap<>();
          response.put("role", role);
          response.put("idPaciente", paciente.getId_paciente());
          return ResponseEntity.ok(response);
        } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Erro: Conta do paciente não associada a nenhum paciente.");
        }
      }

      return ResponseEntity.ok(role);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Email ou senha incorretos.");
    }

  }

  @PutMapping("/marcarPresenca")
  @Operation(summary = "Confirming the presence of the patient for a certain appointment.", description = """
      Marca a presença do paciente na clínica, alterando o status do agendamento
      de AGUARDANDO_CONFIRMACAO para EM_ESPERA.

      ## Fluxo:
      1. Sistema valida se paciente existe
      2. verifica se o agendamento existe
      3. Verifica se já está em espera (evita duplicidade)
      4. Valida estado do agendamento
      5. Atualiza status e horário de chegada

      ## Restrições:
      - Só funciona se status atual for AGUARDANDO_CONFIRMACAO
      - Bloqueia se paciente já tiver outro agendamento EM_ESPERA
      """, tags = { "Fila", "Paciente" }, operationId = "confirmarPresenca",
    parameters = {
      @Parameter(in = ParameterIn.QUERY, name = "codigoCodigo", description = "Patient recognition code."),
      @Parameter(in = ParameterIn.QUERY, name = "id_agendamento", description = "Patient-related scheduling ID")
    }, responses = {
      @ApiResponse(responseCode = "200", description = "Attendance was successfully confirmed.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("Sua presença foi confirmada com Sucesso!"))),
      @ApiResponse(responseCode = "409", description = "Schedule already have status EM_ESPERA.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = @ExampleObject("O paciente já possui um agendamento com o status EM_ESPERA."))),
      @ApiResponse(responseCode = "404", description = "Patient or Schedule not found.", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"), examples = {
          @ExampleObject(name = "paciente_nao_encontrado", summary = "Quando paciente não existe", value = "Paciente não encontrado."),
          @ExampleObject(name = "agendamento_nao_encontrado", summary = "Quando agendamento não existe", value = "Agendamento não encontrado.")
      }))
  })
  public ResponseEntity<String> marcandoPresenca(
      @RequestParam String codigoCodigo,
      @RequestParam Long id_agendamento) {

    queueService.marcarPresenca(codigoCodigo, id_agendamento);
    return ResponseEntity.ok("Sua presença foi confirmada com Sucesso!");
  }

  @GetMapping("/primeirosPacientesDeTodosEspecialistas")
  @Operation(summary = "Retrieve the first patient on hold and the patient being cared for now for all specialists.")
  public ResponseEntity<List<Map<String, Object>>> getPrimeirosPacientesPorEspecialistas() {
    return ResponseEntity.ok(getPrimeirosPacientesPorEspecialistasInfo());
  }

  // Método auxiliar para obter os pacientes por todos os especialistas
  private List<Map<String, Object>> getPrimeirosPacientesPorEspecialistasInfo() {
    List<Map<String, Object>> especialistasPacientesInfo = new ArrayList<>();

    // Busca todos os especialistas cadastrados no sistema
    List<Especialista> especialistas = especialistaRepository.findAll();

    for (Especialista especialista : especialistas) {
      Map<String, Object> especialistaInfo = new HashMap<>();
      Map<String, Object> pacienteInfo = new HashMap<>();

      Long especialistaId = especialista.getId();

      // Buscar agendamentos com status EM_ESPERA para o especialista
      List<Agendamento> agendamentosEmEspera = agendamentoRepository
          .findAllByEspecialista_IdAndStatusAndPaciente_PresencaConfirmado(
              especialistaId, StatusAgendamento.EM_ESPERA, true);

      Optional<Agendamento> primeiroEmEspera = agendamentosEmEspera.stream()
          .sorted(Comparator.comparingInt((Agendamento a) -> a.getPaciente().getPrioridade().getPrioridade())
              .thenComparing(Agendamento::getDataAgendamento))
          .findFirst();

      // Buscar agendamentos com status EM_ATENDIMENTO para o especialista
      List<Agendamento> agendamentosEmAtendimento = agendamentoRepository
          .findAllByEspecialista_IdAndStatusAndPaciente_PresencaConfirmado(
              especialistaId, StatusAgendamento.EM_ATENDIMENTO, true);

      Optional<Agendamento> primeiroEmAtendimento = agendamentosEmAtendimento.stream()
          .findFirst();

      // Dados do paciente em espera (ou null se não houver)
      pacienteInfo.put("PacienteEmEspera", primeiroEmEspera.map(agendamento -> {
        Map<String, String> esperaInfo = new HashMap<>();
        esperaInfo.put("Nome", agendamento.getPaciente().getNomeCompleto());
        esperaInfo.put("Status", agendamento.getStatus().name());
        return esperaInfo;
      }).orElse(null));

      // Dados do paciente em atendimento (ou null se não houver)
      pacienteInfo.put("PacienteEmAtendimento", primeiroEmAtendimento.map(agendamento -> {
        Map<String, String> atendimentoInfo = new HashMap<>();
        atendimentoInfo.put("Nome", agendamento.getPaciente().getNomeCompleto());
        atendimentoInfo.put("Status", agendamento.getStatus().name());
        return atendimentoInfo;
      }).orElse(null));

      // Adicionar informações do especialista
      especialistaInfo.put("EspecialistaId", especialistaId);
      especialistaInfo.put("Nome", especialista.getNome());
      especialistaInfo.put("TipoEspecialista", especialista.getTipoEspecialista().name());
      especialistaInfo.put("Pacientes", pacienteInfo);

      especialistasPacientesInfo.add(especialistaInfo);
    }

    return especialistasPacientesInfo;
  }

  @GetMapping("/contagemEspecialista/{especialistaId}")
  @Operation(summary = "Counts the amount of patient waiting for consultation.")
  public ResponseEntity<Map<String, Integer>> getContagemPacientesPorEspecialista(
      @PathVariable Long especialistaId) {
    return ResponseEntity.ok(contarPacientesPorEspecialistaId(especialistaId));
  }

  // Método auxiliar para contar os pacientes em espera por ID do especialista
  private Map<String, Integer> contarPacientesPorEspecialistaId(Long especialistaId) {
    int contagem = agendamentoRepository.countByEspecialista_IdAndStatus(
        especialistaId, StatusAgendamento.EM_ESPERA);

    // Criar JSON de resposta com a contagem
    Map<String, Integer> contagemResponse = new HashMap<>();
    contagemResponse.put("QuantidadePacientesEmEspera", contagem);

    return contagemResponse;
  }

  @PutMapping("/chamarPaciente")
  @Operation(summary = "Call the first patient in line with the status EM_ESPERA.")
  public ResponseEntity<?> chamarPaciente(@RequestParam Long idEspecialista) {
    return chamarPrimeiroPacientePorEspecialista(idEspecialista);
  }

  private int prioridadeContador = 0; // Contador para alternância entre prioridades

  private ResponseEntity<?> chamarPrimeiroPacientePorEspecialista(Long idEspecialista) {
    // Busca todos os agendamentos em espera para o especialista com presença
    // confirmada
    List<Agendamento> agendamentos = agendamentoRepository
        .findAllByEspecialista_IdAndStatusAndPaciente_PresencaConfirmado(
            idEspecialista, StatusAgendamento.EM_ESPERA, true);

    if (agendamentos.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Nenhum paciente em espera para o especialista com ID " + idEspecialista);
    }

    // Separamos pacientes com e sem prioridade
    List<Agendamento> comPrioridade = agendamentos.stream()
        .filter(a -> a.getPaciente().getPrioridade() == Prioridade.PESSOA_COM_ALGUMA_PRIORIDADE)
        .sorted(Comparator.comparing(Agendamento::getDataHoraChegada))
        .toList();

    List<Agendamento> semPrioridade = agendamentos.stream()
        .filter(a -> a.getPaciente().getPrioridade() == Prioridade.NENHUM)
        .sorted(Comparator.comparing(Agendamento::getDataHoraChegada))
        .toList();

    // Seleciona o próximo paciente com base na alternância
    Agendamento proximoAgendamento = null;

    if (!comPrioridade.isEmpty() && (prioridadeContador < 2 || semPrioridade.isEmpty())) {
      // Chama paciente com prioridade (máximo de 2 seguidos)
      proximoAgendamento = comPrioridade.get(0);
      prioridadeContador++;
    } else if (!semPrioridade.isEmpty()) {
      // Chama paciente sem prioridade
      proximoAgendamento = semPrioridade.get(0);
      prioridadeContador = 0; // Reseta o contador ao chamar um paciente sem prioridade
    }

    if (proximoAgendamento != null) {
      // Atualiza o status do paciente para EM_ATENDIMENTO
      proximoAgendamento.setStatus(StatusAgendamento.EM_ATENDIMENTO);
      agendamentoRepository.save(proximoAgendamento);

      // Retorna as informações do paciente escolhido
      Paciente paciente = proximoAgendamento.getPaciente();
      Map<String, Object> pacienteInfo = new HashMap<>();
      pacienteInfo.put("id", paciente.getId_paciente());
      pacienteInfo.put("nome", paciente.getNomeCompleto());
      pacienteInfo.put("sexo", paciente.getSexo());
      pacienteInfo.put("prioridade", paciente.getPrioridade().name());
      pacienteInfo.put("horaChegada", paciente.getDataHoraChegada());
      pacienteInfo.put("status", proximoAgendamento.getStatus());

      return ResponseEntity.ok(pacienteInfo);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Nenhum paciente disponível para atendimento no momento.");
    }
  }

  /**
   * Endpoint para adicionar uma observação ao prontuário de um paciente e marcar
   * o agendamento como concluído.
   *
   * @param pacienteId     ID do paciente cujo prontuário será atualizado.
   * @param novaObservacao A nova observação a ser adicionada ao prontuário.
   * @return ResponseEntity indicando o sucesso ou erro da operação.
   */
  @PutMapping("/adicionarObservacaoProntuario")
  @Operation(summary = "Edits the patient's report that is in attendance.")
  public ResponseEntity<String> adicionarObservacaoProntuario(
      @RequestParam Long pacienteId,
      @RequestParam String novaObservacao) {

    // Busca o paciente pelo ID
    Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);

    if (pacienteOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Paciente não encontrado.");
    }

    Paciente paciente = pacienteOpt.get();

    // Atualiza o campo prontuario com a nova observação
    String prontuarioAtualizado = paciente.getProntuario() == null
        ? novaObservacao
        : paciente.getProntuario() + "\n" + novaObservacao;

    paciente.setProntuario(prontuarioAtualizado);

    // Busca o agendamento do paciente que está em atendimento
    Optional<Agendamento> agendamentoOpt = agendamentoRepository
        .findByPacienteAndStatus(paciente, StatusAgendamento.EM_ATENDIMENTO);

    if (agendamentoOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Nenhum agendamento em atendimento encontrado para este paciente.");
    }

    Agendamento agendamento = agendamentoOpt.get();

    // Atualiza o status do agendamento para CONCLUIDO
    agendamento.setStatus(StatusAgendamento.CONCLUIDO);

    // Salva as alterações no banco de dados
    pacienteRepository.save(paciente);
    agendamentoRepository.save(agendamento);

    return ResponseEntity.ok("Observação adicionada ao prontuário e agendamento concluído com sucesso.");

  }

}
