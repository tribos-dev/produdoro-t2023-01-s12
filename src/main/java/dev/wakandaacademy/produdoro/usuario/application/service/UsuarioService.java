package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;

import java.util.UUID;

public interface UsuarioService {
	UsuarioCriadoResponse criaNovoUsuario(UsuarioNovoRequest usuarioNovo);

	void mudaStatusPausaCurta(String usuarioEmail, UUID idUsuario);

	UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario);

	void alteraStatusParaPausaLonga(String email, UUID idUsuario);

	void alteraStatusParaFoco(String usuario, UUID idUsuario);
}
