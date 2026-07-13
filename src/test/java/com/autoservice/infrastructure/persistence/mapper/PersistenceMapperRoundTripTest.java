package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemcompra.ItemOrdemCompra;
import com.autoservice.domain.ordemcompra.ItemOrdemCompraID;
import com.autoservice.domain.ordemcompra.OrdemCompra;
import com.autoservice.domain.ordemcompra.OrdemCompraID;
import com.autoservice.domain.ordemcompra.enums.OrdemCompraStatus;
import com.autoservice.domain.ordemcompra.valueobject.DataCompra;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.pessoa.valueobject.CNPJ;
import com.autoservice.domain.pessoa.valueobject.CPF;
import com.autoservice.domain.pessoa.valueobject.Email;
import com.autoservice.domain.pessoa.valueobject.Telefone;
import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoID;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.usuario.Usuario;
import com.autoservice.domain.usuario.UsuarioID;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.domain.veiculo.valueobject.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Persistence mappers")
class PersistenceMapperRoundTripTest {

    @Test
    void ordemServicoMapperRoundTrip() {
        final var id = OrdemServicoID.unique();
        final var domain = OrdemServico.with(
                id,
                VeiculoID.unique(),
                OrdemServicoStatus.EM_EXECUCAO,
                DataCriacao.from(LocalDate.now()),
                "Relato",
                1,
                2,
                LocalDateTime.now().minusHours(1),
                null
        );

        final var roundTrip = OrdemServicoMapper.toDomain(OrdemServicoMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getStatus(), roundTrip.getStatus());
        assertEquals(domain.getRelato(), roundTrip.getRelato());
        assertEquals(domain.getTempoPrevistoExecucaoDias(), roundTrip.getTempoPrevistoExecucaoDias());
        assertNull(OrdemServicoMapper.toDomain(null));
        assertNull(OrdemServicoMapper.toEntity(null));
    }

    @Test
    void clienteMapperRoundTrip() {
        final var domain = Cliente.with(ClienteID.unique(), PessoaID.unique(), LocalDate.now());
        final var roundTrip = ClienteMapper.toDomain(ClienteMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getPessoaId(), roundTrip.getPessoaId());
        assertNull(ClienteMapper.toDomain(null));
    }

    @Test
    void veiculoMapperRoundTrip() {
        final var domain = Veiculo.with(
                VeiculoID.unique(),
                PessoaID.unique(),
                TipoVeiculoID.unique(),
                Placa.from("ABC1D23"),
                Cor.from("Preto"),
                Kilometragem.from(10000)
        );
        final var roundTrip = VeiculoMapper.toDomain(VeiculoMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getPlaca(), roundTrip.getPlaca());
        assertNull(VeiculoMapper.toEntity(null));
    }

    @Test
    void pessoaFisicaMapperRoundTrip() {
        final var domain = PessoaFisica.withId(
                PessoaID.unique(),
                Email.from("pessoa@email.com"),
                Telefone.from("11999999999"),
                "Joao Silva",
                CPF.from("52998224725")
        );
        final var roundTrip = PessoaMapper.toDomain(PessoaMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getNome(), ((PessoaFisica) roundTrip).getNome());
    }

    @Test
    void pessoaJuridicaMapperRoundTrip() {
        final var domain = PessoaJuridica.withId(
                PessoaID.unique(),
                Email.from("empresa@email.com"),
                Telefone.from("11999999999"),
                "Empresa Ltda",
                CNPJ.from("11222333000181"),
                PessoaID.unique()
        );
        final var roundTrip = PessoaMapper.toDomain(PessoaMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getRazaoSocial(), ((PessoaJuridica) roundTrip).getRazaoSocial());
    }

    @Test
    void itemServicoMapperRoundTrip() {
        final var domain = ItemServico.with(
                ItemServicoID.unique(),
                OrdemServicoID.unique(),
                ItemServicoTipo.PECA,
                "Filtro",
                PecaID.unique(),
                2,
                new BigDecimal("25.00")
        );
        final var roundTrip = ItemServicoMapper.toDomain(ItemServicoMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getQuantidade(), roundTrip.getQuantidade());
    }

    @Test
    void pecaMapperRoundTrip() {
        final var domain = Peca.with(
                PecaID.unique(),
                "Filtro de oleo",
                "FO-1",
                "Mann",
                new BigDecimal("45.00"),
                EstoqueID.unique(),
                TipoVeiculoID.unique()
        );
        final var roundTrip = PecaMapper.toDomain(PecaMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getCodigo(), roundTrip.getCodigo());
    }

    @Test
    void estoqueMapperRoundTrip() {
        final var domain = Estoque.with(EstoqueID.unique(), 10, 2, "Prateleira A1");
        final var roundTrip = EstoqueMapper.toDomain(EstoqueMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getQuantidadeDisponivel(), roundTrip.getQuantidadeDisponivel());
    }

    @Test
    void servicoMapperRoundTrip() {
        final var domain = Servico.with(
                ServicoID.unique(),
                "Alinhamento",
                "Alinhamento completo",
                new BigDecimal("120.00")
        );
        final var roundTrip = ServicoMapper.toDomain(ServicoMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getNome(), roundTrip.getNome());
    }

    @Test
    void tipoVeiculoMapperRoundTrip() {
        final var domain = TipoVeiculo.with(
                TipoVeiculoID.unique(),
                Marca.from("Toyota"),
                Modelo.from("Corolla"),
                Ano.from(2023)
        );
        final var roundTrip = TipoVeiculoMapper.toDomain(TipoVeiculoMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getMarca(), roundTrip.getMarca());
    }

    @Test
    void ordemCompraMapperRoundTrip() {
        final var domain = OrdemCompra.with(
                OrdemCompraID.unique(),
                OrdemCompraStatus.REALIZADO,
                DataCompra.from(LocalDate.now())
        );
        final var roundTrip = OrdemCompraMapper.toDomain(OrdemCompraMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getStatus(), roundTrip.getStatus());
    }

    @Test
    void itemOrdemCompraMapperRoundTrip() {
        final var domain = ItemOrdemCompra.with(
                ItemOrdemCompraID.unique(),
                OrdemCompraID.unique(),
                PecaID.unique(),
                3
        );
        final var roundTrip = ItemOrdemCompraMapper.toDomain(ItemOrdemCompraMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getQuantidade(), roundTrip.getQuantidade());
    }

    @Test
    void usuarioMapperRoundTrip() {
        final var domain = new Usuario(UsuarioID.unique(), "admin@autoservice.com", "hash", "ROLE_ADMIN");
        final var roundTrip = UsuarioMapper.toDomain(UsuarioMapper.toEntity(domain));

        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getEmail(), roundTrip.getEmail());
        assertEquals(domain.getRole(), roundTrip.getRole());
    }
}
