package dev.wakandaacademy.produdoro.tarefa.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

	void setUp() {
		MockitoAnnotations.openMocks(this);
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
		assertEquals("A credencial não é válida.", ex.getMessage());
	}
}
