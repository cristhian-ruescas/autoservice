package com.autoservice.infrastructure.ordemservico.notificacao;

import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusClienteNotifier;
import com.autoservice.application.ordemservico.notificacao.OrdemServicoStatusNotificacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class WebhookOrdemServicoStatusClienteNotifier implements OrdemServicoStatusClienteNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookOrdemServicoStatusClienteNotifier.class);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String webhookUrl;

    public WebhookOrdemServicoStatusClienteNotifier(
            final ObjectMapper objectMapper,
            @Value("${autoservice.notificacao.webhook-url:}") final String webhookUrl
    ) {
        this.objectMapper = objectMapper;
        this.webhookUrl = webhookUrl == null ? "" : webhookUrl.trim();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void notificar(final OrdemServicoStatusNotificacao notificacao) {
        if (this.webhookUrl.isBlank()) {
            return;
        }

        try {
            final var payload = objectMapper.writeValueAsString(Map.of(
                    "ordemServicoId", notificacao.ordemServicoId(),
                    "statusAnterior", notificacao.statusAnterior(),
                    "statusNovo", notificacao.statusNovo(),
                    "statusNovoDescricao", notificacao.statusNovoDescricao(),
                    "clienteEmail", notificacao.clienteEmail(),
                    "clienteNome", notificacao.clienteNome(),
                    "placa", notificacao.placa(),
                    "andamentoUrl", notificacao.andamentoUrl(),
                    "ocorridoEm", notificacao.ocorridoEm().toString()
            ));

            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(this.webhookUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            final var response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                LOGGER.warn(
                        "Webhook de status da OS {} retornou HTTP {}",
                        notificacao.ordemServicoId(),
                        response.statusCode()
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            LOGGER.error(
                    "Webhook de status da OS {} interrompido",
                    notificacao.ordemServicoId(),
                    exception
            );
        } catch (Exception exception) {
            LOGGER.error(
                    "Falha ao enviar webhook de status da OS {}",
                    notificacao.ordemServicoId(),
                    exception
            );
        }
    }
}
