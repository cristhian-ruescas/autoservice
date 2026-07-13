package com.autoservice.domain.ordemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrdemServico")
class OrdemServicoTest {

    @Test
    @DisplayName("Deve testar construtor protegido para JPA")
    void testProtectedConstructor() throws Exception {
        final Constructor<OrdemServico> constructor = OrdemServico.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final OrdemServico os = constructor.newInstance();

        assertNull(os.getId());
        assertNull(os.getVeiculoId());
        assertNull(os.getStatus());
        assertNull(os.getDataCriacao());
        assertNull(os.getRelato());
    }

    @Test
    @DisplayName("Deve criar Ordem de Servico valida via newOrdemServico com valores padroes")
    void deveCriarOrdemServicoValida() {
        final VeiculoID veiculoId = VeiculoID.unique();

        final OrdemServico os = OrdemServico.newOrdemServico(
                veiculoId,
                "Cliente relata barulho ao frear"
        );

        assertNotNull(os);
        assertNotNull(os.getId());
        assertEquals(veiculoId, os.getVeiculoId());
        assertEquals(OrdemServicoStatus.RECEBIDO, os.getStatus());
        assertEquals(LocalDate.now(), os.getDataCriacao().getValue());
        assertEquals("Cliente relata barulho ao frear", os.getRelato());
    }

    @Test
    @DisplayName("Deve falhar ao criar Ordem de Servico com Veiculo nulo")
    void deveFalharVeiculoNuloAoCriar() {
        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.newOrdemServico(null, "Cliente relata barulho ao frear"));

        assertEquals("Veículo ID não deve ser nulo", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Deve reconstruir Ordem de Servico valida via with")
    void deveReconstruirOrdemServico() {
        final OrdemServicoID osID = OrdemServicoID.unique();
        final VeiculoID veiculoId = VeiculoID.unique();
        final OrdemServicoStatus status = OrdemServicoStatus.EM_EXECUCAO;
        final DataCriacao dataCriacao = DataCriacao.from(LocalDate.now().minusDays(2));

        final OrdemServico os = OrdemServico.with(
                osID,
                veiculoId,
                status,
                dataCriacao,
                "Cliente relata falha elétrica"
        );

        assertNotNull(os);
        assertEquals(osID, os.getId());
        assertEquals(veiculoId, os.getVeiculoId());
        assertEquals(status, os.getStatus());
        assertEquals(dataCriacao, os.getDataCriacao());
        assertEquals("Cliente relata falha elétrica", os.getRelato());
    }

    @Test
    @DisplayName("Deve falhar ao reconstruir Ordem de Servico com Veiculo nulo")
    void deveFalharVeiculoNuloAoReconstruir() {
        final OrdemServicoID osID = OrdemServicoID.unique();
        final DataCriacao dataCriacao = DataCriacao.from(LocalDate.now());

        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.with(
                        osID,
                        null,
                        OrdemServicoStatus.RECEBIDO,
                        dataCriacao,
                        "Cliente relata barulho ao frear"
                ));

        assertTrue(exception.getErrors().stream().anyMatch(e -> e.message().equals("Veículo ID não deve ser nulo")));
    }

    @Test
    @DisplayName("Deve falhar ao reconstruir Ordem de Servico com Status nulo")
    void deveFalharStatusNuloAoReconstruir() {
        final OrdemServicoID osID = OrdemServicoID.unique();
        final VeiculoID veiculoId = VeiculoID.unique();
        final DataCriacao dataCriacao = DataCriacao.from(LocalDate.now());

        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.with(
                        osID,
                        veiculoId,
                        null,
                        dataCriacao,
                        "Cliente relata barulho ao frear"
                ));

        assertTrue(exception.getErrors().stream().anyMatch(e -> e.message().equals("Status da ordem de serviço não deve ser nulo")));
    }

    @Test
    @DisplayName("Deve falhar ao reconstruir Ordem de Servico com DataCriacao nula")
    void deveFalharDataCriacaoNulaAoReconstruir() {
        final OrdemServicoID osID = OrdemServicoID.unique();
        final VeiculoID veiculoId = VeiculoID.unique();

        var exception = assertThrows(DomainException.class, () ->
                OrdemServico.with(
                        osID,
                        veiculoId,
                        OrdemServicoStatus.RECEBIDO,
                        null,
                        "Cliente relata barulho ao frear"
                ));

        assertTrue(exception.getErrors().stream().anyMatch(e -> e.message().equals("Data de criação não deve ser nula")));
    }

    @Test
    @DisplayName("Deve iniciar diagnóstico quando Ordem de Servico estiver recebida")
    void deveIniciarDiagnostico() {
        final OrdemServico os = OrdemServico.newOrdemServico(
                VeiculoID.unique(),
                "Cliente relata barulho ao frear"
        );

        os.iniciarDiagnostico();

        assertEquals(OrdemServicoStatus.EM_DIAGNOSTICO, os.getStatus());
    }

    @Test
    @DisplayName("Deve falhar ao iniciar diagnóstico quando Ordem de Servico não estiver recebida")
    void deveFalharAoIniciarDiagnosticoForaDoStatusRecebido() {
        final OrdemServico os = OrdemServico.with(
                OrdemServicoID.unique(),
                VeiculoID.unique(),
                OrdemServicoStatus.EM_EXECUCAO,
                DataCriacao.from(LocalDate.now()),
                "Cliente relata barulho ao frear"
        );

        final var exception = assertThrows(DomainException.class, os::iniciarDiagnostico);

        assertEquals(
                "Ordem de serviço precisa estar RECEBIDO para iniciar diagnóstico",
                exception.getErrors().getFirst().message()
        );
    }
}
