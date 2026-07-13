package com.autoservice.infrastructure.veiculo.query;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.domain.veiculo.valueobject.Placa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoQueryService")
class VeiculoQueryServiceTest {

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("Lista veículos com filtros e proprietário pessoa física")
    void listaVeiculosComFiltros() {
        final var service = new VeiculoQueryService(entityManager);
        final var proprietario = pessoaFisica(PessoaID.unique());
        final var tipoVeiculo = tipoVeiculo();
        final var veiculo = veiculo(proprietario.getId(), tipoVeiculo.getId());
        final var listQuery = typedQuery(List.<Object[]>of(new Object[]{veiculo, tipoVeiculo, proprietario, null}));
        final var countQuery = typedQuery(1L);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(listQuery);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);

        final var output = service.listar(0, 10, " Fiat ", " Uno ", 2015, UUID.fromString(proprietario.getId().getValue()));

        assertEquals(1, output.items().size());
        assertEquals("ABC1A23", output.items().getFirst().placa());
        assertEquals("Fiat", output.items().getFirst().marca());
        assertEquals("FISICA", output.items().getFirst().proprietario().tipoPessoa());
        assertEquals("Cliente Um", output.items().getFirst().proprietario().nome());
        assertEquals(1, output.totalElements());

        verify(listQuery).setParameter("marca", "%fiat%");
        verify(listQuery).setParameter("modelo", "%uno%");
        verify(listQuery).setParameter("ano", Ano.from(2015));
        verify(listQuery).setParameter("proprietarioId", proprietario.getId().getValue());
        verify(listQuery).setFirstResult(0);
        verify(listQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("Busca veículo por placa")
    void buscaPorPlaca() {
        final var service = new VeiculoQueryService(entityManager);
        final var empresa = pessoaJuridica(PessoaID.unique());
        final var tipoVeiculo = tipoVeiculo();
        final var veiculo = veiculo(empresa.getId(), tipoVeiculo.getId());
        final var query = typedQuery(List.<Object[]>of(new Object[]{veiculo, tipoVeiculo, null, empresa}));

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        final var output = service.buscarPorPlaca("ABC1A23");

        assertEquals("ABC1A23", output.placa());
        assertEquals("JURIDICA", output.proprietario().tipoPessoa());
        assertEquals("Auto Service LTDA", output.proprietario().razaoSocial());
        verify(query).setParameter(eq("placa"), any(Placa.class));
    }

    @Test
    @DisplayName("Busca veículo por id")
    void buscaPorId() {
        final var service = new VeiculoQueryService(entityManager);
        final var tipoVeiculo = tipoVeiculo();
        final var veiculo = veiculo(PessoaID.unique(), tipoVeiculo.getId());
        final var query = typedQuery(List.<Object[]>of(new Object[]{veiculo, tipoVeiculo, null, null}));

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        final var output = service.buscarPorId(UUID.randomUUID());

        assertEquals("ABC1A23", output.placa());
        assertNull(output.proprietario());
        verify(query).setParameter(eq("id"), anyString());
    }

    @Test
    @DisplayName("Lista veículos por cliente")
    void listaPorCliente() {
        final var service = new VeiculoQueryService(entityManager);
        final var pessoa = PessoaID.unique();
        final var cliente = Cliente.with(ClienteID.unique(), pessoa, LocalDate.of(2024, 1, 10));
        final var tipoVeiculo = tipoVeiculo();
        final var veiculo = veiculo(pessoa, tipoVeiculo.getId());
        final var clienteQuery = typedQuery(List.of(ClienteMapper.toEntity(cliente)));
        final var veiculoQuery = typedQuery(List.<Object[]>of(new Object[]{veiculo, tipoVeiculo, null, null}));

        when(entityManager.createQuery(anyString(), eq(ClienteJpaEntity.class))).thenReturn(clienteQuery);
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(veiculoQuery);

        final var output = service.listarPorCliente(UUID.randomUUID());

        assertEquals(1, output.size());
        assertEquals("ABC1A23", output.getFirst().placa());
        verify(clienteQuery).setParameter(eq("id"), anyString());
        verify(veiculoQuery).setParameter("proprietarioId", pessoa.getValue());
    }

    @Test
    @DisplayName("Falha para entradas obrigatórias e paginação inválida")
    void falhaParaEntradasInvalidas() {
        final var service = new VeiculoQueryService(entityManager);

        assertThrows(DomainException.class, () -> service.buscarPorId(null));
        assertThrows(DomainException.class, () -> service.listarPorCliente(null));
        assertThrows(DomainException.class, () -> service.listar(-1, 10, null, null, null, null));
        assertThrows(DomainException.class, () -> service.listar(0, 0, null, null, null, null));
        assertThrows(DomainException.class, () -> service.listar(0, 101, null, null, null, null));
    }

    @Test
    @DisplayName("Falha quando registros não são encontrados")
    void falhaQuandoNaoEncontrado() {
        final var service = new VeiculoQueryService(entityManager);
        final var emptyObjectQuery = typedQuery(List.<Object[]>of());
        final var emptyClienteQuery = typedQuery(List.<ClienteJpaEntity>of());

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(emptyObjectQuery);
        when(entityManager.createQuery(anyString(), eq(ClienteJpaEntity.class))).thenReturn(emptyClienteQuery);

        final var placaException = assertThrows(DomainException.class, () -> service.buscarPorPlaca("ABC1A23"));
        assertEquals("Veículo não encontrado para a placa informada", placaException.getErrors().getFirst().message());

        final var id = UUID.randomUUID();
        final var idException = assertThrows(DomainException.class, () -> service.buscarPorId(id));
        assertEquals("Veículo não encontrado", idException.getErrors().getFirst().message());

        final var clienteId = UUID.randomUUID();
        final var clienteException = assertThrows(DomainException.class, () -> service.listarPorCliente(clienteId));
        assertEquals("Cliente não encontrado", clienteException.getErrors().getFirst().message());
    }

    private static PessoaFisica pessoaFisica(final PessoaID id) {
        return PessoaFisica.withId(
                id,
                Email.from("cliente@autoservice.local"),
                Telefone.from("11999999999"),
                "Cliente Um",
                CPF.from("52998224725")
        );
    }

    private static PessoaJuridica pessoaJuridica(final PessoaID id) {
        return PessoaJuridica.withId(
                id,
                Email.from("empresa@autoservice.local"),
                Telefone.from("1133334444"),
                "Auto Service LTDA",
                CNPJ.from("11222333000181"),
                PessoaID.unique()
        );
    }

    private static TipoVeiculo tipoVeiculo() {
        return TipoVeiculo.with(
                TipoVeiculoID.unique(),
                Marca.from("Fiat"),
                Modelo.from("Uno"),
                Ano.from(2015)
        );
    }

    private static Veiculo veiculo(final PessoaID proprietarioId, final TipoVeiculoID tipoVeiculoId) {
        return Veiculo.with(
                VeiculoID.unique(),
                proprietarioId,
                tipoVeiculoId,
                Placa.from("ABC1A23"),
                Cor.from("Prata"),
                Kilometragem.from(1000)
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
