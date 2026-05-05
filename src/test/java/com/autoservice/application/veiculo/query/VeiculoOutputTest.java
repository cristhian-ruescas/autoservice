package com.autoservice.application.veiculo.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VeiculoOutputTest {

    @Test
    void comProprietario() {
        var prop = new VeiculoOutput.ProprietarioOutput(
                "PJ", "Nome", "111", "Razão", "22333000100", "e@mail", "21999"
        );
        VeiculoOutput v = new VeiculoOutput(
                "vid", "ABC1D23", "Ford", "Ka", 2021, "cinza", 5000, prop
        );
        assertEquals("ABC1D23", v.placa());
        assertEquals(prop, v.proprietario());
        assertEquals("22333000100", v.proprietario().cnpj());
    }
}
