package com.autoservice.infrastructure.ordemservico.report;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.TempoPrevistoExecucao;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class OrcamentoReportParametersBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MONEY_FORMAT = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    private OrcamentoReportParametersBuilder() {
    }

    static Map<String, Object> build(
            final DetailOrdemServicoOutput ordemServico,
            final java.io.InputStream logoStream
    ) {
        final Map<String, Object> parameters = new HashMap<>();
        final ListOrdemServicoOutput.ClienteOutput cliente = ordemServico.cliente();
        final ListOrdemServicoOutput.VeiculoOutput veiculo = ordemServico.veiculo();

        parameters.put("ORDEM_SERVICO_ID", ordemServico.ordemServicoId());
        parameters.put("LOGO", logoStream);
        parameters.put("STATUS", OrdemServicoStatus.valueOf(ordemServico.status()).getDescricao());
        parameters.put("DATA_CRIACAO", ordemServico.dataCriacao().format(DATE_FORMATTER));
        parameters.put("TEMPO_PREVISTO_EXECUCAO", formatarTempoPrevisto(
                ordemServico.tempoPrevistoExecucaoDias(),
                ordemServico.tempoPrevistoExecucaoHoras()
        ));
        parameters.put("CLIENTE_NOME", nomeCliente(cliente));
        parameters.put("CLIENTE_DOCUMENTO", documentoCliente(cliente));
        parameters.put("CLIENTE_EMAIL", cliente.email());
        parameters.put("CLIENTE_TELEFONE", cliente.telefone());
        parameters.put("REPRESENTANTE_NOME", cliente.representanteLegal() == null
                ? null
                : cliente.representanteLegal().nome());
        parameters.put("VEICULO_PLACA", veiculo.placa());
        parameters.put("VEICULO_MARCA", veiculo.marca());
        parameters.put("VEICULO_MODELO", veiculo.modelo());
        parameters.put("VEICULO_ANO", String.valueOf(veiculo.ano()));
        parameters.put("VEICULO_COR", veiculo.cor());
        parameters.put("VEICULO_KM", String.valueOf(veiculo.kilometragem()));
        parameters.put("VALOR_TOTAL", formatMoney(ordemServico.valorTotal()));

        return parameters;
    }

    static java.util.Collection<Map<String, ?>> itens(final DetailOrdemServicoOutput ordemServico) {
        final java.util.List<Map<String, ?>> rows = new java.util.ArrayList<>();
        for (final var item : ordemServico.itens()) {
            final Map<String, Object> row = new HashMap<>();
            row.put("tipo", item.tipo());
            row.put("descricao", item.descricao());
            row.put("pecaCodigo", item.peca() == null ? null : item.peca().codigo());
            row.put("quantidade", item.quantidade());
            row.put("valorUnitario", formatMoney(item.valorUnitario()));
            row.put("valorTotal", formatMoney(item.valorTotal()));
            rows.add(row);
        }
        return rows;
    }

    private static String formatMoney(final java.math.BigDecimal value) {
        return MONEY_FORMAT.format(value == null ? java.math.BigDecimal.ZERO : value);
    }

    private static String formatarTempoPrevisto(final Integer dias, final Integer horas) {
        if (dias == null && horas == null) {
            return "";
        }

        try {
            return TempoPrevistoExecucao.of(dias, horas).formatarParaRelatorio();
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    private static String nomeCliente(final ListOrdemServicoOutput.ClienteOutput cliente) {
        return cliente.razaoSocial() == null ? cliente.nome() : cliente.razaoSocial();
    }

    private static String documentoCliente(final ListOrdemServicoOutput.ClienteOutput cliente) {
        return cliente.cnpj() == null ? cliente.cpf() : cliente.cnpj();
    }

}
