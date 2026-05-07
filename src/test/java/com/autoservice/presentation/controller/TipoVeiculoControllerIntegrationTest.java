package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import com.autoservice.presentation.dto.tipoveiculo.CadastrarTipoVeiculoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class TipoVeiculoControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /tipos-veiculo persiste e retorna 201 com corpo esperado")
    void postTiposVeiculoCriaRecurso() throws Exception {
        final var body = """
                {"marca":"Fiat","modelo":"Uno","ano":2015}
                """;

        mockMvc.perform(post("/tipos-veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.marca", is("Fiat")))
                .andExpect(jsonPath("$.modelo", is("Uno")))
                .andExpect(jsonPath("$.ano", is(2015)));
    }

    @Test
    @DisplayName("POST /tipos-veiculo duas vezes com mesmo conjunto retorna mesmo registro (idempotente)")
    void postTiposVeiculoIdempotente() throws Exception {
        final var body = objectMapper.writeValueAsString(new CadastrarTipoVeiculoRequest("VW", "Gol", 2018));

        final String firstId = objectMapper.readTree(
                mockMvc.perform(post("/tipos-veiculo")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).get("id").asText();

        mockMvc.perform(post("/tipos-veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(firstId)));
    }

    @Test
    @DisplayName("POST /tipos-veiculo com payload inválido retorna 400")
    void postTiposVeiculoValidacao() throws Exception {
        final var body = """
                {"marca":"","modelo":"X","ano":1800}
                """;

        mockMvc.perform(post("/tipos-veiculo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }
}
