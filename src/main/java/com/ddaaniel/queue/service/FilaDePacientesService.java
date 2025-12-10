package com.ddaaniel.queue.service;

import com.ddaaniel.queue.domain.model.Agendamento;
import com.ddaaniel.queue.domain.model.Paciente;
import com.ddaaniel.queue.domain.model.enuns.StatusAgendamento;
import com.ddaaniel.queue.domain.repository.AgendamentoRepository;
import com.ddaaniel.queue.domain.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilaDePacientesService {

  @Autowired
  private PacienteRepository pacienteRepository;

  @Autowired
  private AgendamentoRepository agendamentoRepository;


  public boolean isValidSchedule(String codigoCodigo, Long id_agendamento) {

    var schedule = agendamentoRepository.findById(id_agendamento);
    if (schedule.isEmpty() || schedule.get().getStatus() != StatusAgendamento.AGUARDANDO_CONFIRMACAO) return false;

    if (schedule.get().getPaciente().getCodigoCodigo() != codigoCodigo) return false;


    return true;
  }



  public void marcarPresenca(String codigoCodigo, Long id_agendamento) {
    // Busca o paciente pelo código
    var pacienteOpt = pacienteRepository.findByCodigoCodigo(codigoCodigo);

    if (pacienteOpt.isPresent()) {

      // Verifica se já existe um agendamento com status EM_ESPERA para o paciente
      boolean jaEmEspera = agendamentoRepository.existsByPacienteAndStatus(pacienteOpt.get(),
          StatusAgendamento.EM_ESPERA);
      if (jaEmEspera) {
        // throw new IllegalStateException("O paciente já possui um agendamento com o
        // status EM_ESPERA.");
      }

      // Busca o agendamento pelo ID
      Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(id_agendamento);
      if (agendamentoOpt.isPresent()) {
        Agendamento objAgendamento = agendamentoOpt.get();

        if (objAgendamento.getStatus() != StatusAgendamento.AGUARDANDO_CONFIRMACAO) {
          throw new IllegalStateException("O agendamento não está em um estado válido para essa operação.");
        }

        // Atualiza o status do agendamento e o campo dataHoraChegada
        objAgendamento.setStatus(StatusAgendamento.EM_ESPERA);
        objAgendamento.setDataHoraChegada(LocalDateTime.now());
        agendamentoRepository.save(objAgendamento); // SALVA IMEDIATAMENTE O AGENDAMENTO

        // Atualiza o paciente se necessário
        if (!pacienteOpt.get().getPresencaConfirmado()) {
          pacienteOpt.get().setPresencaConfirmado(true);
          pacienteOpt.get().setDataHoraChegada(LocalDateTime.now());
          pacienteRepository.save(pacienteOpt.get()); // SALVA IMEDIATAMENTE O PACIENTE
        }

        // return ResponseEntity.status(HttpStatus.OK)
        // .body("Sua presença foi confirmada com Sucesso!");
      } else {
        // return ResponseEntity.status(HttpStatus.NOT_FOUND)
        // .body("Agendamento não encontrado.");
      }
    } else {
      // return ResponseEntity.status(HttpStatus.NOT_FOUND)
      // .body("Paciente não encontrado.");
    }
  }

  // Adicionar paciente na fila (salva no banco de dados)
  public void adicionarAgendamento(Agendamento agendamento) {
    String codigo;
    Paciente paciente = agendamento.getPaciente();
    do {

      // Gera um código aleatório
      paciente.gerarCodigoCodigo();
      codigo = paciente.getCodigoCodigo();

    } while (!isCodigoUnico(codigo)); // Verifica se é único
    agendamentoRepository.save(agendamento);
  }

  // Adicionar paciente na fila (salva no banco de dados) mantendo o atributo
  // codigoCodigo que
  // paciente ja possui.
  public void adicionarAgendamentoo(Agendamento agendamento) {
    // String codigo;
    Paciente paciente = agendamento.getPaciente();
    /*
     * do {
     *
     * // Gera um código aleatório
     * paciente.gerarCodigoCodigo();
     * codigo = paciente.getCodigoCodigo();
     *
     * } while (!isCodigoUnico(codigo)); // Verifica se é único
     */
    agendamentoRepository.save(agendamento);

  }

  // Verifica se o código gerado é único no banco de dados
  private boolean isCodigoUnico(String codigo) {
    return !pacienteRepository.existsByCodigoCodigo(codigo);
  }

  // Chamar o próximo paciente (o de maior prioridade)
  public Paciente chamarProximo() {
    // Buscamos todos os pacientes e os ordenamos
    List<Paciente> fila = pacienteRepository.findAll().stream()
        .sorted((p1, p2) -> {
          // Utilizando diretamente o método getPrioridade do enum CategoriaTriagem
          int prioridadeComparacao = Integer.compare(
              p1.getPrioridade().getPrioridade(),
              p2.getPrioridade().getPrioridade());
          if (prioridadeComparacao == 0) {
            // Se as prioridades forem iguais, compara pelo horário de chegada
            return p1.getDataHoraChegada().compareTo(p2.getDataHoraChegada());
          }
          return prioridadeComparacao;
        }).collect(Collectors.toList());
    // O metodo ´sorted´ ordena a lista de paciente de acordo com a regra que
    // colocamos
    // dentro da função lambda, deixando a lista na ordem "da maior prioridade ->
    // para a menor".

    if (!fila.isEmpty()) {
      Paciente proximo = fila.get(0);
      pacienteRepository.delete(proximo); // Remove o paciente do banco ao ser chamado
      return proximo;
    }
    return null; // Se não houver pacientes na fila
  }

  // Visualizar a fila (ordenada por prioridade e tempo de chegada)
  public List<Paciente> verFila() {
    return pacienteRepository.findAll().stream()
        .sorted((p1, p2) -> {
          // Utilizando diretamente o método getPrioridade do enum CategoriaTriagem
          int prioridadeComparacao = Integer.compare(
              p1.getPrioridade().getPrioridade(),
              p2.getPrioridade().getPrioridade());
          if (prioridadeComparacao == 0) {
            // Se as prioridades forem iguais, compara pelo horário de chegada
            return p1.getDataHoraChegada().compareTo(p2.getDataHoraChegada());
          }
          return prioridadeComparacao;
        }).collect(Collectors.toList());
  }

  // Função auxiliar para obter a prioridade da categoria de triagem
  private int getPrioridadeCategoria(String categoriaTriagem) {
    switch (categoriaTriagem.toLowerCase()) {
      case "vermelho":
        return 1;
      case "amarelo":
        return 2;
      case "verde":
        return 3;
      case "azul":
        return 4;
      default:
        return 5; // Menor prioridade
    }
  }

  // Obter o e-mail do paciente pelo ID
  public String obterEmailPaciente(Long pacienteId) {
    Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
    return pacienteOpt.map(Paciente::getEmail).orElse(null);
  }

  // Obter o código do paciente pelo ID
  public String obterCodigoPaciente(Long pacienteId) {
    Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
    return pacienteOpt.map(Paciente::getCodigoCodigo).orElse(null);
  }
}
