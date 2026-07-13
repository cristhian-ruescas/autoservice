package com.autoservice.infrastructure.ordemservico.report;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.TempoPrevistoExecucao;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class OrcamentoReportParametersBuilder {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        parameters.put("VALOR_TOTAL", ordemServico.valorTotal());

        return parameters;
    }

    static List<ItemReportRow> itens(final DetailOrdemServicoOutput ordemServico) {
        return ordemServico.itens().stream()
                .map(item -> new ItemReportRow(
                        item.tipo(),
                        item.descricao(),
                        item.peca() == null ? null : item.peca().codigo(),
                        item.quantidade(),
                        item.valorUnitario(),
                        item.valorTotal()
                ))
                .toList();
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

    record ItemReportRow(
            String tipo,
            String descricao,
            String pecaCodigo,
            Integer quantidade,
            BigDecimal valorUnitario,
            BigDecimal valorTotal
    ) {
        public String getTipo() {
            return tipo;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getPecaCodigo() {
            return pecaCodigo;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public BigDecimal getValorUnitario() {
            return valorUnitario;
        }

        public BigDecimal getValorTotal() {
            return valorTotal;
        }
    }
}
