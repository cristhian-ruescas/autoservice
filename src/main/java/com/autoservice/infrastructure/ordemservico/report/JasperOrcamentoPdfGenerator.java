package com.autoservice.infrastructure.ordemservico.report;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.orcamento.OrcamentoPdfGenerator;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JasperOrcamentoPdfGenerator implements OrcamentoPdfGenerator {

    private static final String REPORT_PATH = "reports/orcamento_ordem_servico.jrxml";
    private static final String LOGO_PATH = "assets/logo.png";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] generate(final DetailOrdemServicoOutput ordemServico) {
        try (
                InputStream reportStream = new ClassPathResource(REPORT_PATH).getInputStream();
                InputStream logoStream = new ClassPathResource(LOGO_PATH).getInputStream()
        ) {
            final var jasperReport = JasperCompileManager.compileReport(reportStream);
            final var jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parametersOf(ordemServico, logoStream),
                    dataSourceOf(ordemServico)
            );

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível gerar o PDF do orçamento", exception);
        }
    }

    private Map<String, Object> parametersOf(
            final DetailOrdemServicoOutput ordemServico,
            final InputStream logoStream
    ) {
        final Map<String, Object> parameters = new HashMap<>();
        final var cliente = ordemServico.cliente();
        final var veiculo = ordemServico.veiculo();

        parameters.put("ORDEM_SERVICO_ID", ordemServico.ordemServicoId());
        parameters.put("LOGO", logoStream);
        parameters.put("STATUS", statusDescricao(ordemServico.status()));
        parameters.put("DATA_CRIACAO", ordemServico.dataCriacao().format(DATE_FORMATTER));
        parameters.put("TEMPO_PREVISTO_EXECUCAO", tempoPrevistoExecucao(ordemServico));
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

    private JRBeanCollectionDataSource dataSourceOf(final DetailOrdemServicoOutput ordemServico) {
        final List<ItemReportRow> rows = ordemServico.itens().stream()
                .map(item -> new ItemReportRow(
                        item.tipo(),
                        item.descricao(),
                        item.peca() == null ? null : item.peca().codigo(),
                        item.quantidade(),
                        item.valorUnitario(),
                        item.valorTotal()
                ))
                .toList();

        return new JRBeanCollectionDataSource(rows);
    }

    private String nomeCliente(final ListOrdemServicoOutput.ClienteOutput cliente) {
        return cliente.razaoSocial() == null ? cliente.nome() : cliente.razaoSocial();
    }

    private String documentoCliente(final ListOrdemServicoOutput.ClienteOutput cliente) {
        return cliente.cnpj() == null ? cliente.cpf() : cliente.cnpj();
    }

    private String statusDescricao(final String status) {
        return OrdemServicoStatus.valueOf(status).getDescricao();
    }

    private String tempoPrevistoExecucao(final DetailOrdemServicoOutput ordemServico) {
        final int dias = ordemServico.tempoPrevistoExecucaoDias() == null
                ? 0
                : ordemServico.tempoPrevistoExecucaoDias();
        final int horas = ordemServico.tempoPrevistoExecucaoHoras() == null
                ? 0
                : ordemServico.tempoPrevistoExecucaoHoras();
        final List<String> partes = new ArrayList<>();

        if (dias > 0) {
            partes.add(dias + " dia(s)");
        }
        if (horas > 0) {
            partes.add(horas + " hora(s)");
        }

        return String.join(" e ", partes);
    }

    public static class ItemReportRow {

        private final String tipo;
        private final String descricao;
        private final String pecaCodigo;
        private final Integer quantidade;
        private final BigDecimal valorUnitario;
        private final BigDecimal valorTotal;

        public ItemReportRow(
                final String tipo,
                final String descricao,
                final String pecaCodigo,
                final Integer quantidade,
                final BigDecimal valorUnitario,
                final BigDecimal valorTotal
        ) {
            this.tipo = tipo;
            this.descricao = descricao;
            this.pecaCodigo = pecaCodigo;
            this.quantidade = quantidade;
            this.valorUnitario = valorUnitario;
            this.valorTotal = valorTotal;
        }

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
