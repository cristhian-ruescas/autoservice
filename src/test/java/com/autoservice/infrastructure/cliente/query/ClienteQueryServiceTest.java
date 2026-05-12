package com.autoservice.infrastructure.cliente.query;

import com.autoservice.domain.cliente.Cliente;
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
import com.autoservice.domain.veiculo.valueobject.*;
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
@DisplayName("ClienteQueryService")
class ClienteQueryServiceTest {

    @Mock
    private EntityManager entityManager;

    @Test
    @DisplayName("Lista clientes pessoa jurídica com representante legal")
    void listaClientePessoaJuridica() {
        final var service = new ClienteQueryService(entityManager);
        final var representante = pessoaFisica(PessoaID.unique(), "Representante", "52998224725");
        final var pessoaJuridica = pessoaJuridica(PessoaID.unique(), representante.getId());
        final var cliente = Cliente.with(ClienteID.unique(), pessoaJuridica.getId(), LocalDate.of(2024, 1, 10));
        final var listQuery = typedQuery(List.<Object[]>of(new Object[]{cliente, null, pessoaJuridica, representante}));
        final var countQuery = typedQuery(1L);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(listQuery);
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);

        final var output = service.listar(0, 10, " juridica ");

        assertEquals(1, output.items().size());
        assertEquals("JURIDICA", output.items().getFirst().tipoPessoa());
        assertEquals("Auto Service LTDA", output.items().getFirst().razaoSocial());
        assertEquals("11222333000181", output.items().getFirst().cnpj());
        assertEquals("Representante", output.items().getFirst().representanteLegal().nome());
        assertEquals(1, output.totalElements());

        verify(listQuery).setParameter("tipoPessoa", "JURIDICA");
        verify(listQuery).setFirstResult(0);
        verify(listQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("Busca cliente por CPF")
    void buscaPorCpf() {
        final var service = new ClienteQueryService(entityManager);
        final var pessoa = pessoaFisica(PessoaID.unique(), "Cliente Um", "52998224725");
        final var cliente = Cliente.with(ClienteID.unique(), pessoa.getId(), LocalDate.of(2024, 1, 11));
        final var query = typedQuery(List.<Object[]>of(new Object[]{cliente, pessoa, null, null}));
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        final var output = service.buscarPorCpf("529.982.247-25");

        assertEquals("FISICA", output.tipoPessoa());
        assertEquals("Cliente Um", output.nome());
        assertEquals("52998224725", output.cpf());
        verify(query).setParameter(eq("cpf"), any(CPF.class));
    }

    @Test
    @DisplayName("Busca detalhe do cliente com veículos")
    void buscaDetalheComVeiculos() {
        final var service = new ClienteQueryService(entityManager);
        final var pessoa = pessoaFisica(PessoaID.unique(), "Cliente Um", "52998224725");
        final var cliente = Cliente.with(ClienteID.unique(), pessoa.getId(), LocalDate.of(2024, 1, 11));
        final var tipoVeiculo = tipoVeiculo();
        final var veiculo = veiculo(pessoa.getId(), tipoVeiculo.getId());
        final var clienteQuery = typedQuery(List.<Object[]>of(new Object[]{cliente, pessoa, null, null}));
        final var veiculoQuery = typedQuery(List.<Object[]>of(new Object[]{veiculo, tipoVeiculo}));

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(clienteQuery, veiculoQuery);

        final var output = service.buscarPorId(UUID.randomUUID());

        assertEquals("Cliente Um", output.nome());
        assertEquals(1, output.veiculos().size());
        assertEquals("ABC1A23", output.veiculos().getFirst().placa());
        assertEquals("Fiat", output.veiculos().getFirst().marca());
        verify(clienteQuery).setParameter(eq("id"), any(ClienteID.class));
        verify(veiculoQuery).setParameter("proprietarioId", pessoa.getId());
    }

    @Test
    @DisplayName("Falha para filtros e resultados inválidos")
    void falhaParaFiltrosEResultadosInvalidos() {
        final var service = new ClienteQueryService(entityManager);
        final var query = typedQuery(List.<Object[]>of());
        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(query);

        assertThrows(DomainException.class, () -> service.listar(-1, 10, null));
        assertThrows(DomainException.class, () -> service.listar(0, 0, null));
        assertThrows(DomainException.class, () -> service.listar(0, 101, null));
        assertThrows(DomainException.class, () -> service.listar(0, 10, "outro"));
        assertThrows(DomainException.class, () -> service.buscarPorId(null));

        final var notFound = assertThrows(DomainException.class, () -> service.buscarPorCpf("52998224725"));
        assertEquals("Cliente não encontrado para o CPF informado", notFound.getErrors().getFirst().message());
    }

    @Test
    @DisplayName("Falha quando cliente aponta para pessoa inexistente")
    void falhaQuandoPessoaDoClienteNaoExiste() {
        final var service = new ClienteQueryService(entityManager);
        final var cliente = Cliente.with(ClienteID.unique(), PessoaID.unique(), LocalDate.of(2024, 1, 11));
        final var listQuery = typedQuery(List.<Object[]>of(new Object[]{cliente, null, null, null}));

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(listQuery);

        final var exception = assertThrows(DomainException.class, () -> service.listar(0, 10, null));

        assertEquals("Pessoa do cliente não encontrada", exception.getErrors().getFirst().message());
    }

    private static PessoaFisica pessoaFisica(final PessoaID id, final String nome, final String cpf) {
        return PessoaFisica.withId(
                id,
                Email.from(nome.replace(" ", ".").toLowerCase() + "@autoservice.local"),
                Telefone.from("11999999999"),
                nome,
                CPF.from(cpf)
        );
    }

    private static PessoaJuridica pessoaJuridica(final PessoaID id, final PessoaID representanteId) {
        return PessoaJuridica.withId(
                id,
                Email.from("empresa@autoservice.local"),
                Telefone.from("1133334444"),
                "Auto Service LTDA",
                CNPJ.from("11222333000181"),
                representanteId
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
