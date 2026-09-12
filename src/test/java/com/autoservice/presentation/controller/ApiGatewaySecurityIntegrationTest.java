package com.autoservice.presentation.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.autoservice.application.atendimento.create.AbrirAtendimentoOutput;
import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.application.cliente.delete.RemoverClienteUseCase;
import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.cliente.query.GetClienteByCpfQuery;
import com.autoservice.application.cliente.query.GetClienteByIdQuery;
import com.autoservice.application.cliente.query.ListClientesQuery;
import com.autoservice.application.cliente.update.AtualizarClienteUseCase;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoQuery;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoUseCase;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoUseCase;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.infrastructure.ordemservico.persistence.OrdemServicoRepository;
import com.autoservice.infrastructure.security.CpfAccessGuard;
import com.autoservice.infrastructure.security.JwtUtil;
import com.autoservice.infrastructure.security.UsuarioUserDetailsService;
import com.autoservice.infrastructure.security.WebSecurityConfig;
import com.autoservice.presentation.controller.ordemservico.OrdemServicoAprovacaoController;
import com.autoservice.presentation.controller.ordemservico.OrdemServicoConsultaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AtendimentoController.class, ClienteController.class,
        OrdemServicoConsultaController.class, OrdemServicoAprovacaoController.class})
@Import({WebSecurityConfig.class, CpfAccessGuard.class, ApiGatewaySecurityIntegrationTest.SecurityBeans.class})
@MockitoBean(types = {
        AbrirAtendimentoUseCase.class,
        ListClientesQuery.class,
        GetClienteByIdQuery.class,
        GetClienteByCpfQuery.class,
        AtualizarClienteUseCase.class,
        RemoverClienteUseCase.class,
        UsuarioUserDetailsService.class,
        OrdemServicoRepository.class,
        ListOrdemServicoQuery.class,
        DetailOrdemServicoQuery.class,
        AcompanharOrdemServicoQuery.class,
        AtualizarOrdemServicoUseCase.class,
        RemoverOrdemServicoUseCase.class,
        AprovarOrdemServicoUseCase.class,
        ReprovarOrdemServicoUseCase.class
})
@TestPropertySource(properties = {
        "autoservice.jwt.secret=segredo-local-com-tamanho-suficiente-para-testes",
        "autoservice.jwt.issuer=autoservice-auth"
})
class ApiGatewaySecurityIntegrationTest {

