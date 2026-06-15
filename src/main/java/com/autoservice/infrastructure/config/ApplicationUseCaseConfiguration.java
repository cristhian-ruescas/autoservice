package com.autoservice.infrastructure.config;

import com.autoservice.application.atendimento.create.AbrirAtendimentoUseCase;
import com.autoservice.application.atendimento.create.AtendimentoDomainEventPublisher;
import com.autoservice.application.atendimento.create.PessoaAtendimentoOrchestrator;
import com.autoservice.application.atendimento.create.TipoVeiculoAtendimentoResolver;
import com.autoservice.application.cliente.delete.RemoverClienteUseCase;
import com.autoservice.application.cliente.update.AtualizarClienteUseCase;
import com.autoservice.application.estoque.localizacao.AtualizarLocalizacaoEstoqueUseCase;
import com.autoservice.application.ordemcompra.realizar.RealizarOrdemCompraUseCase;
import com.autoservice.application.ordemservico.aprovacao.AprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.aprovacao.ReprovarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.create.DefaultCreateOrdemServicoUseCase;
import com.autoservice.application.ordemservico.delete.RemoverOrdemServicoUseCase;
import com.autoservice.application.ordemservico.diagnostico.FinalizarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.diagnostico.IniciarDiagnosticoUseCase;
import com.autoservice.application.ordemservico.entrega.EntregarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.finalizacao.FinalizarOrdemServicoUseCase;
import com.autoservice.application.ordemservico.itemservico.AdicionarItensServicoUseCase;
import com.autoservice.application.ordemservico.itemservico.AtualizarItemServicoUseCase;
import com.autoservice.application.ordemservico.itemservico.ItemServicoOrchestrator;
import com.autoservice.application.ordemservico.itemservico.RemoverItemServicoUseCase;
import com.autoservice.application.ordemservico.update.AtualizarOrdemServicoUseCase;
import com.autoservice.application.peca.create.CadastrarPecaUseCase;
import com.autoservice.application.peca.delete.RemoverPecaUseCase;
import com.autoservice.application.peca.update.AtualizarPecaUseCase;
import com.autoservice.application.pessoa.RepresentanteLegalOrchestrator;
import com.autoservice.application.servico.create.CadastrarServicoUseCase;
import com.autoservice.application.servico.delete.RemoverServicoUseCase;
import com.autoservice.application.servico.update.AtualizarServicoUseCase;
import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.application.tipoveiculo.create.CadastrarTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.delete.RemoverTipoVeiculoUseCase;
import com.autoservice.application.tipoveiculo.update.AtualizarTipoVeiculoUseCase;
import com.autoservice.application.veiculo.delete.RemoverVeiculoUseCase;
import com.autoservice.application.veiculo.update.AtualizarVeiculoUseCase;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.estoque.EstoqueGateway;
import com.autoservice.domain.events.DomainEventPublisher;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemcompra.OrdemCompraGateway;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.pessoa.PessoaGateway;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfiguration {

    @Bean
    TipoVeiculoResolver tipoVeiculoResolver(final TipoVeiculoGateway tipoVeiculoGateway) {
        return new TipoVeiculoResolver(tipoVeiculoGateway);
    }

    @Bean
    RepresentanteLegalOrchestrator representanteLegalOrchestrator(final PessoaGateway pessoaGateway) {
        return new RepresentanteLegalOrchestrator(pessoaGateway);
    }

    @Bean
    PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator(
            final PessoaGateway pessoaGateway,
            final RepresentanteLegalOrchestrator representanteLegalOrchestrator
    ) {
        return new PessoaAtendimentoOrchestrator(pessoaGateway, representanteLegalOrchestrator);
    }

    @Bean
    AtendimentoDomainEventPublisher atendimentoDomainEventPublisher(
            final DomainEventPublisher eventPublisher,
            final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator
    ) {
        return new AtendimentoDomainEventPublisher(eventPublisher, pessoaAtendimentoOrchestrator);
    }

    @Bean
    TipoVeiculoAtendimentoResolver tipoVeiculoAtendimentoResolver(final TipoVeiculoResolver tipoVeiculoResolver) {
        return new TipoVeiculoAtendimentoResolver(tipoVeiculoResolver);
    }

    @Bean
    ItemServicoOrchestrator itemServicoOrchestrator(
            final OrdemServicoGateway ordemServicoGateway,
            final PecaGateway pecaGateway
    ) {
        return new ItemServicoOrchestrator(ordemServicoGateway, pecaGateway);
    }

    @Bean
    AbrirAtendimentoUseCase abrirAtendimentoUseCase(
            final PessoaAtendimentoOrchestrator pessoaAtendimentoOrchestrator,
            final TipoVeiculoAtendimentoResolver tipoVeiculoAtendimentoResolver,
            final AtendimentoDomainEventPublisher atendimentoDomainEventPublisher,
            final ClienteGateway clienteGateway,
            final VeiculoGateway veiculoGateway,
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoOrchestrator itemServicoOrchestrator,
            final ItemServicoGateway itemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new AbrirAtendimentoUseCase(
                pessoaAtendimentoOrchestrator,
                tipoVeiculoAtendimentoResolver,
                atendimentoDomainEventPublisher,
                clienteGateway,
                veiculoGateway,
                ordemServicoGateway,
                itemServicoOrchestrator,
                itemServicoGateway,
                eventPublisher
        );
    }

    @Bean
    AtualizarClienteUseCase atualizarClienteUseCase(
            final ClienteGateway clienteGateway,
            final PessoaGateway pessoaGateway,
            final RepresentanteLegalOrchestrator representanteLegalOrchestrator
    ) {
        return new AtualizarClienteUseCase(clienteGateway, pessoaGateway, representanteLegalOrchestrator);
    }

    @Bean
    RemoverClienteUseCase removerClienteUseCase(final ClienteGateway clienteGateway) {
        return new RemoverClienteUseCase(clienteGateway);
    }

    @Bean
    AtualizarLocalizacaoEstoqueUseCase atualizarLocalizacaoEstoqueUseCase(final EstoqueGateway estoqueGateway) {
        return new AtualizarLocalizacaoEstoqueUseCase(estoqueGateway);
    }

    @Bean
    RealizarOrdemCompraUseCase realizarOrdemCompraUseCase(
            final OrdemCompraGateway ordemCompraGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new RealizarOrdemCompraUseCase(ordemCompraGateway, eventPublisher);
    }

    @Bean
    AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new AprovarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
    }

    @Bean
    ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        return new ReprovarOrdemServicoUseCase(ordemServicoGateway);
    }

    @Bean
    RemoverOrdemServicoUseCase removerOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        return new RemoverOrdemServicoUseCase(ordemServicoGateway);
    }

    @Bean
    DefaultCreateOrdemServicoUseCase defaultCreateOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new DefaultCreateOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
    }

    @Bean
    FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoGateway itemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new FinalizarDiagnosticoUseCase(ordemServicoGateway, itemServicoGateway, eventPublisher);
    }

    @Bean
    IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new IniciarDiagnosticoUseCase(ordemServicoGateway, eventPublisher);
    }

    @Bean
    EntregarOrdemServicoUseCase entregarOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        return new EntregarOrdemServicoUseCase(ordemServicoGateway);
    }

    @Bean
    FinalizarOrdemServicoUseCase finalizarOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final DomainEventPublisher eventPublisher
    ) {
        return new FinalizarOrdemServicoUseCase(ordemServicoGateway, eventPublisher);
    }

    @Bean
    AdicionarItensServicoUseCase adicionarItensServicoUseCase(
            final ItemServicoOrchestrator itemServicoOrchestrator,
            final ItemServicoGateway itemServicoGateway
    ) {
        return new AdicionarItensServicoUseCase(itemServicoOrchestrator, itemServicoGateway);
    }

    @Bean
    AtualizarItemServicoUseCase atualizarItemServicoUseCase(
            final ItemServicoOrchestrator itemServicoOrchestrator,
            final ItemServicoGateway itemServicoGateway
    ) {
        return new AtualizarItemServicoUseCase(itemServicoOrchestrator, itemServicoGateway);
    }

    @Bean
    RemoverItemServicoUseCase removerItemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoGateway itemServicoGateway
    ) {
        return new RemoverItemServicoUseCase(ordemServicoGateway, itemServicoGateway);
    }

    @Bean
    AtualizarOrdemServicoUseCase atualizarOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final VeiculoGateway veiculoGateway
    ) {
        return new AtualizarOrdemServicoUseCase(ordemServicoGateway, veiculoGateway);
    }

    @Bean
    CadastrarPecaUseCase cadastrarPecaUseCase(
            final PecaGateway pecaGateway,
            final TipoVeiculoGateway tipoVeiculoGateway
    ) {
        return new CadastrarPecaUseCase(pecaGateway, tipoVeiculoGateway);
    }

    @Bean
    RemoverPecaUseCase removerPecaUseCase(
            final PecaGateway pecaGateway,
            final EstoqueGateway estoqueGateway,
            final ItemServicoGateway itemServicoGateway
    ) {
        return new RemoverPecaUseCase(pecaGateway, estoqueGateway, itemServicoGateway);
    }

    @Bean
    AtualizarPecaUseCase atualizarPecaUseCase(
            final PecaGateway pecaGateway,
            final TipoVeiculoGateway tipoVeiculoGateway
    ) {
        return new AtualizarPecaUseCase(pecaGateway, tipoVeiculoGateway);
    }

    @Bean
    CadastrarServicoUseCase cadastrarServicoUseCase(final ServicoGateway servicoGateway) {
        return new CadastrarServicoUseCase(servicoGateway);
    }

    @Bean
    RemoverServicoUseCase removerServicoUseCase(final ServicoGateway servicoGateway) {
        return new RemoverServicoUseCase(servicoGateway);
    }

    @Bean
    AtualizarServicoUseCase atualizarServicoUseCase(final ServicoGateway servicoGateway) {
        return new AtualizarServicoUseCase(servicoGateway);
    }

    @Bean
    CadastrarTipoVeiculoUseCase cadastrarTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        return new CadastrarTipoVeiculoUseCase(tipoVeiculoGateway);
    }

    @Bean
    RemoverTipoVeiculoUseCase removerTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        return new RemoverTipoVeiculoUseCase(tipoVeiculoGateway);
    }

    @Bean
    AtualizarTipoVeiculoUseCase atualizarTipoVeiculoUseCase(final TipoVeiculoGateway tipoVeiculoGateway) {
        return new AtualizarTipoVeiculoUseCase(tipoVeiculoGateway);
    }

    @Bean
    RemoverVeiculoUseCase removerVeiculoUseCase(final VeiculoGateway veiculoGateway) {
        return new RemoverVeiculoUseCase(veiculoGateway);
    }

    @Bean
    AtualizarVeiculoUseCase atualizarVeiculoUseCase(
            final VeiculoGateway veiculoGateway,
            final TipoVeiculoResolver tipoVeiculoResolver
    ) {
        return new AtualizarVeiculoUseCase(veiculoGateway, tipoVeiculoResolver);
    }
}
