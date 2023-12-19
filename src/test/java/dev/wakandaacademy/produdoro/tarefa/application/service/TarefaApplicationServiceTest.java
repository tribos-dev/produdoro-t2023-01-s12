package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.EditaTarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

	// @Autowired
	@InjectMocks
	TarefaApplicationService tarefaApplicationService;

	// @MockBean
	@Mock
	TarefaRepository tarefaRepository;

	@Mock
	UsuarioRepository usuarioRepository;

	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
	private String email = DataHelper.createUsuario().getEmail();

	@Test
	void deveRetornarIdTarefaNovaCriada() {
		TarefaRequest request = getTarefaRequest();
		when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

		TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

		assertNotNull(response);
		assertEquals(TarefaIdResponse.class, response.getClass());
		assertEquals(UUID.class, response.getIdTarefa().getClass());
	}

	public TarefaRequest getTarefaRequest() {
		TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
		return request;
	}

	@Test
	void testEditaTarefa() {
		// Dados de teste
		UUID idTarefa = UUID.randomUUID();
		String usuario = "usuarioTeste";
		EditaTarefaRequest editaTarefaRequest = new EditaTarefaRequest("Hahaha");
		Tarefa tarefaExistente = DataHelper.createTarefa();
		Usuario usuarioMock = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(eq(usuario))).thenReturn(usuarioMock);
		when(tarefaRepository.buscaTarefaPorId(eq(idTarefa))).thenReturn(Optional.of(tarefaExistente));

		tarefaApplicationService.editaTarefa(usuario, idTarefa, editaTarefaRequest);

		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(eq(usuario));
		verify(tarefaRepository, times(1)).buscaTarefaPorId(eq(idTarefa));
		verify(tarefaRepository, times(1)).salva(any(Tarefa.class));
	}

	@Test
	void testEditaTarefa_TarefaNaoEncontrada() {
		UUID idTarefa = UUID.randomUUID();
		String usuario = "usuarioTeste";
		EditaTarefaRequest editaTarefaRequest = new EditaTarefaRequest("Eita");

		when(tarefaRepository.buscaTarefaPorId(eq(idTarefa))).thenReturn(Optional.empty());

		assertThrows(APIException.class,
				() -> tarefaApplicationService.editaTarefa(usuario, idTarefa, editaTarefaRequest));

		verify(tarefaRepository, times(1)).buscaTarefaPorId(eq(idTarefa));
		verifyNoMoreInteractions(tarefaRepository);
	}

	@Test
	void deveIncrementarPomodoroComSucesso() {
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.incrementaPomodoroTarefa(usuario.getEmail(), tarefa.getIdTarefa());

		verify(tarefaRepository, times(1)).salva(tarefa);
		assertEquals(2, tarefa.getContagemPomodoro());
	}

	@Test
	void deveRetornarExceptionAoIncrementaPomodoroAUmaTarefaNaoPertencenteAoUsuario() {
		Tarefa tarefa = DataHelper.createTarefa();
		Usuario usuarioInvalido = DataHelper.usuarioInvalido();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuarioInvalido);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
		APIException ex = assertThrows(APIException.class, () -> tarefaApplicationService
				.incrementaPomodoroTarefa(usuarioInvalido.getEmail(), tarefa.getIdTarefa()));

		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
		assertEquals("Usuário não é dono da Tarefa solicitada!", ex.getMessage());
	}

	@Test
	void mudaStatusParaConcluida_PositiveScenario() {
		// Arrange
		UUID usuarioId = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
		UUID tarefaId = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");

		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

		// Act
		tarefaApplicationService.mudaStatusParaConcluida("email@email.com", tarefaId);

		// Assert
		assertEquals(StatusTarefa.CONCLUIDA, tarefa.getStatus());
		verify(tarefaRepository, times(1)).salva(tarefa);
	}

	@Test
	void mudaStatusParaConcluida_NegativeScenario_TarefaNaoEncontrada() {
		// Arrange
		UUID usuarioId = UUID.fromString("a713162f-20a9-4db9-a85b-90cd51ab18f4");
		UUID tarefaId = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");

		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.empty());

		// Act and Assert
		assertThrows(APIException.class, () -> {
			tarefaApplicationService.mudaStatusParaConcluida("email@email.com", tarefaId);
		});

		// Verify
		verify(tarefaRepository, never()).salva(any());
	}

	void testBuscaTodasTarefas_QuandoRepositorioRetornaTarefas() {

		when(tarefaRepository.buscaTodasTarefas(any(UUID.class))).thenReturn(DataHelper.createListTarefa());
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
		List<TarefaListResponse> response = tarefaApplicationService.buscaTodasTarefas(email, idUsuario);
		assertNotNull(response);
		assertTrue(response.size() > 0);
		assertFalse(response.isEmpty());
	}

	@Test
	void visualizaTodasTarefasFalha() {

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(DataHelper.createUsuario());
		APIException ex = assertThrows(APIException.class,
				() -> tarefaApplicationService.buscaTodasTarefas(email, UUID.randomUUID()));
		assertNotNull(ex);
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
		assertEquals("Credencial de autenticação não é válida", ex.getMessage());
	}

	@Test
	void deveDeletarTarefa() {
		// DADO
		Usuario usuario = DataHelper.createUsuario();
		Tarefa tarefa = DataHelper.createTarefa();

		// QUANDO
		when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
		when(tarefaRepository.buscaTarefaPorId(tarefa.getIdTarefa())).thenReturn(Optional.of(tarefa));
		tarefaApplicationService.deletaTarefa(usuario.getEmail(), tarefa.getIdTarefa());

		// ENTÃO
		verify(tarefaRepository, times(1)).deletaTarefa(tarefa);
	}
}
