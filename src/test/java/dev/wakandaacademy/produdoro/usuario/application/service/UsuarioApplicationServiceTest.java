package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void deveAlterarStatusParaPausaLongaComSucesso(){
        Usuario usuario = DataHelper.createUsuarioComPausaCurta();
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        usuarioApplicationService.alteraStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
    }

    @Test
    public void deveRetornarExceptionAoMudarStatusParaPausaLongaComIdInvalido(){
        UUID idInvalido = UUID.randomUUID();
        Usuario usuario = DataHelper.createUsuarioComPausaCurta();
        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class, () -> usuarioApplicationService
                .alteraStatusParaPausaLonga(usuario.getEmail(), idInvalido));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("credencial de autenticação não é valida", ex.getMessage());
    }

    @Test
    void deveAlterarStatusParaFoco() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        usuarioApplicationService.alteraStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(any());
    }

    @Test
    void statusParaFocoFalha() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        APIException exception = assertThrows(APIException.class,
                () -> usuarioApplicationService.alteraStatusParaFoco("mathias@gmail,com", UUID.randomUUID()));
        assertEquals("Credencial de autenticação não é válida", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
    }

    @Test
    void statusJaEstaEmFoco() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        usuarioApplicationService.alteraStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        APIException exception = assertThrows(APIException.class,
                () -> usuarioApplicationService.alteraStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario()));
        assertEquals("Usuário já está em FOCO", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusException());
    }
}