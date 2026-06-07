package com.autoservice.infrastructure.ordemservico.report;

import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.orcamento.OrcamentoPdfGenerator;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class JasperOrcamentoPdfGenerator implements OrcamentoPdfGenerator {

    private static final String REPORT_PATH = "reports/orcamento_ordem_servico.jrxml";
    private static final String LOGO_PATH = "assets/logo.png";

    private JasperReport compiledReport;

    @PostConstruct
    void compilarRelatorio() throws Exception {
        try (InputStream reportStream = new ClassPathResource(REPORT_PATH).getInputStream()) {
            this.compiledReport = JasperCompileManager.compileReport(reportStream);
        }
    }

    @Override
    public byte[] generate(final DetailOrdemServicoOutput ordemServico) {
        try (InputStream logoStream = new ClassPathResource(LOGO_PATH).getInputStream()) {
            final var jasperPrint = JasperFillManager.fillReport(
                    this.compiledReport,
                    OrcamentoReportParametersBuilder.build(ordemServico, logoStream),
                    new JRBeanCollectionDataSource(OrcamentoReportParametersBuilder.itens(ordemServico))
            );

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível gerar o PDF do orçamento", exception);
        }
    }
}
