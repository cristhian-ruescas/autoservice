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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo principal: atendimento → diagnóstico → itens → orçamento → aprovação → finalização → métrica.
 * O envio de e-mail do orçamento é substituído por mock para não depender de SMTP.
 */
@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class OrdemServicoFluxoPrincipalIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrcamentoEmailSender orcamentoEmailSender;
  
    private static String placaMercosulAleatoria() {
        final int d = ThreadLocalRandom.current().nextInt(0, 10);
        final int dd = ThreadLocalRandom.current().nextInt(10, 99);
        return String.format("ZZZ%dK%02d", d, dd);
    }

    @Test
    @DisplayName("Fluxo feliz completo até métrica de tempo médio")
    void fluxoCompleto() throws Exception {
        final String placa = placaMercosulAleatoria();

        final var atendimentoBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "Maria Integração",
                  "cpf": "52998224725",
                  "email": "maria.integration@test.local",
                  "telefone": "11987654321",
                  "placa": "%s",
                  "marca": "VW",
                  "modelo": "Gol",
                  "ano": 2018,
                  "cor": "Prata",
                  "kilometragem": 42000,
                  "relato": "Revisão geral"
                }
                """.formatted(placa);

        final String json = mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atendimentoBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordemServicoId", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final JsonNode root = this.objectMapper.readTree(json);
        final String osId = root.get("ordemServicoId").asText();

        mockMvc.perform(patch("/ordens-servico/" + osId + "/diagnostico")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());

        final var itensBody = """
                {
                  "itens": [
                    {
                      "tipo": "SERVICO",
                      "descricao": "Troca de óleo integração",
                      "quantidade": 1,
                      "valorUnitario": 199.90
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/ordens-servico/" + osId + "/itens")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itensBody))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/ordens-servico/" + osId + "/diagnostico/finalizar")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tempoPrevistoExecucaoDias\":0,\"tempoPrevistoExecucaoHoras\":2}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/ordens-servico/" + osId + "/aprovacao/aprovar")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/ordens-servico/" + osId + "/finalizar")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tempoMedioGlobalSegundos").exists())
                .andExpect(jsonPath("$.porDescricaoItemServico[0].descricaoItemServico").value("Troca de óleo integração"));
    }
}
