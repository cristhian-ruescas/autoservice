package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class ServicoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /servicos persiste catálogo")
    void postServicosCriaRecurso() throws Exception {
        final var body = """
                {"nome":"Troca de óleo","descricao":"Filtro incluso","valorReferencia":280.50}
                """;

        mockMvc.perform(post("/servicos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Troca de óleo")))
                .andExpect(jsonPath("$.valorReferencia", is(280.5)));
    }

    @Test
    @DisplayName("GET /servicos lista catálogo")
    void getServicosLista() throws Exception {
        final String nome = "Revisão-" + System.nanoTime();
        mockMvc.perform(post("/servicos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"" + nome + "\",\"valorReferencia\":350}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/servicos").with(user("admin@autoservice.local").roles("ADMIN")).param("nome", nome))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", is(1)))
                .andExpect(jsonPath("$.items[0].nome", is(nome)));
    }
}
