package com.autoservice.application.tipoveiculo;

import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TipoVeiculoResolver")
class TipoVeiculoResolverTest {

    @Mock
    private TipoVeiculoGateway tipoVeiculoGateway;

    @InjectMocks
    private TipoVeiculoResolver resolver;

    @Test
    void retornaExistenteQuandoJaCadastrado() {
        final var existente = TipoVeiculo.with(TipoVeiculoID.unique(), Marca.from("Toyota"), Modelo.from("Corolla"), Ano.from(2023));
        when(tipoVeiculoGateway.findByMarcaModeloAno("Toyota", "Corolla", 2023)).thenReturn(Optional.of(existente));

        final var resultado = resolver.obterOuCriarComIndicador("Toyota", "Corolla", 2023);

        assertSame(existente, resultado.tipoVeiculo());
        assertFalse(resultado.criado());
    }

    @Test
    void criaNovoQuandoNaoExiste() {
        when(tipoVeiculoGateway.findByMarcaModeloAno("Honda", "Civic", 2022)).thenReturn(Optional.empty());
        when(tipoVeiculoGateway.create(any(TipoVeiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final var resultado = resolver.obterOuCriarComIndicador("Honda", "Civic", 2022);

        assertTrue(resultado.criado());
        verify(tipoVeiculoGateway).create(any(TipoVeiculo.class));
    }
}
