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
import com.autoservice.infrastructure.security.CpfAccessGuard;
import com.autoservice.infrastructure.security.JwtUtil;
import com.autoservice.infrastructure.security.UsuarioUserDetailsService;
import com.autoservice.infrastructure.security.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AtendimentoController.class, ClienteController.class})
@Import({WebSecurityConfig.class, CpfAccessGuard.class, ApiGatewaySecurityIntegrationTest.SecurityBeans.class})
@TestPropertySource(properties = {
        "autoservice.jwt.secret=segredo-local-com-tamanho-suficiente-para-testes",
        "autoservice.jwt.issuer=autoservice-auth"
})
class ApiGatewaySecurityIntegrationTest {

    private static final String JWT_SECRET = "segredo-local-com-tamanho-suficiente-para-testes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AbrirAtendimentoUseCase abrirAtendimentoUseCase;

    @MockBean
    private ListClientesQuery listClientesQuery;

    @MockBean
    private GetClienteByIdQuery getClienteByIdQuery;

    @MockBean
    private GetClienteByCpfQuery getClienteByCpfQuery;

    @MockBean
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @MockBean
    private RemoverClienteUseCase removerClienteUseCase;

    @MockBean
    private UsuarioUserDetailsService usuarioUserDetailsService;

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
