package com.autoservice.presentation.mapper;

import com.autoservice.application.ordemservico.itemservico.AdicionarItemServicoCommand;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.presentation.dto.ordemservico.AdicionarItemServicoRequest;
import com.autoservice.validation.Error;

import java.util.UUID;

public final class ItemServicoRequestMapper {

    private ItemServicoRequestMapper() {
    }

    public static AdicionarItemServicoCommand toCommand(
            final UUID ordemServicoId,
            final AdicionarItemServicoRequest item
    ) {
        return AdicionarItemServicoCommand.with(
                ordemServicoId,
                parseTipo(item.tipo()),
                item.descricao(),
                item.pecaId(),
                item.quantidade(),
                item.valorUnitario()
        );
    }

    public static ItemServicoTipo parseTipo(final String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw DomainException.with(new Error("Tipo do item de serviço não deve ser nulo"));
        }

        try {
            return ItemServicoTipo.valueOf(tipo.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("Tipo do item de serviço deve ser SERVICO ou PECA"));
        }
    }

    public static ItemServicoTipo parseTipoOpcional(final String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return null;
        }

        return parseTipo(tipo);
    }
}
