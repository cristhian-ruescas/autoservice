package com.autoservice.infrastructure.query.mapper;

import com.autoservice.application.cliente.query.ClienteOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.pessoa.TipoPessoaCodigo;
import com.autoservice.application.veiculo.query.VeiculoOutput;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.infrastructure.query.ValueObjectFormatter;

public final class PessoaReadModelMapper {

    private PessoaReadModelMapper() {
    }

    public static ClienteOutput.RepresentanteLegalOutput toRepresentanteLegalComId(
            final PessoaFisica representante
    ) {
        if (representante == null) {
            return null;
        }

        return new ClienteOutput.RepresentanteLegalOutput(
                representante.getId().getValue(),
                representante.getNome(),
                representante.getCpf().getValue(),
                ValueObjectFormatter.asString(representante.getEmail()),
                ValueObjectFormatter.asString(representante.getTelefone())
        );
    }

    public static ListOrdemServicoOutput.RepresentanteLegalOutput toRepresentanteLegalSemId(
            final PessoaFisica representante
    ) {
        if (representante == null) {
            return null;
        }

        return new ListOrdemServicoOutput.RepresentanteLegalOutput(
                representante.getNome(),
                representante.getCpf().getValue(),
                ValueObjectFormatter.asString(representante.getEmail()),
                ValueObjectFormatter.asString(representante.getTelefone())
        );
    }

    public static VeiculoOutput.ProprietarioOutput toProprietarioOutput(
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica
    ) {
        if (pessoaJuridica != null) {
            return new VeiculoOutput.ProprietarioOutput(
                    TipoPessoaCodigo.JURIDICA,
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    ValueObjectFormatter.asString(pessoaJuridica.getEmail()),
                    ValueObjectFormatter.asString(pessoaJuridica.getTelefone())
            );
        }

        if (pessoaFisica != null) {
            return new VeiculoOutput.ProprietarioOutput(
                    TipoPessoaCodigo.FISICA,
                    pessoaFisica.getNome(),
                    pessoaFisica.getCpf().getValue(),
                    null,
                    null,
                    ValueObjectFormatter.asString(pessoaFisica.getEmail()),
                    ValueObjectFormatter.asString(pessoaFisica.getTelefone())
            );
        }

        return null;
    }
}
