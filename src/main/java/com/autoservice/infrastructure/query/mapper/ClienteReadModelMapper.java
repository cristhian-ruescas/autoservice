package com.autoservice.infrastructure.query.mapper;

import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.pessoa.TipoPessoaCodigo;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.infrastructure.query.ValueObjectFormatter;
import com.autoservice.validation.Error;

public final class ClienteReadModelMapper {

    private ClienteReadModelMapper() {
    }

    public static ClienteOutput fromQueryRow(final Object[] row) {
        final var cliente = QueryRowMapperSupport.toCliente(row[0]);
        final var pessoaFisica = QueryRowMapperSupport.toPessoaFisica(row[1]);
        final var pessoaJuridica = QueryRowMapperSupport.toPessoaJuridica(row[2]);
        final var representante = QueryRowMapperSupport.toPessoaFisica(row[3]);

        return toClienteOutput(cliente, pessoaFisica, pessoaJuridica, representante);
    }

    public static ClienteOutput toClienteOutput(
            final Cliente cliente,
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica,
            final PessoaFisica representante
    ) {
        if (pessoaJuridica != null) {
            return new ClienteOutput(
                    cliente.getId().getValue(),
                    TipoPessoaCodigo.JURIDICA,
                    cliente.getDataCadastro().getValue(),
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    ValueObjectFormatter.asString(pessoaJuridica.getEmail()),
                    ValueObjectFormatter.asString(pessoaJuridica.getTelefone()),
                    PessoaReadModelMapper.toRepresentanteLegalComId(representante)
            );
        }

        if (pessoaFisica == null) {
            throw DomainException.with(new Error("Pessoa do cliente não encontrada"));
        }

        return new ClienteOutput(
                cliente.getId().getValue(),
                TipoPessoaCodigo.FISICA,
                cliente.getDataCadastro().getValue(),
                pessoaFisica.getNome(),
                pessoaFisica.getCpf().getValue(),
                null,
                null,
                ValueObjectFormatter.asString(pessoaFisica.getEmail()),
                ValueObjectFormatter.asString(pessoaFisica.getTelefone()),
                null
        );
    }

    public static ListOrdemServicoOutput.ClienteOutput toOrdemServicoListClienteOutput(
            final Cliente cliente,
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica,
            final PessoaFisica representante
    ) {
        if (pessoaJuridica != null) {
            return new ListOrdemServicoOutput.ClienteOutput(
                    cliente.getId().getValue(),
                    TipoPessoaCodigo.JURIDICA,
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    ValueObjectFormatter.asString(pessoaJuridica.getEmail()),
                    ValueObjectFormatter.asString(pessoaJuridica.getTelefone()),
                    PessoaReadModelMapper.toRepresentanteLegalSemId(representante)
            );
        }

        if (pessoaFisica == null) {
            throw DomainException.with(new Error("Pessoa do cliente não encontrada"));
        }

        return new ListOrdemServicoOutput.ClienteOutput(
                cliente.getId().getValue(),
                TipoPessoaCodigo.FISICA,
                pessoaFisica.getNome(),
                pessoaFisica.getCpf().getValue(),
                null,
                null,
                ValueObjectFormatter.asString(pessoaFisica.getEmail()),
                ValueObjectFormatter.asString(pessoaFisica.getTelefone()),
                null
        );
    }
}
