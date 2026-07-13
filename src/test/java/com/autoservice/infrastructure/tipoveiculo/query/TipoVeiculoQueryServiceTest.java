package com.autoservice.infrastructure.tipoveiculo.query;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TipoVeiculoQueryService")
class TipoVeiculoQueryServiceTest {

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("Lista tipos de veículo com filtros e paginação")
    void listaComFiltrosEPaginacao() {
        final var service = new TipoVeiculoQueryService(entityManager);
        final var tipo = tipoVeiculo();
        final var tipoEntity = TipoVeiculoMapper.toEntity(tipo);
        final var listQuery = typedQuery(List.of(tipoEntity));
        final var countQuery = typedQuery(1L);

        when(entityManager.createQuery(anyString(), eq(TipoVeiculoJpaEntity.class))).thenReturn(listQuery);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);

        final var output = service.listar(1, 10, " Fiat ", " Uno ", 2015);

        assertEquals(1, output.items().size());
        assertEquals("Fiat", output.items().getFirst().marca());
        assertEquals("Uno", output.items().getFirst().modelo());
        assertEquals(2015, output.items().getFirst().ano());
        assertEquals(1, output.page());
        assertEquals(10, output.size());
        assertEquals(1, output.totalElements());

        verify(listQuery).setParameter("marca", "%fiat%");
        verify(listQuery).setParameter("modelo", "%uno%");
        verify(listQuery).setParameter("ano", 2015);
        verify(listQuery).setFirstResult(10);
        verify(listQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("Busca tipo de veículo por id")
    void buscaPorId() {
        final var service = new TipoVeiculoQueryService(entityManager);
        final var tipo = tipoVeiculo();
        final var query = typedQuery(List.of(TipoVeiculoMapper.toEntity(tipo)));
        when(entityManager.createQuery(anyString(), eq(TipoVeiculoJpaEntity.class))).thenReturn(query);

        final var output = service.buscarPorId(UUID.randomUUID());

        assertEquals("Fiat", output.marca());
        assertEquals("Uno", output.modelo());
        assertEquals(2015, output.ano());
        verify(query).setParameter(eq("id"), anyString());
    }

    @Test
    @DisplayName("Falha quando tipo de veículo não é encontrado")
    void falhaQuandoNaoEncontrado() {
        final var service = new TipoVeiculoQueryService(entityManager);
        final var query = typedQuery(List.<TipoVeiculoJpaEntity>of());
        when(entityManager.createQuery(anyString(), eq(TipoVeiculoJpaEntity.class))).thenReturn(query);

        final var id = UUID.randomUUID();

        final var exception = assertThrows(DomainException.class, () -> service.buscarPorId(id));

        assertEquals("Tipo de veículo não encontrado", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Valida entrada obrigatória e paginação")
    void validaEntradaObrigatoriaEPaginacao() {
        final var service = new TipoVeiculoQueryService(entityManager);

        assertThrows(DomainException.class, () -> service.buscarPorId(null));
        assertThrows(DomainException.class, () -> service.listar(-1, 10, null, null, null));
        assertThrows(DomainException.class, () -> service.listar(0, 0, null, null, null));
        assertThrows(DomainException.class, () -> service.listar(0, 101, null, null, null));
    }

    private static TipoVeiculo tipoVeiculo() {
        return TipoVeiculo.with(
                TipoVeiculoID.unique(),
                Marca.from("Fiat"),
                Modelo.from("Uno"),
                Ano.from(2015)
        );
    }

    private static <T> TypedQuery<T> typedQuery(final List<T> result) {
        final TypedQuery<T> query = mock(TypedQuery.class);
        lenient().when(query.setParameter(anyString(), any())).thenReturn(query);
        lenient().when(query.setFirstResult(anyInt())).thenReturn(query);
        lenient().when(query.setMaxResults(anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(result);
        return query;
    }

    private static <T> TypedQuery<T> typedQuery(final T singleResult) {
        final TypedQuery<T> query = mock(TypedQuery.class);
        lenient().when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(singleResult);
        return query;
    }
}
