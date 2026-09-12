package com.autoservice.domain.veiculo.validators;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlacaValidator")
class PlacaValidatorTest {

    @Test
    @DisplayName("Deve aceitar placa válida no padrão Mercosul")
    void deveAceitarPlacaValidaPadraoMercosul() {
        var placa = Placa.from("ABC1D23");
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new PlacaValidator(placa, handler);
            validator.validate();
        });
    }

    @Test
    @DisplayName("Deve aceitar placa válida no padrão antigo com hífen")
    void deveAceitarPlacaValidaPadraoAntigoComHifen() {
        var placa = Placa.from("ABC-1234");
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new PlacaValidator(placa, handler);
            validator.validate();
        });
    }

    @Test
    @DisplayName("Deve aceitar placa válida no padrão antigo sem hífen")
    void deveAceitarPlacaValidaPadraoAntigoSemHifen() {
        var placa = Placa.from("ABC1234");
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new PlacaValidator(placa, handler);
            validator.validate();
        });
    }

    @Test
    @DisplayName("Deve normalizar placa para letras maiúsculas")
    void deveNormalizarPlacaParaMaiusculas() {
        var placa = Placa.from("abc1d23");
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new PlacaValidator(placa, handler);
            validator.validate();
        });

        assertEquals("ABC1D23", placa.getValue());
    }

    @Test
    @DisplayName("Deve remover espaços em branco da placa")
    void deveRemoverEspacosEmBrancoDaPlaca() {
        var placa = Placa.from("  ABC1D23  ");
        var handler = new ThrowsValidationHandler();

        assertDoesNotThrow(() -> {
            var validator = new PlacaValidator(placa, handler);
            validator.validate();
        });

        assertEquals("ABC1D23", placa.getValue());
    }

    @Test
    @DisplayName("Deve rejeitar se placa (instância) for nula ao inicializar validador")
    void deveRejeitarInstanciaPlacaNula() {
        var handler = new ThrowsValidationHandler();

        var erro = assertThrows(DomainException.class, () -> {
            var validator = new PlacaValidator(null, handler);
            validator.validate();
        });

        assertEquals("Placa não deve ser nula", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar placa cujo valor interno seja nulo")
    void deveRejeitarPlacaValorNulo() {
        var erro = assertThrows(DomainException.class, () -> Placa.from(null));

        assertEquals("Placa não deve ser nula", erro.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve rejeitar placa com formato inválido")
    void deveRejeitarPlacaFormatoInvalido() {
        assertThrows(DomainException.class, () -> {
            Placa.from("XYZ123");
        });
    }

    @Test
    @DisplayName("Deve rejeitar placa iniciando com número")
    void deveRejeitarPlacaIniciandoComNumero() {
        assertThrows(DomainException.class, () -> {
            Placa.from("123ABC1D");
        });
    }

    @Test
    @DisplayName("Deve rejeitar placa com caracteres especiais inválidos")
    void deveRejeitarPlacaComCaracteresEspeciais() {
        assertThrows(DomainException.class, () -> {
            Placa.from("ABC@D23");
        });
    }

    @Test
    @DisplayName("Deve rejeitar placa vazia")
    void deveRejeitarPlacaVazia() {
        assertThrows(DomainException.class, () -> {
            Placa.from("");
        });
    }

    @Test
    @DisplayName("Deve rejeitar placa composta apenas por espaços")
    void deveRejeitarPlacaApenasEspacos() {
        assertThrows(DomainException.class, () -> {
            Placa.from("   ");
        });
    }

    @Test
    @DisplayName("Deve considerar placas iguais quando possuem o mesmo valor")
    void deveConsiderarPlacasIguaisPorValor() {
        var placa1 = Placa.from("ABC1D23");
        var placa2 = Placa.from("ABC1D23");

        assertEquals(placa1, placa2);
    }

    @Test
    @DisplayName("Deve gerar mesmo hashCode para placas iguais")
    void deveGerarMesmoHashCodeParaPlacasIguais() {
        var placa1 = Placa.from("ABC1D23");
        var placa2 = Placa.from("ABC1D23");

        assertEquals(placa1.hashCode(), placa2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar placas diferentes quando valores forem diferentes")
    void deveConsiderarPlacasDiferentes() {
        var placa1 = Placa.from("ABC1D23");
        var placa2 = Placa.from("XYZ1A99");

        assertNotEquals(placa1, placa2);
    }

    @Test
    @DisplayName("Deve retornar valor correto no toString")
    void deveRetornarValorNoToString() {
        var placa = Placa.from("ABC1D23");

        assertEquals("ABC1D23", placa.toString());
    }
}
