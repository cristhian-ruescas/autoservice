package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.cliente.delete.RemoverClienteUseCase;
import com.autoservice.application.cliente.query.ClienteDetailOutput;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.cliente.query.GetClienteByIdQuery;
import com.autoservice.application.cliente.query.GetClienteByCpfQuery;
import com.autoservice.application.cliente.query.ListClientesQuery;
import com.autoservice.application.cliente.update.AtualizarClienteCommand;
import com.autoservice.application.cliente.update.AtualizarClienteUseCase;
import com.autoservice.presentation.dto.cliente.AtualizarClienteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClienteController.class)
@DisplayName("ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListClientesQuery listClientesQuery;

    @MockitoBean
    private GetClienteByIdQuery getClienteByIdQuery;

    @MockitoBean
    private GetClienteByCpfQuery getClienteByCpfQuery;

    @MockitoBean
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @MockitoBean
    private RemoverClienteUseCase removerClienteUseCase;

    @Test
    @DisplayName("GET /clientes deve listar clientes com paginação padrão")
    void deveListarClientesComPaginacaoPadrao() throws Exception {
        final var cliente1 = new ClienteOutput(
                "cliente-1", "FISICA", LocalDate.now(), "João Silva", null, null, null, null, null, null
        );
        final var cliente2 = new ClienteOutput(
                "cliente-2", "FISICA", LocalDate.now(), "Maria Santos", null, null, null, null, null, null
        );
        final var paginationOutput = new PaginationOutput<>(
                List.of(cliente1, cliente2), 0, 20, 2, 1
        );

        when(listClientesQuery.listar(0, 20, null)).thenReturn(paginationOutput);

        mockMvc.perform(get("/clientes").with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value("cliente-1"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(listClientesQuery).listar(0, 20, null);
    }

    @Test
    @DisplayName("GET /clientes com paginação customizada deve retornar resultado")
    void deveListarClientesComPaginacaoCustomizada() throws Exception {
        final var cliente = new ClienteOutput(
                "cliente-3", "FISICA", LocalDate.now(), "Pedro Costa", null, null, null, null, null, null
        );
        final var paginationOutput = new PaginationOutput<>(List.of(cliente), 1, 10, 1, 2);

        when(listClientesQuery.listar(1, 10, "FISICA")).thenReturn(paginationOutput);

        mockMvc.perform(get("/clientes")
                .param("page", "1")
                .param("size", "10")
                .param("tipoPessoa", "FISICA")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));

        verify(listClientesQuery).listar(1, 10, "FISICA");
    }

    @Test
    @DisplayName("GET /clientes/cpf/{cpf} deve retornar cliente por CPF")
    void deveBuscarClientePorCpf() throws Exception {
        final var clienteOutput = new ClienteOutput(
                "cliente-id", "FISICA", LocalDate.now(), "João Silva", "52998224725",
                null, null, "joao@email.com", "11999999999", null
        );

        when(getClienteByCpfQuery.buscarPorCpf("52998224725")).thenReturn(clienteOutput);

        mockMvc.perform(get("/clientes/cpf/52998224725")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cliente-id"))
                .andExpect(jsonPath("$.cpf").value("52998224725"));

        verify(getClienteByCpfQuery).buscarPorCpf("52998224725");
    }

    @Test
    @DisplayName("GET /clientes/{id} deve retornar detalhes do cliente")
    void deveBuscarClientePorId() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final var clienteDetailOutput = new ClienteDetailOutput(
                "cliente-id", "FISICA", LocalDate.now(), "Maria Santos", "98765432100",
                null, null, "maria@email.com", "11988888888", null, List.of()
        );

        when(getClienteByIdQuery.buscarPorId(clienteId)).thenReturn(clienteDetailOutput);

        mockMvc.perform(get("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("cliente-id"));

        verify(getClienteByIdQuery).buscarPorId(clienteId);
    }

    @Test
    @DisplayName("PUT /clientes/{id} deve atualizar cliente")
    void deveAtualizarCliente() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        final var clienteOutput = new ClienteOutput(
                "cliente-id", "FISICA", LocalDate.now(), "João Silva Atualizado", null,
                null, null, "joao.novo@email.com", "11999999988", null
        );

        when(atualizarClienteUseCase.execute(any(AtualizarClienteCommand.class)))
                .thenReturn(clienteOutput);

        mockMvc.perform(put("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "João Silva Atualizado",
                          "email": "joao.novo@email.com",
                          "telefone": "11999999988"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));

        verify(atualizarClienteUseCase).execute(any(AtualizarClienteCommand.class));
    }

    @Test
    @DisplayName("PUT /clientes/{id} com JSON inválido deve retornar 400")
    void deveRetornarBadRequestComJsonInvalido() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(put("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ json inválido }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /clientes/{id} deve remover cliente")
    void deveRemoverCliente() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        doNothing().when(removerClienteUseCase).execute(any());

        mockMvc.perform(delete("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(removerClienteUseCase).execute(any());
    }

    @Test
    @DisplayName("GET /clientes sem autenticação deve retornar 401")
    void deveRetornarUnauthorizedSemAutenticacao() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /clientes/cpf/{cpf} sem autenticação deve retornar 401")
    void deveBuscarPorCpfUnauthorized() throws Exception {
        mockMvc.perform(get("/clientes/cpf/52998224725"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /clientes/{id} sem autenticação deve retornar 401")
    void deveBuscarPorIdUnauthorized() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(get("/clientes/{id}", clienteId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /clientes/{id} sem autenticação deve retornar 401")
    void deveRetornarUnauthorizedDeleteSemAutenticacao() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(delete("/clientes/{id}", clienteId).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /clientes/{id} com representante deve atualizar corretamente")
    void deveAtualizarClienteComRepresentante() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        final var clienteOutput = new ClienteOutput(
                "cliente-id", "JURIDICA", LocalDate.now(), null, null,
                "Empresa XYZ", null, "contato@empresa.com", "1133334444", null
        );

        when(atualizarClienteUseCase.execute(any(AtualizarClienteCommand.class)))
                .thenReturn(clienteOutput);

        mockMvc.perform(put("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "razaoSocial": "Empresa XYZ",
                          "email": "contato@empresa.com",
                          "telefone": "1133334444"
                        }
                        """))
                .andExpect(status().isOk());

        verify(atualizarClienteUseCase).execute(any(AtualizarClienteCommand.class));
    }

    @Test
    @DisplayName("GET /clientes com múltiplas páginas deve navegar corretamente")
    void deveNavigarEmMultiplasPaginas() throws Exception {
        final var cliente = new ClienteOutput(
                "cliente-1", "FISICA", LocalDate.now(), "Cliente Página 2", null,
                null, null, null, null, null
        );
        final var paginationOutput = new PaginationOutput<>(List.of(cliente), 1, 5, 1, 3);

        when(listClientesQuery.listar(1, 5, null)).thenReturn(paginationOutput);

        mockMvc.perform(get("/clientes")
                .param("page", "1")
                .param("size", "5")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));

        verify(listClientesQuery).listar(1, 5, null);
    }

    @Test
    @DisplayName("PUT /clientes/{id} deve retornar 200 OK com dados atualizados")
    void deveRetornarDadosAtualizadosCorretamente() throws Exception {
        final var clienteId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        final var clienteOutput = new ClienteOutput(
                "id-retornado", "FISICA", LocalDate.now(), "Nome Atualizado", null,
                null, null, "email@novo.com", "11987654321", null
        );

        when(atualizarClienteUseCase.execute(any(AtualizarClienteCommand.class)))
                .thenReturn(clienteOutput);

        mockMvc.perform(put("/clientes/{id}", clienteId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nome": "Nome Atualizado",
                          "email": "email@novo.com",
                          "telefone": "11987654321"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id-retornado"));
    }
}
