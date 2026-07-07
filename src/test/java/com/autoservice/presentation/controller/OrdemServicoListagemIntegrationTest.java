package com.autoservice.presentation.controller;

import com.autoservice.AbstractIntegrationTest;
import com.autoservice.application.ordemservico.orcamento.OrcamentoEmailSender;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@EnabledIf("com.autoservice.support.TestEnvironment#dockerAvailable")
class OrdemServicoListagemIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrcamentoEmailSender orcamentoEmailSender;

    @Test
    @DisplayName("Listagem operacional prioriza status e exclui ordens encerradas")
    void listagemOperacional() throws Exception {
        final var osRecebido = abrirAtendimento("Cliente Recebido");
        final var osDiagnostico = abrirAtendimento("Cliente Diagnostico");
        final var osAguardando = abrirAtendimento("Cliente Aguardando");
        final var osExecucao = abrirAtendimento("Cliente Execucao");
        final var osFinalizada = abrirAtendimento("Cliente Finalizada");

        iniciarDiagnostico(osDiagnostico);
        avancarParaAguardandoAprovacao(osAguardando);
        avancarParaExecucao(osExecucao);
        avancarParaFinalizada(osFinalizada);

        final var idsMonitorados = Map.of(
                osExecucao, "EM_EXECUCAO",
                osAguardando, "AGUARDANDO_APROVACAO",
                osDiagnostico, "EM_DIAGNOSTICO",
                osRecebido, "RECEBIDO"
        );

        final String json = mockMvc.perform(get("/ordens-servico")
                        .param("size", "100")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final JsonNode items = objectMapper.readTree(json).get("items");
        final var ordensMonitoradas = new LinkedHashMap<String, String>();

        for (final JsonNode item : items) {
            final var id = item.get("ordemServicoId").asText();
            if (idsMonitorados.containsKey(id)) {
                ordensMonitoradas.put(id, item.get("status").asText());
            }
            assertNotEquals(osFinalizada, id, "Ordem finalizada não deve aparecer na listagem operacional");
        }

        assertEquals(List.of("EM_EXECUCAO", "AGUARDANDO_APROVACAO", "EM_DIAGNOSTICO", "RECEBIDO"), List.copyOf(ordensMonitoradas.values()));
        assertEquals(Set.copyOf(idsMonitorados.keySet()), ordensMonitoradas.keySet());
    }

    private String abrirAtendimento(final String nome) throws Exception {
        final var suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        final var cpf = gerarCpfValido(suffix);
        final var placa = String.format("ZZZ%dK%02d", suffix % 10, suffix % 100);

        final var body = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "%s",
                  "cpf": "%s",
                  "email": "listagem.%d@test.local",
                  "telefone": "11987654321",
                  "placa": "%s",
                  "marca": "VW",
                  "modelo": "Gol",
                  "ano": 2018,
                  "cor": "Prata",
                  "kilometragem": 42000,
                  "relato": "Teste listagem operacional"
                }
                """.formatted(nome, cpf, suffix, placa);

        final String json = mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ordemServicoId", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("ordemServicoId").asText();
    }

    private void iniciarDiagnostico(final String osId) throws Exception {
        mockMvc.perform(patch("/ordens-servico/" + osId + "/diagnostico")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    private void avancarParaAguardandoAprovacao(final String osId) throws Exception {
        iniciarDiagnostico(osId);

        mockMvc.perform(post("/ordens-servico/" + osId + "/itens")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "itens": [
                                    {
                                      "tipo": "SERVICO",
                                      "descricao": "Serviço listagem",
                                      "quantidade": 1,
                                      "valorUnitario": 150.00
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/ordens-servico/" + osId + "/diagnostico/finalizar")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tempoPrevistoExecucaoDias\":0,\"tempoPrevistoExecucaoHoras\":1}"))
                .andExpect(status().isOk());
    }

    private void avancarParaExecucao(final String osId) throws Exception {
        avancarParaAguardandoAprovacao(osId);

        mockMvc.perform(patch("/ordens-servico/" + osId + "/aprovacao/aprovar")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    private void avancarParaFinalizada(final String osId) throws Exception {
        avancarParaExecucao(osId);

        mockMvc.perform(patch("/ordens-servico/" + osId + "/finalizar")
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    private static String gerarCpfValido(final int seed) {
        final var base = new int[9];
        base[0] = 1 + (seed % 9);
        for (int i = 1; i < 9; i++) {
            base[i] = (seed + i * 7) % 10;
        }

        final var digitos = new java.util.ArrayList<Integer>(9);
        for (final int valor : base) {
            digitos.add(valor);
        }

        digitos.add(calcularDigitoVerificador(digitos, 10));
        digitos.add(calcularDigitoVerificador(digitos, 11));

        final var builder = new StringBuilder(11);
        for (final int valor : digitos) {
            builder.append(valor);
        }
        return builder.toString();
    }

    private static int calcularDigitoVerificador(final List<Integer> digitos, final int pesoInicial) {
        var soma = 0;
        for (int i = 0; i < digitos.size(); i++) {
            soma += digitos.get(i) * (pesoInicial - i);
        }
        final var resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
