package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.service.TarefaService;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TarefaRestController implements TarefaAPI {
	private final TarefaService tarefaService;
	private final TokenService tokenService;

	public TarefaIdResponse postNovaTarefa(TarefaRequest tarefaRequest) {
		log.info("[inicia]  TarefaRestController - postNovaTarefa  ");
		TarefaIdResponse tarefaCriada = tarefaService.criaNovaTarefa(tarefaRequest);
		log.info("[finaliza]  TarefaRestController - postNovaTarefa");
		return tarefaCriada;
	}

	@Override
	public TarefaDetalhadoResponse detalhaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - detalhaTarefa");
		String usuario = getUsuarioByToken(token);
		Tarefa tarefa = tarefaService.detalhaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - detalhaTarefa");
		return new TarefaDetalhadoResponse(tarefa);
	}

	@Override
	public void incrementaPomodoroTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - incrementaPomodoroTarefa");
		String email = getUsuarioByToken(token);
		tarefaService.incrementaPomodoroTarefa(email, idTarefa);
		log.info("[finaliza] TarefaRestController - incrementaPomodoroTarefa");
	}

	private String getUsuarioByToken(String token) {
		log.debug("[token] {}", token);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, token));
		log.info("[usuario] {}", usuario);
		return usuario;
	}

	@Override
	public void tarefaAtiva(String token, UUID idUsuario, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - tarefaAtiva");
		String usuarioToken = getUsuarioByToken(token);
		tarefaService.ativaTarefa(usuarioToken, idUsuario, idTarefa);
		log.info("[finaliza] TarefaRestController - tarefaAtiva");

	}

	public void editaTarefa(String token, UUID idTarefa, EditaTarefaRequest editaTarefaRequest) {
		log.info("[inicia] TarefaRestController - editaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.editaTarefa(usuario, idTarefa, editaTarefaRequest);
		log.info("[finaliza] TarefaRestController - editaTarefa");
	}

	@Override
	public void mudaStatusParaConcluida(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - mudaStatusParaConcluida");
		log.info("[idTarefa {}", idTarefa);
		String usuario = getUsuarioByToken(token);
		tarefaService.mudaStatusParaConcluida(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - mudaStatusParaConcluida");
	}

	public void deletaTarefa(String token, UUID idTarefa) {
		log.info("[inicia] TarefaRestController - deletaTarefa");
		String usuario = getUsuarioByToken(token);
		tarefaService.deletaTarefa(usuario, idTarefa);
		log.info("[finaliza] TarefaRestController - deletaTarefa");
	}

	public List<TarefaListResponse> getTarefasDoUsuario(String token, UUID idUsuario) {
		log.info("[inicia] TarefaRestController - getTarefasDoUsuario");
		String usuario = getUsuarioByToken(token);
		List<TarefaListResponse> tarefas = tarefaService.buscaTodasTarefas(usuario, idUsuario);
		log.info("[finaliza] TarefaRestController - getTarefasDoUsuario");
		return tarefas;
	}
}
