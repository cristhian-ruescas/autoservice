package com.autoservice.presentation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class OpenApiConfiguration {

    public static final String BEARER_JWT = "bearer-jwt";

    private static final List<String> TAG_ORDER = List.of(
            "Autenticação",
            "Atendimentos",
            "Ordens de serviço",
            "Ordem de serviço — métricas",
            "Ordens de compra",
            "Clientes",
            "Veículos",
            "Tipos de veículo",
            "Peças",
            "Serviços",
            "Estoques",
            "Integrações externas"
    );

    private static String descricaoFluxoENegocio() {
        return """
                API da oficina mecânica (Tech Challenge — Fase 2): atendimento, ordens de serviço, catálogos, \
                estoque, ordens de compra, métricas e notificações.
                
                ## Autenticação
                
                1. `POST /auth/login` com `username` e `password` (seed local: `admin@autoservice.local` / `admin123`).
                2. Copie o `token` retornado.
                3. No Swagger, use **Authorize** e informe: `Bearer <token>` (ou só o token — o UI prefixa Bearer).
                
                Rotas administrativas exigem JWT. Públicas: login, Swagger, health, \
                `GET /ordens-servico/{id}/andamento` e links `GET .../aprovacao/aprovar|reprovar` (e-mail).
                
                ## Fluxo sugerido (happy path)
                
                1. **Login** — `POST /auth/login`.
                2. **Abrir OS** — `POST /atendimentos` (cliente PF/PJ + veículo; itens opcionais). Status inicial `RECEBIDO` \
                (ou `EM_DIAGNOSTICO` se já enviare itens).
                3. **Diagnóstico** — `PATCH /ordens-servico/{id}/diagnostico` → `EM_DIAGNOSTICO`.
                4. **Catálogos / itens** — `POST /pecas`, `/tipos-veiculo`, `/servicos`; `POST /ordens-servico/{id}/itens`.
                5. **Orçamento** — `PATCH /ordens-servico/{id}/diagnostico/finalizar` → `AGUARDANDO_APROVACAO` \
                (dispara e-mail com PDF e links de aprovação).
                6. **Aprovar / reprovar** — `PATCH` (admin) ou `GET` (link do e-mail) em `.../aprovacao/aprovar|reprovar` \
                → `EM_EXECUCAO` ou `REPROVADO`. Na aprovação, pode nascer ordem de compra de peças.
                7. **Ordem de compra** — `PATCH /ordens-compra/{id}/realizar` (entrada de estoque).
                8. **Finalizar** — `PATCH /ordens-servico/{id}/finalizar` → `FINALIZADA` (baixa de estoque). \
                Em falta de estoque vinculado: **422**.
                9. **Entregar** — `PATCH /ordens-servico/{id}/entregar` → `ENTREGUE`.
                
                **Acompanhamento do cliente:** `GET /ordens-servico/{id}/andamento` (público).
                
                ## Status da OS (`status`)
                
                | Fase | Valor | Principais endpoints |
                |------|-------|----------------------|
                | Recebida | `RECEBIDO` | `POST /atendimentos` |
                | Diagnóstico | `EM_DIAGNOSTICO` | `PATCH .../diagnostico`, `POST .../itens` |
                | Aguardando aprovação | `AGUARDANDO_APROVACAO` | `PATCH .../diagnostico/finalizar` |
                | Em execução / reprovada | `EM_EXECUCAO` / `REPROVADO` | `.../aprovacao/aprovar|reprovar` |
                | Finalizada / entregue | `FINALIZADA` / `ENTREGUE` | `.../finalizar`, `.../entregar` |
                | Cancelada | `CANCELADO` | quando aplicável |
                
                **Listagem operacional** (`GET /ordens-servico`): prioriza Em Execução > Aguardando Aprovação > \
                Diagnóstico > Recebida; mais antigas primeiro; exclui `FINALIZADA` e `ENTREGUE`.
                
                ## Erros HTTP comuns
                
                | Código | Quando |
                |--------|--------|
                | **400** | Validação / JSON inválido (`ErrorResponse`) |
                | **401** | Sem JWT ou credenciais inválidas |
                | **422** | Regra de domínio (status inválido, estoque insuficiente, peça sem estoque vinculado, etc.) |
                | **500** | Erro inesperado |
                """;
    }

    @Bean
    public OpenAPI autoserviceOpenApi(
            @Value("${autoservice.app.base-url:http://localhost:8088}") final String baseUrl
    ) {
        final var errorSchema = new Schema<>().$ref("#/components/schemas/ErrorResponse");
        final var errorContent = new Content().addMediaType(
                "application/json",
                new MediaType().schema(errorSchema)
        );

        final Map<String, ApiResponse> commonResponses = new LinkedHashMap<>();
        commonResponses.put("400", new ApiResponse()
                .description("Requisição inválida (validação ou JSON)")
                .content(errorContent));
        commonResponses.put("401", new ApiResponse()
                .description("Não autenticado ou credenciais inválidas"));
        commonResponses.put("422", new ApiResponse()
                .description("Erro de regra de negócio / domínio")
                .content(errorContent));
        commonResponses.put("500", new ApiResponse()
                .description("Erro interno")
                .content(errorContent));

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(baseUrl).description("API local (APP_BASE_URL)")
                ))
                .info(new Info()
                        .title("Autoservice API")
                        .version("2.0.0")
                        .description(descricaoFluxoENegocio())
                        .contact(new Contact().name("Autoservice — Tech Challenge").url(baseUrl)))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger UI")
                        .url(baseUrl + "/swagger-ui.html"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT, new SecurityScheme()
                                .name(BEARER_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido em POST /auth/login"))
                        .responses(commonResponses))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_JWT));
    }

    @Bean
    public OpenApiCustomizer ordenarTagsEAjustarSeguranca() {
        return openApi -> {
            final var tags = openApi.getTags();
            if (tags != null && !tags.isEmpty()) {
                final var sorted = tags.stream()
                        .sorted(Comparator
                                .comparingInt((Tag t) -> {
                                    final int i = TAG_ORDER.indexOf(t.getName());
                                    return i >= 0 ? i : Integer.MAX_VALUE;
                                })
                                .thenComparing(Tag::getName, Comparator.nullsFirst(String::compareToIgnoreCase)))
                        .toList();
                openApi.setTags(sorted);
            }

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {
                if (pathItem == null) {
                    return;
                }
                pathItem.readOperationsMap().forEach((method, operation) -> {
                    if (operation == null) {
                        return;
                    }
                    if (isPublic(path, method)) {
                        operation.setSecurity(List.of());
                    } else {
                        operation.setSecurity(List.of(new SecurityRequirement().addList(BEARER_JWT)));
                    }
                    adicionarRespostasPadrao(operation, method, path);
                });
            });
        };
    }

    private static boolean isPublic(final String path, final PathItem.HttpMethod method) {
        if (path.startsWith("/auth")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/actuator")) {
            return true;
        }
        if (method == PathItem.HttpMethod.GET && path.matches("/ordens-servico/\\{[^}]+\\}/andamento")) {
            return true;
        }
        return method == PathItem.HttpMethod.GET
                && (path.matches("/ordens-servico/\\{[^}]+\\}/aprovacao/aprovar")
                || path.matches("/ordens-servico/\\{[^}]+\\}/aprovacao/reprovar"));
    }

    private static void adicionarRespostasPadrao(
            final Operation operation,
            final PathItem.HttpMethod method,
            final String path
    ) {
        if (operation.getResponses() == null) {
            return;
        }
        final var responses = operation.getResponses();
        if (!isPublic(path, method) && !responses.containsKey("401")) {
            responses.addApiResponse("401", new ApiResponse().$ref("#/components/responses/401"));
        }
        if (Set.of(PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT, PathItem.HttpMethod.PATCH)
                .contains(method)
                && !responses.containsKey("400")) {
            responses.addApiResponse("400", new ApiResponse().$ref("#/components/responses/400"));
        }
        if (Set.of(PathItem.HttpMethod.POST, PathItem.HttpMethod.PUT, PathItem.HttpMethod.PATCH, PathItem.HttpMethod.DELETE)
                .contains(method)
                && !responses.containsKey("422")) {
            responses.addApiResponse("422", new ApiResponse().$ref("#/components/responses/422"));
        }
    }
}
