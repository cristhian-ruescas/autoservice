package com.autoservice.infrastructure.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
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
    @DisplayName("create delega para o repositório")
    void createDelega() {
        final var tipo = TipoVeiculo.newTipoVeiculo(
                Marca.from("Honda"),
                Modelo.from("Civic"),
                Ano.from(2020));
        when(repository.save(tipo)).thenReturn(tipo);

        final TipoVeiculo saved = gateway.create(tipo);

        assertEquals(tipo.getId(), saved.getId());
        verify(repository).save(tipo);
    }

    @Test
    @DisplayName("findById delega para o repositório")
    void findByIdDelega() {
        final var id = TipoVeiculoID.unique();
        final var tipo = TipoVeiculo.with(id, Marca.from("Honda"), Modelo.from("Fit"), Ano.from(2012));
        when(repository.findById(id)).thenReturn(Optional.of(tipo));

        final Optional<TipoVeiculo> result = gateway.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("findByMarcaModeloAno delega para o repositório")
    void findByMarcaModeloAnoDelega() {
        when(repository.findByMarcaModeloAno("a", "b", 2000)).thenReturn(Optional.empty());

        final Optional<TipoVeiculo> result = gateway.findByMarcaModeloAno("a", "b", 2000);

        assertTrue(result.isEmpty());
        verify(repository).findByMarcaModeloAno("a", "b", 2000);
    }
}
