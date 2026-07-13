package com.autoservice.presentation.dto.ordemservico;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdicionarItensServicoRequest(
        @NotEmpty List<@Valid AdicionarItemServicoRequest> itens
) {
}
