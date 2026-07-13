package com.autoservice.presentation.controller;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueOutput;
import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueUseCase;
import com.autoservice.application.estoque.query.EstoqueOutput;
import com.autoservice.application.estoque.query.EstoqueQuery;
import com.autoservice.presentation.dto.estoque.AtualizarLocalizacaoEstoqueRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EstoqueController.class)
@DisplayName("EstoqueController")
class EstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstoqueQuery estoqueQuery;

    @MockitoBean
    private AtualizarLocalizacaoEstoqueUseCase atualizarLocalizacaoEstoqueUseCase;

    @Test
    @DisplayName("GET /estoques deve listar estoque com paginação padrão")
    void deveListarEstoqueComPaginacaoPadrao() throws Exception {
        final var peca1 = new EstoqueOutput.PecaOutput(
                "peca-1", "P001", "Filtro de óleo", "Bosch"
        );
        final var estoque1 = new EstoqueOutput(
                "estoque-1", 50, 10, "Prateleira A1", peca1
        );

        final var peca2 = new EstoqueOutput.PecaOutput(
                "peca-2", "P002", "Vela de ignição", "NGK"
        );
        final var estoque2 = new EstoqueOutput(
                "estoque-2", 100, 20, "Prateleira B2", peca2
        );

        final var paginationOutput = new PaginationOutput<>(
                List.of(estoque1, estoque2), 0, 20, 2, 1
        );

        when(estoqueQuery.listar(0, 20)).thenReturn(paginationOutput);

        mockMvc.perform(get("/estoques")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id").value("estoque-1"))
                .andExpect(jsonPath("$.items[0].quantidadeDisponivel").value(50))
                .andExpect(jsonPath("$.items[0].peca.codigo").value("P001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));

        verify(estoqueQuery).listar(0, 20);
    }

    @Test
    @DisplayName("GET /estoques com paginação customizada deve retornar resultado")
    void deveListarEstoqueComPaginacaoCustomizada() throws Exception {
        final var peca = new EstoqueOutput.PecaOutput(
                "peca-3", "P003", "Corrente de distribuição", "Dayco"
        );
        final var estoque = new EstoqueOutput(
                "estoque-3", 30, 5, "Prateleira C1", peca
        );

        final var paginationOutput = new PaginationOutput<>(
                List.of(estoque), 1, 10, 15, 2
        );

        when(estoqueQuery.listar(1, 10)).thenReturn(paginationOutput);

        mockMvc.perform(get("/estoques")
                .param("page", "1")
                .param("size", "10")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(2));

        verify(estoqueQuery).listar(1, 10);
    }

    @Test
    @DisplayName("GET /estoques/{id} deve detalhar estoque")
    void deveDetalharEstoque() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final var peca = new EstoqueOutput.PecaOutput(
                "peca-4", "P004", "Bateria 60Ah", "Moura"
        );
        final var estoqueOutput = new EstoqueOutput(
                "estoque-4", 15, 3, "Prateleira D2", peca
        );

        when(estoqueQuery.detalhar(estoqueId)).thenReturn(estoqueOutput);

        mockMvc.perform(get("/estoques/{id}", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("estoque-4"))
                .andExpect(jsonPath("$.quantidadeDisponivel").value(15))
                .andExpect(jsonPath("$.quantidadeMinima").value(3))
                .andExpect(jsonPath("$.localizacao").value("Prateleira D2"))
                .andExpect(jsonPath("$.peca.descricao").value("Bateria 60Ah"));

        verify(estoqueQuery).detalhar(estoqueId);
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao deve atualizar localização")
    void deveAtualizarLocalizacao() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        final var atualizarOutput = new AtualizarLocalizacaoEstoqueOutput(
                "estoque-4", 15, 3, "Prateleira E3"
        );

        when(atualizarLocalizacaoEstoqueUseCase.execute(any()))
                .thenReturn(atualizarOutput);

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "localizacao": "Prateleira E3"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("estoque-4"))
                .andExpect(jsonPath("$.localizacao").value("Prateleira E3"));

        verify(atualizarLocalizacaoEstoqueUseCase).execute(any());
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao sem localizacao deve retornar 400")
    void deveRetornarBadRequestSemLocalizacao() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "localizacao": ""
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao com localizacao muito longa deve retornar 400")
    void deveRetornarBadRequestComLocalizacaoMuitoLonga() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final var localizacaoLonga = "A".repeat(121); // Máximo é 120

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "localizacao": "%s"
                        }
                        """.formatted(localizacaoLonga)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /estoques sem autenticação deve retornar 401")
    void deveRetornarUnauthorizedSemAutenticacao() throws Exception {
        mockMvc.perform(get("/estoques"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /estoques/{id} sem autenticação deve retornar 401")
    void deveDetalharUnauthorized() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(get("/estoques/{id}", estoqueId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao sem autenticação deve retornar 401")
    void deveAtualizarLocalizacaoUnauthorized() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "localizacao": "Prateleira E3"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao com JSON inválido deve retornar 400")
    void deveRetornarBadRequestComJsonInvalido() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ json inválido }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /estoques com múltiplas páginas deve navegar corretamente")
    void deveNavigarEmMultiplasPaginas() throws Exception {
        final var peca = new EstoqueOutput.PecaOutput(
                "peca-5", "P005", "Óleo do motor", "Mobil"
        );
        final var estoque = new EstoqueOutput(
                "estoque-5", 200, 50, "Prateleira F1", peca
        );

        final var paginationOutput = new PaginationOutput<>(
                List.of(estoque), 2, 5, 20, 4
        );

        when(estoqueQuery.listar(2, 5)).thenReturn(paginationOutput);

        mockMvc.perform(get("/estoques")
                .param("page", "2")
                .param("size", "5")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalPages").value(4));

        verify(estoqueQuery).listar(2, 5);
    }

    @Test
    @DisplayName("GET /estoques deve retornar detalhes completos com peca")
    void deveRetornarEstoqueComPecaCompleta() throws Exception {
        final var peca = new EstoqueOutput.PecaOutput(
                "peca-6", "P006", "Cabo de vela", "Magneti Marelli"
        );
        final var estoque = new EstoqueOutput(
                "estoque-6", 75, 15, "Prateleira G1", peca
        );

        final var paginationOutput = new PaginationOutput<>(
                List.of(estoque), 0, 20, 1, 1
        );

        when(estoqueQuery.listar(0, 20)).thenReturn(paginationOutput);

        mockMvc.perform(get("/estoques")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].peca.id").value("peca-6"))
                .andExpect(jsonPath("$.items[0].peca.codigo").value("P006"))
                .andExpect(jsonPath("$.items[0].peca.descricao").value("Cabo de vela"))
                .andExpect(jsonPath("$.items[0].peca.marca").value("Magneti Marelli"));
    }

    @Test
    @DisplayName("PATCH /estoques/{id}/localizacao deve retornar 200 OK")
    void deveRetornarOkAoAtualizarLocalizacao() throws Exception {
        final var estoqueId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        final var atualizarOutput = new AtualizarLocalizacaoEstoqueOutput(
                "estoque-7", 80, 20, "Prateleira H2"
        );

        when(atualizarLocalizacaoEstoqueUseCase.execute(any()))
                .thenReturn(atualizarOutput);

        mockMvc.perform(patch("/estoques/{id}/localizacao", estoqueId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "localizacao": "Prateleira H2"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeDisponivel").value(80))
                .andExpect(jsonPath("$.quantidadeMinima").value(20));
    }

    @Test
    @DisplayName("GET /estoques com lista vazia deve retornar array vazio")
    void deveRetornarListaVazia() throws Exception {
        final var paginationOutput = new PaginationOutput<EstoqueOutput>(
                List.of(), 0, 20, 0, 0
        );

        when(estoqueQuery.listar(anyInt(), anyInt())).thenReturn(paginationOutput);

        mockMvc.perform(get("/estoques")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(0)));
    }
}
