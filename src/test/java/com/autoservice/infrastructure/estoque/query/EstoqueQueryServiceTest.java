package com.autoservice.infrastructure.estoque.query;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstoqueQueryService")
class EstoqueQueryServiceTest {

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("Lista estoques com peça vinculada")
    void listaEstoquesComPeca() {
        final var service = new EstoqueQueryService(entityManager);
        final var estoque = estoque();
        final var peca = peca(estoque.getId());
        final var listQuery = typedQuery(List.<Object[]>of(new Object[]{estoque, peca}));
        final var countQuery = typedQuery(1L);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(listQuery);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);

        final var output = service.listar(0, 20);

        assertEquals(1, output.items().size());
        assertEquals(25, output.items().getFirst().quantidadeDisponivel());
        assertEquals("A1", output.items().getFirst().localizacao());
        assertEquals("FILTRO-001", output.items().getFirst().peca().codigo());
        assertEquals(1, output.totalElements());
        verify(listQuery).setFirstResult(0);
        verify(listQuery).setMaxResults(20);
    }

    @Test
    @DisplayName("Detalha estoque sem peça vinculada")
    void detalhaEstoqueSemPeca() {
        final var service = new EstoqueQueryService(entityManager);
        final var estoque = estoque();
        final var query = typedQuery(List.<Object[]>of(new Object[]{estoque, null}));
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        final var output = service.detalhar(UUID.randomUUID());

        assertEquals(25, output.quantidadeDisponivel());
        assertNull(output.peca());
        verify(query).setParameter(eq("id"), anyString());
    }

    @Test
    @DisplayName("Falha quando estoque não é encontrado")
    void falhaQuandoNaoEncontrado() {
        final var service = new EstoqueQueryService(entityManager);
        final var query = typedQuery(List.<Object[]>of());
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        final var id = UUID.randomUUID();

        final var exception = assertThrows(DomainException.class, () -> service.detalhar(id));

        assertEquals("Estoque não encontrado", exception.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Valida paginação")
    void validaPaginacao() {
        final var service = new EstoqueQueryService(entityManager);

        assertThrows(DomainException.class, () -> service.listar(-1, 20));
        assertThrows(DomainException.class, () -> service.listar(0, 0));
        assertThrows(DomainException.class, () -> service.listar(0, 101));
    }

    private static Estoque estoque() {
        return Estoque.with(EstoqueID.unique(), 25, 5, "A1");
    }

    private static Peca peca(final EstoqueID estoqueId) {
        return Peca.with(
                PecaID.unique(),
                "Filtro de óleo",
                "FILTRO-001",
                "Bosch",
                BigDecimal.TEN,
                estoqueId,
                TipoVeiculoID.unique()
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
        when(query.getSingleResult()).thenReturn(singleResult);
        return query;
    }
}
