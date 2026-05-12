package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VeiculoValidator")
class VeiculoValidatorTest {

    private VeiculoID veiculoId;
    private PessoaID proprietarioId;
    private TipoVeiculoID tipoVeiculoId;
    private Placa placa;
    private Cor cor;
    private Kilometragem kilometragem;

    @BeforeEach
    void setup() {
        veiculoId = VeiculoID.unique();
        proprietarioId = PessoaID.unique();
        tipoVeiculoId = TipoVeiculoID.unique();
        placa = Placa.from("ABC1D23");
        cor = Cor.from("Preto");
        kilometragem = Kilometragem.from(10000);
    }

    @Test
    @DisplayName("Deve validar veículo com dados válidos")
    void deveValidarVeiculoComDadosValidos() {
        var veiculo = Veiculo.with(veiculoId, proprietarioId, tipoVeiculoId, placa, cor, kilometragem);
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });
    }

    @Test
    @DisplayName("Deve rejeitar veículo com proprietário nulo")
    void deveRejeitarVeiculoComProprietarioNulo() {
        var veiculo = Veiculo.with(veiculoId, null, tipoVeiculoId, placa, cor, kilometragem);
        var handler = new ThrowsValidationHandler();

        var error = assertThrows(DomainException.class, () -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });

        assertEquals("Proprietário ID não deve ser nulo", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar veículo com tipo de veículo nulo")
    void deveRejeitarVeiculoComTipoVeiculoNulo() {
        var veiculo = Veiculo.with(veiculoId, proprietarioId, null, placa, cor, kilometragem);
        var handler = new ThrowsValidationHandler();

        var error = assertThrows(DomainException.class, () -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });

        assertEquals("Tipo de veículo ID não deve ser nulo", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar veículo com placa nula")
    void deveRejeitarVeiculoComPlacaNula() {
        var veiculo = Veiculo.with(veiculoId, proprietarioId, tipoVeiculoId, null, cor, kilometragem);
        var handler = new ThrowsValidationHandler();

        var error = assertThrows(DomainException.class, () -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });

        assertEquals("Placa não deve ser nula", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar veículo com cor nula")
    void deveRejeitarVeiculoComCorNula() {
        var veiculo = Veiculo.with(veiculoId, proprietarioId, tipoVeiculoId, placa, null, kilometragem);
        var handler = new ThrowsValidationHandler();

        var error = assertThrows(DomainException.class, () -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });

        assertEquals("Cor do veículo não deve ser nula", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar veículo com kilometragem nula")
    void deveRejeitarVeiculoComKilometragemNula() {
        var veiculo = Veiculo.with(veiculoId, proprietarioId, tipoVeiculoId, placa, cor, null);
        var handler = new ThrowsValidationHandler();

        var error = assertThrows(DomainException.class, () -> {
            var validator = new VeiculoValidator(veiculo, handler);
            validator.validate();
        });

        assertEquals("Kilometragem não deve ser nula", error.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve validar criação de veículo pela factory")
    void deveValidarFactoryNewVeiculo() {
        var veiculo = Veiculo.newVeiculo(proprietarioId, tipoVeiculoId, placa, cor, kilometragem);

        assertNotNull(veiculo);
        assertNotNull(veiculo.getId());
        assertEquals(proprietarioId, veiculo.getProprietarioId());
        assertEquals(tipoVeiculoId, veiculo.getTipoVeiculoId());
        assertEquals(placa, veiculo.getPlaca());
        assertEquals(cor, veiculo.getCor());
        assertEquals(kilometragem, veiculo.getKilometragem());
    }
}
