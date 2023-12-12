package dev.wakandaacademy.produdoro.tarefa.application.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class EditaTarefaResponse {
    @NotBlank
    @Size(message = "Campo descrição tarefa não pode estar vazio", max = 255, min = 3)
    private String descricao;
}
