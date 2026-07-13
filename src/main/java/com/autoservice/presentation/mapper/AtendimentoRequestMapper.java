package com.autoservice.presentation.mapper;

import com.autoservice.application.atendimento.create.AbrirAtendimentoCommand;
import com.autoservice.application.atendimento.create.AbrirAtendimentoItemCommand;
import com.autoservice.application.atendimento.create.enums.TipoPessoaAtendimento;
import com.autoservice.presentation.dto.atendimento.AbrirAtendimentoRequest;
import com.autoservice.presentation.dto.ordemservico.AdicionarItemServicoRequest;

import java.util.List;

public final class AtendimentoRequestMapper {

    private AtendimentoRequestMapper() {
    }

    public static AbrirAtendimentoCommand toCommand(final AbrirAtendimentoRequest request) {
        return AbrirAtendimentoCommand.with(
                TipoPessoaAtendimento.valueOf(request.tipoPessoa().trim().toUpperCase()),
                request.nome(),
                request.cpf(),
                request.razaoSocial(),
                request.cnpj(),
                request.representanteNome(),
                request.representanteCpf(),
                request.representanteEmail(),
                request.representanteTelefone(),
                request.email(),
                request.telefone(),
                request.placa(),
                request.marca(),
                request.modelo(),
                request.ano(),
                request.cor(),
                request.kilometragem(),
                request.relato(),
                mapItens(request.itens())
        );
    }

    private static List<AbrirAtendimentoItemCommand> mapItens(final List<AdicionarItemServicoRequest> itens) {
        if (itens == null || itens.isEmpty()) {
            return List.of();
        }

        return itens.stream()
                .map(item -> AbrirAtendimentoItemCommand.with(
                        ItemServicoRequestMapper.parseTipo(item.tipo()),
                        item.descricao(),
                        item.pecaId(),
                        item.quantidade(),
                        item.valorUnitario()
                ))
                .toList();
    }
}
