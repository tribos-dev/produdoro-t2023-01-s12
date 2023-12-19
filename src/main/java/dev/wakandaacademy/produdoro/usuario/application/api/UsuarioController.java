package dev.wakandaacademy.produdoro.usuario.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import dev.wakandaacademy.produdoro.config.security.service.TokenService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Validated
@Log4j2
@RequiredArgsConstructor
public class UsuarioController implements UsuarioAPI {
	private final UsuarioService usuarioAppplicationService;
	private final TokenService tokenService;

	@Override
	public UsuarioCriadoResponse postNovoUsuario(@Valid UsuarioNovoRequest usuarioNovo) {
		log.info("[inicia] UsuarioController - postNovoUsuario");
		UsuarioCriadoResponse usuarioCriado = usuarioAppplicationService.criaNovoUsuario(usuarioNovo);
		log.info("[finaliza] UsuarioController - postNovoUsuario");
		return usuarioCriado;
	}

	@Override
	public UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario) {
		log.info("[inicia] UsuarioController - buscaUsuarioPorId");
		log.info("[idUsuario] {}", idUsuario);
		UsuarioCriadoResponse buscaUsuario = usuarioAppplicationService.buscaUsuarioPorId(idUsuario);
		log.info("[finaliza] UsuarioController - buscaUsuarioPorId");
		return buscaUsuario;
	}

	@Override
	public void mudaStatusParaPausaCurta(String token, UUID idUsuario) {
		log.info("[inicia]UsuarioController - mudaStatusParaPausaCurta");
		log.info("[idUsuario] {}", idUsuario);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.FORBIDDEN, "Token Inválido!"));
		usuarioAppplicationService.mudaStatusPausaCurta(usuario, idUsuario);
		log.info("[Finaliza]UsuarioController - mudaStatusParaPausaCurta");
	}

	public void alteraStatusUsuarioPausaLonga(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - alteraStatusUsuarioPausaLonga");
		String email = buscaEmailUsuarioPeloToken(token);
		usuarioAppplicationService.alteraStatusParaPausaLonga(email, idUsuario);
		log.info("[finaliza] UsuarioController - alteraStatusUsuarioPausaLonga");
	}

	private String buscaEmailUsuarioPeloToken(String token) {
		log.debug("[token] {}", token);
		String email = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.UNAUTHORIZED, "Usuário inválido"));
		log.info("[email] {}", email);
		return email;
	}

	@Override
	public void patchAlteraStatusParaFoco(String token, UUID idUsuario) {
		log.info("[inicia] UsuarioController - patchAlteraStatusDoUsuarioParaFoco");
		log.info("[idUsuario] {}", idUsuario);
		String usuario = tokenService.getUsuarioByBearerToken(token)
				.orElseThrow(() -> APIException.build(HttpStatus.FORBIDDEN, "Token inválido."));
		usuarioAppplicationService.alteraStatusParaFoco(usuario, idUsuario);
		log.info("[finaliza] UsuarioController - patchAlteraStatusDoUsuarioParaFoco");
	}

}
