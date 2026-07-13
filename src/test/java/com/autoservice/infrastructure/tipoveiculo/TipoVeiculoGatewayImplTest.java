package com.autoservice.infrastructure.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
import com.autoservice.infrastructure.tipoveiculo.persistence.TipoVeiculoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TipoVeiculoGatewayImpl")
class TipoVeiculoGatewayImplTest {

    @Mock
    private TipoVeiculoRepository repository;

    @InjectMocks
    private TipoVeiculoGatewayImpl gateway;

    @Test
    @DisplayName("create delega para o reposit�rio")
    void createDelega() {
        final var tipo = TipoVeiculo.newTipoVeiculo(
                Marca.from("Honda"),
                Modelo.from("Civic"),
                Ano.from(2020));

        final TipoVeiculo saved = gateway.create(tipo);

        assertEquals(tipo.getId(), saved.getId());
        verify(repository).save(any(TipoVeiculoJpaEntity.class));
    }

    @Test
    @DisplayName("findById delega para o reposit�rio")
    void findByIdDelega() {
        final var id = TipoVeiculoID.unique();
        final var tipo = TipoVeiculo.with(id, Marca.from("Honda"), Modelo.from("Fit"), Ano.from(2012));
        when(repository.findById(id.getValue())).thenReturn(Optional.of(TipoVeiculoMapper.toEntity(tipo)));

        final Optional<TipoVeiculo> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(repository).findById(id.getValue());
    }

    @Test
    @DisplayName("findByMarcaModeloAno delega para o reposit�rio")
    void findByMarcaModeloAnoDelega() {
        when(repository.findByMarcaModeloAno("a", "b", Ano.from(2000))).thenReturn(Optional.empty());

        final Optional<TipoVeiculo> result = gateway.findByMarcaModeloAno("a", "b", 2000);

        assertTrue(result.isEmpty());
        verify(repository).findByMarcaModeloAno("a", "b", Ano.from(2000));
    }
}
