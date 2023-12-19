package dev.wakandaacademy.produdoro.tarefa.application.service;

import java.util.List;
import java.util.UUID;

import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

public interface TarefaService {
	TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);

	Tarefa detalhaTarefa(String usuario, UUID idTarefa);

	List<TarefaListResponse> buscaTodasTarefas(String usuario, UUID idUsuario);

	void editaTarefa(String usuario, UUID idTarefa, EditaTarefaRequest editaTarefaRequest);
}
