package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
	private final TarefaRepository tarefaRepository;
	private final UsuarioRepository usuarioRepository;

	@Override
	public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
		Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
		log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
		return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
	}

	@Override
	public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - detalhaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		log.info("[usuarioPorEmail] {}", usuarioPorEmail);
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
		return tarefa;
	}

	@Override
	public void ativaTarefa(String usuario, UUID idUsuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - ativaTarefa");
		Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
		Tarefa tarefa = validaTarefa(idUsuario, idTarefa, usuarioPorEmail);
		tarefa.ativaTarefa();
		tarefaRepository.desativaTarefa(idUsuario);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - ativaTarefa");

	}

	private Tarefa validaTarefa(UUID idUsuario, UUID idTarefa, Usuario usuarioPorEmail) {
		Tarefa tarefa = tarefaRepository.buscaTarefaPorId(idTarefa)
				.orElseThrow(() -> APIException.build(HttpStatus.BAD_REQUEST, "ID da Tarefa inválido"));
		tarefa.pertenceAoUsuario(usuarioPorEmail);
		tarefa.validaUsuario(idUsuario);
		return tarefa;
	}

	@Override
	public void editaTarefa(String usuario, UUID idTarefa, EditaTarefaRequest editaTarefaRequest) {
		log.info("[inicia] TarefaApplicationService - editaTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefa.altera(editaTarefaRequest);
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - editaTarefa");
	}

	@Override
	public void incrementaPomodoroTarefa(String email, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - incrementaPomodoroTarefa");
		Tarefa tarefa = detalhaTarefa(email, idTarefa);
		tarefa.incrementaPomodoro();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] TarefaApplicationService - incrementaPomodoroTarefa");
	}

	@Override
	public void mudaStatusParaConcluida(String usuario, UUID idTarefa) {
		log.info("[inicia] PessoaApplicationService - mudaStatusParaConcluida");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefa.mudaStatusParaConcluida();
		tarefaRepository.salva(tarefa);
		log.info("[finaliza] PessoaApplicationService - mudaStatusParaConcluida");
	}

	public List<TarefaListResponse> buscaTodasTarefas(String usuario, UUID idUsuario) {
		log.info("[inicia] TarefaApplicationService - buscaTodasTarefas");
		verificaUsuario(usuario, idUsuario);
		List<Tarefa> tarefas = tarefaRepository.buscaTodasTarefas(idUsuario);
		log.info("[finaliza] TarefaApplicationService - buscaTodasTarefas");
		return TarefaListResponse.converte(tarefas);
	}

	public void verificaUsuario(String usuario, UUID idUsuario) {
		Usuario usuarioVerificado = usuarioRepository.buscaUsuarioPorEmail(usuario);
		usuarioRepository.buscaUsuarioPorId(idUsuario);
		usuarioVerificado.validaUsuario(idUsuario);
	}

	@Override
	public void deletaTarefa(String usuario, UUID idTarefa) {
		log.info("[inicia] TarefaApplicationService - deletaTarefa");
		Tarefa tarefa = detalhaTarefa(usuario, idTarefa);
		tarefaRepository.deletaTarefa(tarefa);
		log.info("[finaliza] TarefaApplicationService - deletaTarefa");
	}
}
