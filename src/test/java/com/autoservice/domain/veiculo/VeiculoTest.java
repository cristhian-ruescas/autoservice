package com.autoservice.domain.veiculo;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Veículo")
class VeiculoTest {

    private Veiculo criarVeiculo() {
        return Veiculo.newVeiculo(
                PessoaID.unique(),
                TipoVeiculoID.unique(),
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(10000)
        );
    }

    @Test
    @DisplayName("Deve criar veículo corretamente usando factory newVeiculo")
    void deveCriarVeiculoComSucesso() {
        Veiculo veiculo = criarVeiculo();

        assertNotNull(veiculo);
        assertNotNull(veiculo.getId());
        assertNotNull(veiculo.getProprietarioId());
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar veículo com dados inválidos pela factory")
    void deveFalharAoCriarVeiculoInvalido() {
        var error = assertThrows(DomainException.class, () -> Veiculo.newVeiculo(
                null,
                TipoVeiculoID.unique(),
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(10000)
        ));
        assertEquals("Proprietário ID não deve ser nulo", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve retornar tipo de veículo corretamente")
    void deveRetornarTipoVeiculoCorretamente() {
        Veiculo veiculo = criarVeiculo();

        assertNotNull(veiculo.getTipoVeiculoId());
    }

    @Test
    @DisplayName("Deve retornar cor corretamente do veículo")
    void deveRetornarCorCorretamente() {
        Veiculo veiculo = criarVeiculo();

        assertEquals(Cor.from("Preto"), veiculo.getCor());
    }

    @Test
    @DisplayName("Deve retornar quilometragem corretamente do veículo")
    void deveRetornarKilometragemCorretamente() {
        Veiculo veiculo = criarVeiculo();

        assertEquals(Kilometragem.from(10000), veiculo.getKilometragem());
    }

    @Test
    @DisplayName("Deve retornar placa corretamente do veículo")
    void deveRetornarPlacaCorretamente() {
        Veiculo veiculo = criarVeiculo();

        assertEquals("ABC1D23", veiculo.getPlaca().getValue());
    }

    @Test
    @DisplayName("Deve reconstruir veículo corretamente usando factory with")
    void deveReconstruirVeiculoComWith() {
        Veiculo original = criarVeiculo();

        Veiculo atualizado = Veiculo.with(
                original.getId(),
                original.getProprietarioId(),
                original.getTipoVeiculoId(),
                original.getPlaca(),
                Cor.from("Branco"),
                Kilometragem.from(5000)
        );

        assertEquals(original.getTipoVeiculoId(), atualizado.getTipoVeiculoId());
        assertEquals(Cor.from("Branco"), atualizado.getCor());
        assertEquals(Kilometragem.from(5000), atualizado.getKilometragem());
    }

    @Test
    @DisplayName("Deve validar veículo com sucesso quando estado é válido")
    void deveValidarVeiculoComSucesso() {
        Veiculo veiculo = criarVeiculo();

        assertDoesNotThrow(() ->
                veiculo.validate(new ThrowsValidationHandler())
        );
    }

    @Test
    @DisplayName("Deve manter consistência entre Value Objects e getters do veículo")
    void deveManterConsistenciaEntreVOsEGetters() {
        Veiculo veiculo = criarVeiculo();

        assertEquals(Placa.from("ABC1D23"), veiculo.getPlaca());
        assertNotNull(veiculo.getTipoVeiculoId());
        assertEquals(Cor.from("Preto"), veiculo.getCor());
        assertEquals(Kilometragem.from(10000), veiculo.getKilometragem());
    }

    @Test
    @DisplayName("Deve sempre criar veículo com ID válido via factory")
    void deveSempreCriarVeiculoComIdValido() {
        Veiculo veiculo = criarVeiculo();

        assertNotNull(veiculo.getId());
        assertNotNull(veiculo.getProprietarioId());
    }

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void deveTestarConstrutorProtegidoParaJPA() throws Exception {
        final Constructor<Veiculo> constructor =
                Veiculo.class.getDeclaredConstructor();

        constructor.setAccessible(true);

        final Veiculo veiculo = constructor.newInstance();

        assertNotNull(veiculo);
        assertNull(veiculo.getId());
        assertNull(veiculo.getProprietarioId());
        assertNull(veiculo.getTipoVeiculoId());
        assertNull(veiculo.getPlaca());
        assertNull(veiculo.getCor());
        assertNull(veiculo.getKilometragem());
    }
}
