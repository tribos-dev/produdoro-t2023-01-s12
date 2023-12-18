package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

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
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
    }
}