package com.autoservice.presentation.controller;

import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.AbrirAtendimentoOutput;
import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AtendimentoController.class)
@DisplayName("AtendimentoController")
class AtendimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AbrirAtendimentoUseCase abrirAtendimentoUseCase;

    @Test
    @DisplayName("POST /atendimentos com pessoa física deve retornar 201 CREATED com dados corretos")
    void deveAbrirAtendimentoComPessoaFisica() throws Exception {
        final var output = new AbrirAtendimentoOutput(
                "cliente-123",
                "veiculo-456",
                "ordem-789",
                OrdemServicoStatus.RECEBIDO.name()
        );

        when(abrirAtendimentoUseCase.execute(any(AbrirAtendimentoCommand.class)))
                .thenReturn(output);

        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("cliente-123"))
                .andExpect(jsonPath("$.veiculoId").value("veiculo-456"))
                .andExpect(jsonPath("$.ordemServicoId").value("ordem-789"))
                .andExpect(jsonPath("$.status").value(OrdemServicoStatus.RECEBIDO.name()));

        verify(abrirAtendimentoUseCase).execute(any(AbrirAtendimentoCommand.class));
    }

    @Test
    @DisplayName("POST /atendimentos com pessoa jurídica deve retornar 201 CREATED")
    void deveAbrirAtendimentoComPessoaJuridica() throws Exception {
        final var output = new AbrirAtendimentoOutput(
                "cliente-999",
                "veiculo-888",
                "ordem-777",
                OrdemServicoStatus.RECEBIDO.name()
        );

        when(abrirAtendimentoUseCase.execute(any(AbrirAtendimentoCommand.class)))
                .thenReturn(output);

        final var requestBody = """
                {
                  "tipoPessoa": "JURIDICA",
                  "razaoSocial": "Empresa Teste Ltda",
                  "cnpj": "11222333000181",
                  "representanteNome": "Maria Silva",
                  "representanteCpf": "12345678901",
                  "representanteEmail": "maria@empresa.com",
                  "representanteTelefone": "11987654321",
                  "email": "contato@empresa.com",
                  "telefone": "11912345678",
                  "placa": "XYZ9K99",
                  "marca": "Volkswagen",
                  "modelo": "Gol",
                  "ano": 2022,
                  "cor": "Branco",
                  "kilometragem": 25000,
                  "relato": "Partida lenta",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId", notNullValue()))
                .andExpect(jsonPath("$.veiculoId", notNullValue()))
                .andExpect(jsonPath("$.ordemServicoId", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()));

        verify(abrirAtendimentoUseCase).execute(any(AbrirAtendimentoCommand.class));
    }

    @Test
    @DisplayName("POST /atendimentos com itens deve retornar 201 CREATED com status EM_DIAGNOSTICO")
    void deveAbrirAtendimentoComItens() throws Exception {
        final var output = new AbrirAtendimentoOutput(
                "cliente-111",
                "veiculo-222",
                "ordem-333",
                OrdemServicoStatus.EM_DIAGNOSTICO.name()
        );

        when(abrirAtendimentoUseCase.execute(any(AbrirAtendimentoCommand.class)))
                .thenReturn(output);

        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "Pedro Costa",
                  "cpf": "98765432100",
                  "email": "pedro@email.com",
                  "telefone": "11988888888",
                  "placa": "DEF2K45",
                  "marca": "Honda",
                  "modelo": "Civic",
                  "ano": 2021,
                  "cor": "Cinza",
                  "kilometragem": 45000,
                  "relato": "Falha na injeção",
                  "itens": [
                    {
                      "tipo": "SERVICO",
                      "descricao": "Diagnóstico completo",
                      "quantidade": 1,
                      "valorUnitario": 150.00
                    },
                    {
                      "tipo": "SERVICO",
                      "descricao": "Alinhamento",
                      "quantidade": 1,
                      "valorUnitario": 200.00
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(OrdemServicoStatus.EM_DIAGNOSTICO.name()));

        verify(abrirAtendimentoUseCase).execute(any(AbrirAtendimentoCommand.class));
    }

    @Test
    @DisplayName("POST /atendimentos sem tipoPessoa deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestSemTipoPessoa() throws Exception {
        final var requestBody = """
                {
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos sem email deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestSemEmail() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos sem telefone deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestSemTelefone() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos sem placa deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestSemPlaca() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos com ano inválido deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestComAnoMenor1900() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 1899,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos com kilometragem negativa deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestComKilometragemNegativa() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": -1,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /atendimentos com resposta nula deve retornar 201 CREATED")
    void deveRetornarRespostaCompleta() throws Exception {
        final var output = new AbrirAtendimentoOutput(
                "id-cliente",
                "id-veiculo",
                "id-ordem",
                "RECEBIDO"
        );

        when(abrirAtendimentoUseCase.execute(any(AbrirAtendimentoCommand.class)))
                .thenReturn(output);

        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "Ana Silva",
                  "cpf": "11111111111",
                  "email": "ana@email.com",
                  "telefone": "11999999999",
                  "placa": "GHI5J67",
                  "marca": "Hyundai",
                  "modelo": "HB20",
                  "ano": 2020,
                  "cor": "Vermelho",
                  "kilometragem": 55000,
                  "relato": "Falha na transmissão",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value("id-cliente"))
                .andExpect(jsonPath("$.veiculoId").value("id-veiculo"))
                .andExpect(jsonPath("$.ordemServicoId").value("id-ordem"))
                .andExpect(jsonPath("$.status").value("RECEBIDO"));
    }

    @Test
    @DisplayName("POST /atendimentos sem autenticação deve retornar 403 FORBIDDEN")
    void deveRetornarForbiddenSemAutenticacao() throws Exception {
        final var requestBody = """
                {
                  "tipoPessoa": "FISICA",
                  "nome": "João Silva",
                  "cpf": "52998224725",
                  "email": "joao@email.com",
                  "telefone": "11999999999",
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2023,
                  "cor": "Preto",
                  "kilometragem": 10000,
                  "relato": "Barulho ao frear",
                  "itens": []
                }
                """;

        mockMvc.perform(post("/atendimentos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /atendimentos com JSON inválido deve retornar 400 BAD REQUEST")
    void deveRetornarBadRequestComJsonInvalido() throws Exception {
        mockMvc.perform(post("/atendimentos")
                        .with(user("admin@autoservice.local").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ json inválido }"))
                .andExpect(status().isBadRequest());
    }
}
