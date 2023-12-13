package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    public void alterStatusParaFocoComSucesso(){
        Usuario usuario = DataHelper.createUsuarioComPausaCurta();

        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        usuarioApplicationService.alteraStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());

        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
    }

    @Test
    public void deveRetornExceptionAoMudarStatusParaPausaLongaComIdInvalido(){
        UUID idInvalido = UUID.randomUUID();
        Usuario usuario = DataHelper.createUsuarioComPausaCurta();

        when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class, () -> usuarioApplicationService
                .alteraStatusParaPausaLonga(usuario.getEmail(), idInvalido));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("credencial de autenticação não é valida", ex.getMessage());
    }
}