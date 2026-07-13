package com.autoservice.infrastructure.ordemservico;

import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.infrastructure.ordemservico.persistence.OrdemServicoRepository;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrdemServicoGatewayImpl")
class OrdemServicoGatewayImplTest {

    @Mock
    private OrdemServicoRepository repository;

    @InjectMocks
    private OrdemServicoGatewayImpl gateway;

    @Test
    @DisplayName("Deve salvar uma ordem de servico corretamente")
    void deveSalvarOrdemServico() {
        final VeiculoID veiculoId = VeiculoID.unique();
        final OrdemServico os = OrdemServico.newOrdemServico(
                veiculoId,
                "Cliente relata barulho ao frear"
        );

        final OrdemServico saved = gateway.create(os);

        assertNotNull(saved);
        assertEquals(os.getId(), saved.getId());
        verify(repository, times(1)).save(any(OrdemServicoJpaEntity.class));
    }
}
