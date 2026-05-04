package com.autoservice.presentation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI autoserviceOpenApi(
            @Value("${autoservice.app.base-url:http://localhost:8088}") final String baseUrl
    ) {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(baseUrl).description("URL base (autoservice.app.base-url / APP_BASE_URL)")
                ))
                .info(new Info()
                        .title("Autoservice API")
                        .version("0.0.1-SNAPSHOT")
                        .description(descricaoFluxoOrdemServico())
                        .contact(new Contact().name("Autoservice").url(baseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description("UI Swagger")
                        .url(baseUrl + "/swagger-ui/index.html"));
    }

    /**
     * Documenta o fluxo de negócio (diagrama) e o mapeamento para os valores reais expostos pela API
     * (`com.autoservice.domain.ordemservico.enums.OrdemServicoStatus`).
     */
    private static String descricaoFluxoOrdemServico() {
        return """
                API do oficina: atendimento (abertura de OS), ordem de serviço, estoque, peças, ordem de compra e cadastro de tipo de veículo.

                ## Fluxo da ordem de serviço (visão de negócio × API)

                | Etapa no diagrama | Valor em `status` na API | Endpoints relacionados |
                |---------------------|--------------------------|--------------------------|
                | Recebida | `RECEBIDO` | `POST /atendimentos` (cria cliente/veículo e OS) |
                | Em diagnóstico | `EM_DIAGNOSTICO` | `PATCH /ordens-servico/{id}/diagnostico` |
                | Orçamento (itens, preço, estoque) | (ainda `EM_DIAGNOSTICO`) | `POST /ordens-servico/{id}/itens` — só com OS em diagnóstico |
                | Orçamento gerado → aguardando cliente | `AGUARDANDO_APROVACAO` | `PATCH /ordens-servico/{id}/diagnostico/finalizar` — envio de e-mail de orçamento no fluxo de domínio |
                | Reprovada | `REPROVADO` | `PATCH` ou `GET /ordens-servico/{id}/aprovacao/reprovar` |
                | Aprovada | `APROVADO` | `PATCH` ou `GET /ordens-servico/{id}/aprovacao/aprovar` |
                | Em execução | `EM_EXECUCAO` | Transição após aprovação (pedido de peças pode ser disparado por eventos) |
                | Finalizada | `FINALIZADA` | `PATCH /ordens-servico/{id}/finalizar` |
                | Entregue | `ENTREGUE` | `PATCH /ordens-servico/{id}/entregar` |
                | Cancelada | `CANCELADO` | Estado de domínio; exposto quando aplicável em consultas |

                **Nota:** no diagrama aparecem rótulos no feminino (ex.: RECEBIDA); na API o enum usa os identificadores acima (`RECEBIDO`, `APROVADO`, …).

                ## Outros recursos

                - **Estoque:** `GET /estoques`, `GET /estoques/{id}`, `PATCH /estoques/{id}/localizacao`
                - **Peças:** `POST /pecas`
                - **Ordens de compra:** `GET /ordens-compra`, `GET /ordens-compra/{id}`, `PATCH /ordens-compra/{id}/realizar`
                - **Tipos de veículo:** `POST /tipos-veiculo`
                """;
    }
}
