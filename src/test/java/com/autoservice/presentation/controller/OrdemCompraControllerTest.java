package com.autoservice.presentation.controller;

import com.autoservice.application.ordemcompra.query.OrdemCompraOutput;
import com.autoservice.application.ordemcompra.query.OrdemCompraQuery;
import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraOutput;
import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrdemCompraController.class)
@DisplayName("OrdemCompraController - Integration Tests")
class OrdemCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RealizarOrdemCompraUseCase realizarOrdemCompraUseCase;

    @MockitoBean
    private OrdemCompraQuery ordemCompraQuery;

    @Test
    @DisplayName("GET /ordens-compra deve listar todas as ordens")
    void deveListarTodasAsOrdens() throws Exception {
        final var item1 = new OrdemCompraOutput.ItemOutput(
                "item-1", "peca-1", "P001", "Óleo do motor", 5
        );
        final var item2 = new OrdemCompraOutput.ItemOutput(
                "item-2", "peca-2", "P002", "Filtro de ar", 3
        );
        final var ordem1 = new OrdemCompraOutput(
                "ordem-1", "PENDENTE", LocalDate.of(2026, 7, 6), List.of(item1, item2)
        );

        final var item3 = new OrdemCompraOutput.ItemOutput(
                "item-3", "peca-3", "P003", "Bateria", 1
        );
        final var ordem2 = new OrdemCompraOutput(
                "ordem-2", "REALIZADA", LocalDate.of(2026, 7, 5), List.of(item3)
        );

        when(ordemCompraQuery.listar()).thenReturn(List.of(ordem1, ordem2));

        mockMvc.perform(get("/ordens-compra")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("ordem-1"))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[0].dataCompra").value("2026-07-06"))
                .andExpect(jsonPath("$[0].itens", hasSize(2)))
                .andExpect(jsonPath("$[0].itens[0].codigo").value("P001"))
                .andExpect(jsonPath("$[1].id").value("ordem-2"))
                .andExpect(jsonPath("$[1].itens", hasSize(1)));

        verify(ordemCompraQuery).listar();
    }

    @Test
    @DisplayName("GET /ordens-compra com lista vazia")
    void deveRetornarListaVaziaQuandoNaoHaOrdens() throws Exception {
        when(ordemCompraQuery.listar()).thenReturn(List.of());

        mockMvc.perform(get("/ordens-compra")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(ordemCompraQuery).listar();
    }

    @Test
    @DisplayName("GET /ordens-compra/{id} deve detalhar ordem de compra")
    void deveDetalharOrdemCompra() throws Exception {
        final var ordemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        final var item1 = new OrdemCompraOutput.ItemOutput(
                "item-1", "peca-1", "P001", "Óleo do motor", 5
        );
        final var item2 = new OrdemCompraOutput.ItemOutput(
                "item-2", "peca-2", "P002", "Filtro de ar", 3
        );
        final var ordemDetail = new OrdemCompraOutput(
                ordemId.toString(), "PENDENTE", LocalDate.of(2026, 7, 6), List.of(item1, item2)
        );

        when(ordemCompraQuery.detalhar(ordemId)).thenReturn(ordemDetail);

        mockMvc.perform(get("/ordens-compra/{id}", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemId.toString()))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.dataCompra").value("2026-07-06"))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].id").value("item-1"))
                .andExpect(jsonPath("$.itens[0].pecaId").value("peca-1"))
                .andExpect(jsonPath("$.itens[0].codigo").value("P001"))
                .andExpect(jsonPath("$.itens[0].descricao").value("Óleo do motor"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(5))
                .andExpect(jsonPath("$.itens[1].id").value("item-2"))
                .andExpect(jsonPath("$.itens[1].quantidade").value(3));

        verify(ordemCompraQuery).detalhar(ordemId);
    }

    @Test
    @DisplayName("GET /ordens-compra/{id} com item único")
    void deveDetalharOrdemComUmItem() throws Exception {
        final var ordemId = UUID.fromString("223e4567-e89b-12d3-a456-426614174000");
        final var item = new OrdemCompraOutput.ItemOutput(
                "item-1", "peca-1", "P001", "Bateria 60Ah", 1
        );
        final var ordemDetail = new OrdemCompraOutput(
                ordemId.toString(), "REALIZADA", LocalDate.of(2026, 7, 5), List.of(item)
        );

        when(ordemCompraQuery.detalhar(ordemId)).thenReturn(ordemDetail);

        mockMvc.perform(get("/ordens-compra/{id}", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REALIZADA"))
                .andExpect(jsonPath("$.itens", hasSize(1)));

        verify(ordemCompraQuery).detalhar(ordemId);
    }

    @Test
    @DisplayName("PATCH /ordens-compra/{id}/realizar deve executar ordem de compra")
    void deveRealizarOrdemCompra() throws Exception {
        final var ordemId = UUID.fromString("323e4567-e89b-12d3-a456-426614174000");
        final var realizarOutput = new RealizarOrdemCompraOutput(
                ordemId.toString(), "REALIZADA", LocalDate.of(2026, 7, 6)
        );

        when(realizarOrdemCompraUseCase.execute(any())).thenReturn(realizarOutput);

        mockMvc.perform(patch("/ordens-compra/{id}/realizar", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordemId.toString()))
                .andExpect(jsonPath("$.status").value("REALIZADA"))
                .andExpect(jsonPath("$.dataCompra").value("2026-07-06"));

        verify(realizarOrdemCompraUseCase).execute(any());
    }

    @Test
    @DisplayName("PATCH /ordens-compra/{id}/realizar com diferentes datas")
    void deveRealizarOrdemComDataDiferente() throws Exception {
        final var ordemId = UUID.fromString("423e4567-e89b-12d3-a456-426614174000");
        final var dataAnterior = LocalDate.of(2026, 6, 15);
        final var realizarOutput = new RealizarOrdemCompraOutput(
                ordemId.toString(), "REALIZADA", dataAnterior
        );

        when(realizarOrdemCompraUseCase.execute(any())).thenReturn(realizarOutput);

        mockMvc.perform(patch("/ordens-compra/{id}/realizar", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataCompra").value("2026-06-15"));
    }

    @Test
    @DisplayName("GET /ordens-compra sem autenticação retorna 401")
    void deveRetornar401SemAutenticacao() throws Exception {
        mockMvc.perform(get("/ordens-compra"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /ordens-compra/{id}/realizar sem autenticação retorna 403 (CSRF)")
    void deveRetornar403SemAutenticacaoNaRealizacao() throws Exception {
        final var ordemId = UUID.randomUUID();

        mockMvc.perform(patch("/ordens-compra/{id}/realizar", ordemId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /ordens-compra/{id}/realizar sem token CSRF retorna 403")
    void deveRetornar403SemTokenCSRF() throws Exception {
        final var ordemId = UUID.randomUUID();

        mockMvc.perform(patch("/ordens-compra/{id}/realizar", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /ordens-compra/{id} com lista de itens grandes")
    void deveDetalharOrdemComMuitosItens() throws Exception {
        final var ordemId = UUID.fromString("523e4567-e89b-12d3-a456-426614174000");
        final var itens = List.of(
                new OrdemCompraOutput.ItemOutput("item-1", "peca-1", "P001", "Óleo", 10),
                new OrdemCompraOutput.ItemOutput("item-2", "peca-2", "P002", "Filtro", 5),
                new OrdemCompraOutput.ItemOutput("item-3", "peca-3", "P003", "Correia", 2),
                new OrdemCompraOutput.ItemOutput("item-4", "peca-4", "P004", "Vela", 8),
                new OrdemCompraOutput.ItemOutput("item-5", "peca-5", "P005", "Bateria", 1)
        );
        final var ordemDetail = new OrdemCompraOutput(
                ordemId.toString(), "PENDENTE", LocalDate.of(2026, 7, 6), itens
        );

        when(ordemCompraQuery.detalhar(ordemId)).thenReturn(ordemDetail);

        mockMvc.perform(get("/ordens-compra/{id}", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens", hasSize(5)))
                .andExpect(jsonPath("$.itens[4].codigo").value("P005"))
                .andExpect(jsonPath("$.itens[4].descricao").value("Bateria"));

        verify(ordemCompraQuery).detalhar(ordemId);
    }

    @Test
    @DisplayName("GET /ordens-compra com múltiplas ordens e diferentes status")
    void deveListarOrdensDiferentes() throws Exception {
        final var ordens = List.of(
                new OrdemCompraOutput("ordem-1", "PENDENTE", LocalDate.of(2026, 7, 6), 
                        List.of(new OrdemCompraOutput.ItemOutput("i1", "p1", "P001", "Desc1", 5))),
                new OrdemCompraOutput("ordem-2", "REALIZADA", LocalDate.of(2026, 7, 5),
                        List.of(new OrdemCompraOutput.ItemOutput("i2", "p2", "P002", "Desc2", 3))),
                new OrdemCompraOutput("ordem-3", "CANCELADA", LocalDate.of(2026, 7, 4),
                        List.of(new OrdemCompraOutput.ItemOutput("i3", "p3", "P003", "Desc3", 1)))
        );

        when(ordemCompraQuery.listar()).thenReturn(ordens);

        mockMvc.perform(get("/ordens-compra")
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].status").value("PENDENTE"))
                .andExpect(jsonPath("$[1].status").value("REALIZADA"))
                .andExpect(jsonPath("$[2].status").value("CANCELADA"));

        verify(ordemCompraQuery).listar();
    }

    @Test
    @DisplayName("GET /ordens-compra/{id} com valores de quantidade variados")
    void deveDetalharOrdenComQuantidadesVariadas() throws Exception {
        final var ordemId = UUID.fromString("623e4567-e89b-12d3-a456-426614174000");
        final var itens = List.of(
                new OrdemCompraOutput.ItemOutput("i1", "p1", "P001", "Item grande", 100),
                new OrdemCompraOutput.ItemOutput("i2", "p2", "P002", "Item pequeno", 1),
                new OrdemCompraOutput.ItemOutput("i3", "p3", "P003", "Item médio", 50)
        );
        final var ordemDetail = new OrdemCompraOutput(
                ordemId.toString(), "PENDENTE", LocalDate.of(2026, 7, 6), itens
        );

        when(ordemCompraQuery.detalhar(ordemId)).thenReturn(ordemDetail);

        mockMvc.perform(get("/ordens-compra/{id}", ordemId)
                .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].quantidade").value(100))
                .andExpect(jsonPath("$.itens[1].quantidade").value(1))
                .andExpect(jsonPath("$.itens[2].quantidade").value(50));

        verify(ordemCompraQuery).detalhar(ordemId);
    }
}
