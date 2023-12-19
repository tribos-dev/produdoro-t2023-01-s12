package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EditaTarefaRequest {
	@NotBlank
	@Size(message = "Campo descrição tarefa não pode estar vazio", max = 255, min = 3)
	private String descricao;
}
