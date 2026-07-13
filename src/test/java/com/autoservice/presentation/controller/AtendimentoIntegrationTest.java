package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class AtendimentoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String placaMercosulAleatoria() {
        final int d = ThreadLocalRandom.current().nextInt(0, 10);
        final int dd = ThreadLocalRandom.current().nextInt(10, 99);
        return String.format("ATD%dK%02d", d, dd);
    }

    @Test
    @DisplayName("Abertura completa com cliente, veículo, serviços e peças em uma chamada")
    void deveAbrirAtendimentoComItens() throws Exception {
        final String placa = placaMercosulAleatoria();

        final var pecaBody = """
                {
                  "descricao": "Filtro de óleo integração",
                  "codigo": "FILTRO-001",
                  "marca": "Mann",
                  "valorUnitario": 45.90
                }
                """;

        final String pecaJson = mockMvc.perform(post("/pecas")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pecaBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final JsonNode pecaNode = this.objectMapper.readTree(pecaJson);
        final String pecaId = pecaNode.get("id").asText();

        final var atendimentoBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "Carlos Integração",
                  "cpf": "52998224725",
                  "email": "carlos.integration@test.local",
                  "telefone": "11912345678",
                  "placa": "%s",
                  "marca": "Fiat",
                  "modelo": "Uno",
                  "ano": 2015,
                  "cor": "Branco",
                  "kilometragem": 85000,
                  "relato": "Barulho no motor",
                  "itens": [
                    {
                      "tipo": "SERVICO",
                      "descricao": "Diagnóstico completo",
                      "quantidade": 1,
                      "valorUnitario": 120.00
                    },
                    {
                      "tipo": "PECA",
                      "pecaId": "%s",
                      "quantidade": 2,
                      "valorUnitario": 45.90
                    }
                  ]
                }
                """.formatted(placa, pecaId);

        final String atendimentoJson = mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atendimentoBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordemServicoId", notNullValue()))
                .andExpect(jsonPath("$.status").value("EM_DIAGNOSTICO"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final JsonNode atendimentoNode = this.objectMapper.readTree(atendimentoJson);
        final String osId = atendimentoNode.get("ordemServicoId").asText();

        mockMvc.perform(get("/ordens-servico/" + osId + "/itens")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
