package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import com.autoservice.application.ordemservico.orcamento.OrcamentoEmailSender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class NotificacaoStatusClienteIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrcamentoEmailSender orcamentoEmailSender;

    @BeforeEach
    void limparNotificacoes() throws Exception {
        mockMvc.perform(delete("/integracoes/notificacoes/simuladas")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Mudanças de status geram notificações simuladas para o cliente")
    void notificaClienteEmCadaMudancaDeStatus() throws Exception {
        final String osId = criarOrdemRecebida();

        mockMvc.perform(get("/integracoes/notificacoes/simuladas")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ordemServicoId").value(osId))
                .andExpect(jsonPath("$[0].statusNovo").value("RECEBIDO"));

        mockMvc.perform(patch("/ordens-servico/" + osId + "/diagnostico")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/integracoes/notificacoes/simuladas")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusNovo").value("EM_DIAGNOSTICO"));
    }

    private String criarOrdemRecebida() throws Exception {
        final String placa = String.format("ZZZ%dK%02d",
                ThreadLocalRandom.current().nextInt(0, 10),
                ThreadLocalRandom.current().nextInt(10, 99));

        final var atendimentoBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "Cliente Notificação",
                  "cpf": "52998224725",
                  "email": "notificacao@test.local",
                  "telefone": "11987654321",
                  "placa": "%s",
                  "marca": "VW",
                  "modelo": "Gol",
                  "ano": 2018,
                  "cor": "Prata",
                  "kilometragem": 42000,
                  "relato": "Teste notificação de status"
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

        final JsonNode root = objectMapper.readTree(json);
        return root.get("ordemServicoId").asText();
    }
}
