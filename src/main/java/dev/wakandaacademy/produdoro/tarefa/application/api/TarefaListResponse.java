package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import lombok.Getter;

@Getter
public class TarefaListResponse {

	private UUID idTarefa;
	private String descricao;

	public static List<TarefaListResponse> converte(List<Tarefa> tarefas) {
		return tarefas.stream().map(TarefaListResponse::new).collect(Collectors.toList());
	}

	public TarefaListResponse(Tarefa tarefa) {
		this.idTarefa = tarefa.getIdTarefa();
		this.descricao = tarefa.getDescricao();
	}

}