    private static final String JWT_SECRET = "segredo-local-com-tamanho-suficiente-para-testes";
    private static final UUID ORDEM_SERVICO_ID = UUID.fromString("cb528c25-97a1-43d7-ad94-30da9ec066a0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final AbrirAtendimentoUseCase abrirAtendimentoUseCase;
    private final GetClienteByCpfQuery getClienteByCpfQuery;
    private final OrdemServicoRepository ordemServicoRepository;
    private final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;

    @Autowired
    ApiGatewaySecurityIntegrationTest(
            final AbrirAtendimentoUseCase abrirAtendimentoUseCase,
            final GetClienteByCpfQuery getClienteByCpfQuery,
            final OrdemServicoRepository ordemServicoRepository,
            final AcompanharOrdemServicoQuery acompanharOrdemServicoQuery,
            final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase
    ) {
        this.abrirAtendimentoUseCase = abrirAtendimentoUseCase;
        this.getClienteByCpfQuery = getClienteByCpfQuery;
        this.ordemServicoRepository = ordemServicoRepository;
        this.acompanharOrdemServicoQuery = acompanharOrdemServicoQuery;
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.reprovarOrdemServicoUseCase = reprovarOrdemServicoUseCase;
    }

    @TestConfiguration
    static class SecurityBeans {
        @Bean
        JwtUtil jwtUtil() {
            return new JwtUtil(JWT_SECRET, "autoservice-auth");
        }
    }

    @Test
    void devePermitirAbrirAtendimentoComCpfDoMesmoToken() throws Exception {
        when(abrirAtendimentoUseCase.execute(any()))
                .thenReturn(new AbrirAtendimentoOutput("cliente-1", "veiculo-1", "os-1", "RECEBIDO"));

        mockMvc.perform(post("/atendimentos")
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705"))
                        .header("X-Correlation-Id", "corr-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarPayloadAtendimento("390.533.447-05"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-Correlation-Id", "corr-123"))
                .andExpect(jsonPath("$.ordemServicoId").value("os-1"));
    }

    @Test
    void deveBloquearAbrirAtendimentoQuandoCpfDoRequestNaoBateComToken() throws Exception {
        mockMvc.perform(post("/atendimentos")
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarPayloadAtendimento("111.444.777-35"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirBuscarClientePorCpfQuandoTokenTemMesmoCpf() throws Exception {
        when(getClienteByCpfQuery.buscarPorCpf("39053344705"))
                .thenReturn(new ClienteOutput(
                        "cliente-1",
                        "PF",
                        LocalDate.now(),
                        "Cliente Teste",
                        "39053344705",
                        null,
                        null,
                        "cliente@teste.com",
                        "11999999999",
                        null
                ));

        mockMvc.perform(get("/clientes/cpf/39053344705")
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("39053344705"));
    }

    @Test
    void deveNegarConsultaDeClienteSemToken() throws Exception {
        mockMvc.perform(get("/clientes/cpf/39053344705"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    @ValueSource(strings = {"andamento", "aprovacao/aprovar", "aprovacao/reprovar"})
    void permiteClienteAcessarSomenteSuaOrdemServico(final String rota) throws Exception {
        when(ordemServicoRepository.findProprietarioCpfById(ORDEM_SERVICO_ID.toString()))
                .thenReturn(Optional.of(CPF.from("39053344705")));
        configurarRespostaOrdemServico(rota);

        mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota)
                        .header("Authorization", "Bearer " + gerarTokenCliente("390.533.447-05")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"andamento", "aprovacao/aprovar", "aprovacao/reprovar"})
    void bloqueiaOrdemServicoDeOutroClienteAntesDeExecutarOperacao(final String rota) throws Exception {
        when(ordemServicoRepository.findProprietarioCpfById(ORDEM_SERVICO_ID.toString()))
                .thenReturn(Optional.of(CPF.from("11144477735")));

        mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota)
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(acompanharOrdemServicoQuery, aprovarOrdemServicoUseCase, reprovarOrdemServicoUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"andamento", "aprovacao/aprovar", "aprovacao/reprovar"})
    void bloqueiaClienteQuandoOrdemServicoNaoPossuiProprietarioCpf(final String rota) throws Exception {
        when(ordemServicoRepository.findProprietarioCpfById(ORDEM_SERVICO_ID.toString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota)
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705")))
                .andExpect(status().isForbidden());

        verifyNoInteractions(acompanharOrdemServicoQuery, aprovarOrdemServicoUseCase, reprovarOrdemServicoUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"andamento", "aprovacao/aprovar", "aprovacao/reprovar"})
    void bloqueiaOrdemServicoSemToken(final String rota) throws Exception {
        mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(ordemServicoRepository, acompanharOrdemServicoQuery,
                aprovarOrdemServicoUseCase, reprovarOrdemServicoUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"andamento", "aprovacao/aprovar", "aprovacao/reprovar"})
    void preservaAcessoAdministrativoAsOrdensServico(final String rota) throws Exception {
        configurarRespostaOrdemServico(rota);

        mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota)
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());

        verifyNoInteractions(ordemServicoRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aprovar", "reprovar"})
    void preservaPatchDeAprovacaoRestritoAoAdmin(final String acao) throws Exception {
        mockMvc.perform(patch("/ordens-servico/" + ORDEM_SERVICO_ID + "/aprovacao/" + acao)
                        .header("Authorization", "Bearer " + gerarTokenCliente("39053344705")))
                .andExpect(status().isForbidden());

        configurarRespostaOrdemServico("aprovacao/" + acao);
        mockMvc.perform(patch("/ordens-servico/" + ORDEM_SERVICO_ID + "/aprovacao/" + acao)
                        .with(user("admin@autoservice.local").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sem-expiracao", "expiracao-nula", "expirado", "issuer-incorreto", "assinatura-incorreta"})
    void recusaJwtInvalidoNasRotasProtegidas(final String caso) throws Exception {
        final var builder = JWT.create()
                .withSubject("39053344705")
                .withClaim("roles", List.of("CUSTOMER"))
                .withIssuer(caso.equals("issuer-incorreto") ? "outro-issuer" : "autoservice-auth");
        if (caso.equals("expiracao-nula")) {
            builder.withNullClaim("exp");
        } else if (!caso.equals("sem-expiracao")) {
            builder.withExpiresAt(new Date(System.currentTimeMillis() + (caso.equals("expirado") ? -60_000 : 3600_000)));
        }
        final var token = builder.sign(Algorithm.HMAC256(
                caso.equals("assinatura-incorreta") ? "outro-segredo" : JWT_SECRET));

        for (final var rota : List.of("andamento", "aprovacao/aprovar", "aprovacao/reprovar")) {
            mockMvc.perform(get("/ordens-servico/" + ORDEM_SERVICO_ID + "/" + rota)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
        verifyNoInteractions(ordemServicoRepository, acompanharOrdemServicoQuery,
                aprovarOrdemServicoUseCase, reprovarOrdemServicoUseCase);
    }

    private void configurarRespostaOrdemServico(final String rota) {
        switch (rota) {
            case "andamento" -> when(acompanharOrdemServicoQuery.acompanhar(ORDEM_SERVICO_ID))
                    .thenReturn(new AcompanharOrdemServicoOutput(
                            ORDEM_SERVICO_ID.toString(), "RECEBIDO", "Recebido", "Recebido", 0,
                            LocalDate.now(), null, null, null, List.of()));
            case "aprovacao/aprovar" -> when(aprovarOrdemServicoUseCase.execute(any()))
                    .thenReturn(new OrdemServicoStatusOutput(ORDEM_SERVICO_ID.toString(), "EM_EXECUCAO"));
            case "aprovacao/reprovar" -> when(reprovarOrdemServicoUseCase.execute(any()))
                    .thenReturn(new OrdemServicoStatusOutput(ORDEM_SERVICO_ID.toString(), "CANCELADA"));
            default -> throw new IllegalArgumentException("Rota inesperada: " + rota);
        }
    }

    private String gerarTokenCliente(final String cpf) {
        return JWT.create()
                .withSubject(cpf)
                .withClaim("cpf", cpf)
                .withClaim("customer_status", "ATIVO")
                .withClaim("roles", List.of("CUSTOMER"))
                .withIssuer("autoservice-auth")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000))
                .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    private Map<String, Object> criarPayloadAtendimento(final String cpf) {
        return Map.ofEntries(
                new SimpleEntry<>("tipoPessoa", "FISICA"),
                new SimpleEntry<>("nome", "Cliente Teste"),
                new SimpleEntry<>("cpf", cpf),
                new SimpleEntry<>("email", "cliente@teste.com"),
                new SimpleEntry<>("telefone", "11999999999"),
                new SimpleEntry<>("placa", "ABC1D23"),
                new SimpleEntry<>("marca", "Fiat"),
                new SimpleEntry<>("modelo", "Uno"),
                new SimpleEntry<>("ano", 2020),
                new SimpleEntry<>("cor", "Prata"),
                new SimpleEntry<>("kilometragem", 1000),
                new SimpleEntry<>("relato", "Ruido no motor"),
                new SimpleEntry<>("itens", List.of())
        );
    }
}
