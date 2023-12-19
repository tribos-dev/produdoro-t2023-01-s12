package dev.wakandaacademy.produdoro.tarefa.application.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EditaTarefaRequest {
    @NotNull
    @Size(message = "O campo não pode estar vazio", max = 255, min = 3)
    private String descricao;
}
