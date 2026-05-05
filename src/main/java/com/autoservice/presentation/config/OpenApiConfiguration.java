package com.autoservice.presentation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private static final List<String> TAG_ORDER = List.of(
            "Atendimentos",
            "Tipos de veículo",
            "Peças",
            "Serviços",
            "Estoques",
            "Ordens de serviço",
            "Ordem de serviço — métricas",
            "Ordens de compra"
    );

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
                        .description(descricaoFluxoENegocio())
                        .contact(new Contact().name("Autoservice").url(baseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description("Interface Swagger")
                        .url(baseUrl + "/swagger-ui.html"));
    }

    @Bean
    public OpenApiCustomizer ordenarTagsPeloFluxo() {
        return openApi -> {
            final var tags = openApi.getTags();
            if (tags == null || tags.isEmpty()) {
                return;
            }
            final var sorted = tags.stream()
                    .sorted(Comparator
                            .comparingInt((Tag t) -> {
                                final int i = TAG_ORDER.indexOf(t.getName());
                                return i >= 0 ? i : Integer.MAX_VALUE;
                            })
                            .thenComparing(Tag::getName, Comparator.nullsFirst(String::compareToIgnoreCase)))
                    .toList();
            openApi.setTags(sorted);
        };
    }

    private static String descricaoFluxoENegocio() {
        return """
                API da oficina: atendimento (abertura de OS com cliente PF ou PJ), ordem de serviço, peças, **catálogo de serviços**, estoque, ordem de compra e **métricas** de execução.

                ## Fluxo sugerido (happy path)

                1. **Cliente PF ou PJ + OS** — `POST /atendimentos` com `tipoPessoa` `FISICA` ou `JURIDICA`. Retorna a OS em `RECEBIDO`.
                2. **Iniciar diagnóstico** — `PATCH /ordens-servico/{id}/diagnostico` → `EM_DIAGNOSTICO`.
                3. **Catálogos** — `POST /pecas`, opcional `POST /tipos-veiculo` (marca/modelo/ano) e `POST /servicos` (serviços ofertados e valor de referência).
                4. **Incluir itens na OS (serviço e/ou peça)** — `POST /ordens-servico/{id}/itens` com itens do tipo `SERVICO` ou `PECA` (somente com OS em `EM_DIAGNOSTICO`). Opcional: `GET /estoques` para consultar disponibilidade.
                5. **Finalizar diagnóstico / orçamento** — `PATCH /ordens-servico/{id}/diagnostico/finalizar` → `AGUARDANDO_APROVACAO` (valor total do orçamento deve ser maior que zero).
                6. **Aprovação** — `PATCH /ordens-servico/{id}/aprovacao/aprovar` (ou `.../reprovar`) → `EM_EXECUCAO` quando aprovada.
                7. **Ordem de compra** — `GET /ordens-compra` e `GET /ordens-compra/{id}`; **realizar** com `PATCH /ordens-compra/{id}/realizar` (atualiza estoque na conclusão do pedido).
                8. **Encerrar serviço** — `PATCH /ordens-servico/{id}/finalizar` → `FINALIZADA`.
                9. **Entrega** — `PATCH /ordens-servico/{id}/entregar` → `ENTREGUE`.

                **Atalho de leitura:** acompanhamento do cliente — `GET /ordens-servico/{id}/andamento`.

                ## Resumo: status da ordem de serviço

                | Fase | Valor em `status` (API) | Endpoints principais |
                |------|-------------------------|----------------------|
                | Recebida | `RECEBIDO` | `POST /atendimentos` |
                | Em diagnóstico / montando orçamento | `EM_DIAGNOSTICO` | `PATCH .../diagnostico`, `POST .../itens` |
                | Aguardando cliente | `AGUARDANDO_APROVACAO` | `PATCH .../diagnostico/finalizar` |
                | Aprovada / reprovada | `APROVADO` / `REPROVADO` | `PATCH .../aprovacao/aprovar` ou `reprovar` |
                | Em execução | `EM_EXECUCAO` | após aprovação; ordens de compra no painel `GET /ordens-compra` |
                | Finalizada / entregue | `FINALIZADA` / `ENTREGUE` | `PATCH .../finalizar`, `PATCH .../entregar` |
                | Cancelada | `CANCELADO` | estados de domínio; consultas podem expor quando aplicável |

                **Nota:** rótulos de negócio podem estar no feminino; os identificadores do enum seguem os nomes acima (`RECEBIDO`, `APROVADO`, etc.).

                ## Outros recursos

                - **Estoque:** `GET /estoques`, `GET /estoques/{id}`, `PATCH /estoques/{id}/localizacao`
                - **Listagem e detalhe da OS:** `GET /ordens-servico`, `GET /ordens-servico/{id}`
                - **CRUD de serviços (catálogo):** `GET/POST /servicos`, `GET/PUT/DELETE /servicos/{id}`
                - **Tempo médio de execução (após aprovar até finalizar):** `GET /ordens-servico/metricas/tempo-medio-execucao`
                """;
    }
}
