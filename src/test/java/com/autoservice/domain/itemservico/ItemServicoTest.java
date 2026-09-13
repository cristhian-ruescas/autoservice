package com.autoservice.domain.itemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.peca.PecaID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemServico")
class ItemServicoTest {

    @Test
    @DisplayName("Construtor protegido para JPA")
    void construtorProtegidoJpa() throws Exception {
        final Constructor<ItemServico> c = ItemServico.class.getDeclaredConstructor();
        c.setAccessible(true);
        final ItemServico is = c.newInstance();
        assertNull(is.getId());
    }

    @Test
    @DisplayName("newServico cria item válido")
    void newServicoValido() {
        final OrdemServicoID osId = OrdemServicoID.unique();
        final ItemServico item = ItemServico.newServico(osId, "Troca de pastilhas", new BigDecimal("120.50"));

        assertEquals(ItemServicoTipo.SERVICO, item.getTipo());
        assertEquals(osId, item.getOrdemServicoId());
        assertNull(item.getPecaId());
        assertEquals(1, item.getQuantidade());
        assertEquals(0, item.getValorTotal().compareTo(new BigDecimal("120.50")));
    }

    @Test
    @DisplayName("newPeca cria item com peça vinculada")
    void newPecaValido() {
        final OrdemServicoID osId = OrdemServicoID.unique();
        final PecaID pecaId = PecaID.unique();
        final ItemServico item = ItemServico.newPeca(
                osId, "Filtro de óleo", pecaId, 3, new BigDecimal("10.00"));

        assertEquals(ItemServicoTipo.PECA, item.getTipo());
        assertEquals(pecaId, item.getPecaId());
        assertEquals(3, item.getQuantidade());
        assertEquals(0, item.getValorTotal().compareTo(new BigDecimal("30.00")));
    }

    @Test
    @DisplayName("with reconstrói agregado")
    void withReconstroi() {
        final ItemServicoID id = ItemServicoID.unique();
        final OrdemServicoID osId = OrdemServicoID.unique();
        final ItemServico item = ItemServico.with(
                id, osId, ItemServicoTipo.SERVICO, "Alinhamento", null, 1, new BigDecimal("80"));

        assertEquals(id, item.getId());
    }

    @Test
    @DisplayName("Falha quando ordem de serviço nula")
    void falhaOrdemNula() {
        assertThrows(DomainException.class, () ->
                ItemServico.newServico(null, "x", BigDecimal.ONE));
    }

    @Test
    @DisplayName("Falha quando descrição vazia")
    void falhaDescricaoVazia() {
        assertThrows(DomainException.class, () ->
                ItemServico.newServico(OrdemServicoID.unique(), "  ", BigDecimal.ONE));
    }

    @Test
    @DisplayName("Falha quando quantidade inválida")
    void falhaQuantidade() {
        assertThrows(DomainException.class, () ->
                ItemServico.with(
                        ItemServicoID.unique(),
                        OrdemServicoID.unique(),
                        ItemServicoTipo.PECA,
                        "x",
                        PecaID.unique(),
                        0,
                        BigDecimal.ONE));
    }

    @Test
    @DisplayName("Falha quando PECA sem peça")
    void falhaPecaSemId() {
        assertThrows(DomainException.class, () ->
                ItemServico.newPeca(
                        OrdemServicoID.unique(),
                        "x",
                        null,
                        1,
                        BigDecimal.ONE));
    }

    @Test
    @DisplayName("Falha quando SERVICO com peça vinculada")
    void falhaServicoComPeca() {
        assertThrows(DomainException.class, () ->
                ItemServico.with(
                        ItemServicoID.unique(),
                        OrdemServicoID.unique(),
                        ItemServicoTipo.SERVICO,
                        "x",
                        PecaID.unique(),
                        1,
                        BigDecimal.ONE));
    }
}
