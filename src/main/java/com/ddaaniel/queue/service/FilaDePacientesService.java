// package com.ddaaniel.queue.service;
//
// import com.ddaaniel.queue.domain.model.Agendamento;
// import com.ddaaniel.queue.domain.model.Conta;
// import com.ddaaniel.queue.domain.model.Especialista;
// import com.ddaaniel.queue.domain.model.Paciente;
// import com.ddaaniel.queue.domain.model.enuns.Prioridade;
// import com.ddaaniel.queue.domain.model.enuns.Role;
// import com.ddaaniel.queue.domain.model.enuns.StatusAgendamento;
// import com.ddaaniel.queue.domain.repository.AgendamentoRepository;
// import com.ddaaniel.queue.domain.repository.ContaRepository;
// import com.ddaaniel.queue.domain.repository.PacienteRepository;
// import com.ddaaniel.queue.exception.ConflitoDeStatusException;
// import com.ddaaniel.queue.exception.LoginIncorretoException;
// import com.ddaaniel.queue.exception.RecursoNaoEncontradoException;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.time.LocalDateTime;
// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;
//
// @Service
// public class FilaDePacientesService {
//
//   @Autowired
//   private PacienteRepository pacienteRepository;
//
//   @Autowired
//   private AgendamentoRepository agendamentoRepository;
//
//   @Autowired
//   private ContaRepository contaRepository;
//
//
//   // private int prioridadeContador = 0; // Contador para alternância entre prioridades
//   //
//   // public ResponseEntity<String> chamarPrimeiroPacientePorEspecialista(Long idEspecialista) {
//   //   // Busca todos os agendamentos em espera para o especialista com presença
//   //   // confirmada
//   //   List<Agendamento> agendamentos = agendamentoRepository
//   //       .findAllByEspecialista_IdAndStatusAndPaciente_PresencaConfirmado(
//   //           idEspecialista, StatusAgendamento.EM_ESPERA, true);
//   //
//   //   if (agendamentos.isEmpty()) {
//   //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
//   //         .body("Nenhum paciente em espera para o especialista com ID " + idEspecialista);
//   //   }
//   //
//   //   // Separamos pacientes com e sem prioridade
//   //   List<Agendamento> comPrioridade = agendamentos.stream()
//   //       .filter(a -> a.getPaciente().getPrioridade() == Prioridade.PESSOA_COM_ALGUMA_PRIORIDADE)
//   //       .sorted(Comparator.comparing(Agendamento::getDataHoraChegada))
//   //       .toList();
//   //
//   //   List<Agendamento> semPrioridade = agendamentos.stream()
//   //       .filter(a -> a.getPaciente().getPrioridade() == Prioridade.NENHUM)
//   //       .sorted(Comparator.comparing(Agendamento::getDataHoraChegada))
//   //       .toList();
//   //
//   //   // Seleciona o próximo paciente com base na alternância
//   //   Agendamento proximoAgendamento = null;
//   //
//   //   if (!comPrioridade.isEmpty() && (prioridadeContador < 2 || semPrioridade.isEmpty())) {
//   //     // Chama paciente com prioridade (máximo de 2 seguidos)
//   //     proximoAgendamento = comPrioridade.get(0);
//   //     prioridadeContador++;
//   //   } else if (!semPrioridade.isEmpty()) {
//   //     // Chama paciente sem prioridade
//   //     proximoAgendamento = semPrioridade.get(0);
//   //     prioridadeContador = 0; // Reseta o contador ao chamar um paciente sem prioridade
//   //   }
//   //
//   //   if (proximoAgendamento != null) {
//   //     // Atualiza o status do paciente para EM_ATENDIMENTO
//   //     proximoAgendamento.setStatus(StatusAgendamento.EM_ATENDIMENTO);
//   //     agendamentoRepository.save(proximoAgendamento);
//   //
//   //     // Retorna as informações do paciente escolhido
//   //     Paciente paciente = proximoAgendamento.getPaciente();
//   //     Map<String, Object> pacienteInfo = new HashMap<>();
//   //     pacienteInfo.put("id", paciente.getId_paciente());
//   //     pacienteInfo.put("nome", paciente.getNomeCompleto());
//   //     pacienteInfo.put("sexo", paciente.getSexo());
//   //     pacienteInfo.put("prioridade", paciente.getPrioridade().name());
//   //     pacienteInfo.put("horaChegada", paciente.getDataHoraChegada());
//   //     pacienteInfo.put("status", proximoAgendamento.getStatus());
//   //
//   //     return ResponseEntity.ok(pacienteInfo);
//   //   } else {
//   //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
//   //         .body("Nenhum paciente disponível para atendimento no momento.");
//   //   }
//   // }
//
//
//
//   public Role findRoleByLogin(String emailOrCpf, String password) {
//
//     Paciente pacienteOpt = pacienteRepository.findByCodigoCodigo(password)
//         .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));
//     Conta contaOpt = contaRepository.findByPassword(password)
//         .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma conta relacionada econtrada."));
//
//     if (emailOrCpf.equals(pacienteOpt.getEmail()) && emailOrCpf.equals(contaOpt.getLogin())) {
//       Conta conta = contaOpt;
//       Role role = conta.getRoleEnum();
//
//       if (role == Role.ESPECIALISTA) {
//         Especialista especialista = conta.getEspecialista();
//         if (especialista != null) {
//           return role;
//         } else {
//           throw new RecursoNaoEncontradoException("Erro: Conta do especialista não associada a nenhum especialista.");
//         }
//       }
//
//       if (role == Role.PACIENTE) {
//         Paciente paciente = conta.getPaciente();
//         if (paciente != null) {
//           return role;
//         } else {
//           throw new RecursoNaoEncontradoException("Erro: Conta do paciente não associada a nenhum paciente.");
//         }
//       }
//       return role;
//     } else {
//       throw new LoginIncorretoException("Email ou senha incorretos.");
//     }
//   }
//
//   @Transactional
//   public void marcarPresenca(String codigoCodigo, Long id_agendamento) {
//     // Busca o paciente pelo código
//     var pacienteOpt = pacienteRepository.findByCodigoCodigo(codigoCodigo)
//         .orElseThrow(() -> new RecursoNaoEncontradoException("Código paciente não encontrado."));
//
//     // Busca o agendamento pelo ID
//     var agendamentoOpt = agendamentoRepository.findById(id_agendamento)
//         .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));
//
//     if (agendamentoOpt.getStatus() != StatusAgendamento.AGUARDANDO_CONFIRMACAO)
//       throw new ConflitoDeStatusException("O paciente já possui um agendamento com o status EM_ESPERA.");
//
//     // Atualiza o status do agendamento e o campo dataHoraChegada
//     agendamentoOpt.setStatus(StatusAgendamento.EM_ESPERA);
//     agendamentoOpt.setDataHoraChegada(LocalDateTime.now());
//     agendamentoRepository.save(agendamentoOpt); // SALVA IMEDIATAMENTE O AGENDAMENTO
//
//     // Atualiza o paciente se necessário
//     if (!pacienteOpt.getPresencaConfirmado()) {
//       pacienteOpt.setPresencaConfirmado(true);
//       pacienteOpt.setDataHoraChegada(LocalDateTime.now());
//       pacienteRepository.save(pacienteOpt); // SALVA IMEDIATAMENTE O PACIENTE
//     }
//
//   }
//
//   // // Busca o paciente pelo código
//   // var pacienteOpt = pacienteRepository.findByCodigoCodigo(codigoCodigo);
//   //
//   // if (pacienteOpt.isPresent()) {
//   // Paciente objPaciente = pacienteOpt.get();
//   //
//   // // Verifica se já existe um agendamento com status EM_ESPERA para o paciente
//   // boolean jaEmEspera =
//   // agendamentoRepository.existsByPacienteAndStatus(objPaciente,
//   // StatusAgendamento.EM_ESPERA);
//   // if (jaEmEspera) {
//   // return ResponseEntity.status(HttpStatus.CONFLICT)
//   // .body("O paciente já possui um agendamento com o status EM_ESPERA.");
//   // }
//   //
//   // // Busca o agendamento pelo ID
//   // Optional<Agendamento> agendamentoOpt =
//   // agendamentoRepository.findById(id_agendamento);
//   // if (agendamentoOpt.isPresent()) {
//   // Agendamento objAgendamento = agendamentoOpt.get();
//   //
//   // if (objAgendamento.getStatus() != StatusAgendamento.AGUARDANDO_CONFIRMACAO) {
//   // throw new IllegalStateException("O agendamento não está em um estado válido
//   // para essa operação.");
//   // }
//   //
//   // // Atualiza o status do agendamento e o campo dataHoraChegada
//   // objAgendamento.setStatus(StatusAgendamento.EM_ESPERA);
//   // objAgendamento.setDataHoraChegada(LocalDateTime.now());
//   // agendamentoRepository.save(objAgendamento); // SALVA IMEDIATAMENTE O
//   // AGENDAMENTO
//   //
//   // // Atualiza o paciente se necessário
//   // if (!objPaciente.getPresencaConfirmado()) {
//   // objPaciente.setPresencaConfirmado(true);
//   // objPaciente.setDataHoraChegada(LocalDateTime.now());
//   // pacienteRepository.save(objPaciente); // SALVA IMEDIATAMENTE O PACIENTE
//   // }
//   //
//   // return ResponseEntity.status(HttpStatus.OK)
//   // .body("Sua presença foi confirmada com Sucesso!");
//   // } else {
//   // return ResponseEntity.status(HttpStatus.NOT_FOUND)
//   // .body("Agendamento não encontrado.");
//   // }
//   // } else {
//   // return ResponseEntity.status(HttpStatus.NOT_FOUND)
//   // .body("Paciente não encontrado.");
//   // }
//
//   // Adicionar paciente na fila (salva no banco de dados)
//   public void adicionarAgendamento(Agendamento agendamento) {
//     String codigo;
//     Paciente paciente = agendamento.getPaciente();
//     do {
//
//       // Gera um código aleatório
//       paciente.gerarCodigoCodigo();
//       codigo = paciente.getCodigoCodigo();
//
//     } while (!isCodigoUnico(codigo)); // Verifica se é único
//     agendamentoRepository.save(agendamento);
//   }
//
//   // Adicionar paciente na fila (salva no banco de dados) mantendo o atributo
//   // codigoCodigo que
//   // paciente ja possui.
//   public void adicionarAgendamentoo(Agendamento agendamento) {
//     // String codigo;
//     Paciente paciente = agendamento.getPaciente();
//     /*
//      * do {
//      *
//      * // Gera um código aleatório
//      * paciente.gerarCodigoCodigo();
//      * codigo = paciente.getCodigoCodigo();
//      *
//      * } while (!isCodigoUnico(codigo)); // Verifica se é único
//      */
//     agendamentoRepository.save(agendamento);
//
//   }
//
//   // Verifica se o código gerado é único no banco de dados
//   private boolean isCodigoUnico(String codigo) {
//     return !pacienteRepository.existsByCodigoCodigo(codigo);
//   }
//
//   // Chamar o próximo paciente (o de maior prioridade)
//   public Paciente chamarProximo() {
//     // Buscamos todos os pacientes e os ordenamos
//     List<Paciente> fila = pacienteRepository.findAll().stream()
//         .sorted((p1, p2) -> {
//           // Utilizando diretamente o método getPrioridade do enum CategoriaTriagem
//           int prioridadeComparacao = Integer.compare(
//               p1.getPrioridade().getPrioridade(),
//               p2.getPrioridade().getPrioridade());
//           if (prioridadeComparacao == 0) {
//             // Se as prioridades forem iguais, compara pelo horário de chegada
//             return p1.getDataHoraChegada().compareTo(p2.getDataHoraChegada());
//           }
//           return prioridadeComparacao;
//         }).collect(Collectors.toList());
//     // O metodo ´sorted´ ordena a lista de paciente de acordo com a regra que
//     // colocamos
//     // dentro da função lambda, deixando a lista na ordem "da maior prioridade ->
//     // para a menor".
//
//     if (!fila.isEmpty()) {
//       Paciente proximo = fila.get(0);
//       pacienteRepository.delete(proximo); // Remove o paciente do banco ao ser chamado
//       return proximo;
//     }
//     return null; // Se não houver pacientes na fila
//   }
//
//   // Visualizar a fila (ordenada por prioridade e tempo de chegada)
//   public List<Paciente> verFila() {
//     return pacienteRepository.findAll().stream()
//         .sorted((p1, p2) -> {
//           // Utilizando diretamente o método getPrioridade do enum CategoriaTriagem
//           int prioridadeComparacao = Integer.compare(
//               p1.getPrioridade().getPrioridade(),
//               p2.getPrioridade().getPrioridade());
//           if (prioridadeComparacao == 0) {
//             // Se as prioridades forem iguais, compara pelo horário de chegada
//             return p1.getDataHoraChegada().compareTo(p2.getDataHoraChegada());
//           }
//           return prioridadeComparacao;
//         }).collect(Collectors.toList());
//   }
//
//   // Função auxiliar para obter a prioridade da categoria de triagem
//   private int getPrioridadeCategoria(String categoriaTriagem) {
//     switch (categoriaTriagem.toLowerCase()) {
//       case "vermelho":
//         return 1;
//       case "amarelo":
//         return 2;
//       case "verde":
//         return 3;
//       case "azul":
//         return 4;
//       default:
//         return 5; // Menor prioridade
//     }
//   }
//
//   // Obter o e-mail do paciente pelo ID
//   public String obterEmailPaciente(Long pacienteId) {
//     Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
//     return pacienteOpt.map(Paciente::getEmail).orElse(null);
//   }
//
//   // Obter o código do paciente pelo ID
//   public String obterCodigoPaciente(Long pacienteId) {
//     Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
//     return pacienteOpt.map(Paciente::getCodigoCodigo).orElse(null);
//   }
// }
