package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class OrdemServicoMetricasIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /ordens-servico/metricas/tempo-medio-execucao retorna estrutura")
    void tempoMedioExecucacaoSemDados() throws Exception {
        mockMvc.perform(get("/ordens-servico/metricas/tempo-medio-execucao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porDescricaoItemServico").isArray());
    }
}
