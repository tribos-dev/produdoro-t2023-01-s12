package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

	@GetMapping("/{idTarefa}")
	@ResponseStatus(code = HttpStatus.OK)
	TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

	@PatchMapping("/ativa-tarefa/{idTarefa}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void tarefaAtiva(@RequestHeader(name = "Authorization", required = true) String token, @RequestParam UUID idUsuario,
			@PathVariable UUID idTarefa);

	@PatchMapping("/editaTarefa/{idTarefa}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void editaTarefa(@RequestHeader(name = "Authorization", required = true) String token, @PathVariable UUID idTarefa,
			@Valid @RequestBody EditaTarefaRequest editaTarefaRequest);

	@PatchMapping(value = "/{idTarefa}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void incrementaPomodoroTarefa(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

	@PatchMapping(value = "/{idTarefa}/concluida")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void mudaStatusParaConcluida(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

	@DeleteMapping("/{idTarefa}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void deletaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idTarefa);

	@GetMapping(value = "/tarefas/{idUsuario}")
	@ResponseStatus(HttpStatus.OK)
	List<TarefaListResponse> getTarefasDoUsuario(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idUsuario);
}