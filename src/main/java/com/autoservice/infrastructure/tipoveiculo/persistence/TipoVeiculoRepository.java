package com.autoservice.infrastructure.tipoveiculo.persistence;

import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVeiculoRepository extends JpaRepository<TipoVeiculoJpaEntity, String> {

    @Query("""
            select tipo
            from TipoVeiculoJpaEntity tipo
            where lower(tipo.marca) = lower(:marca)
              and lower(tipo.modelo) = lower(:modelo)
              and tipo.ano = :ano
            """)
    Optional<TipoVeiculoJpaEntity> findByMarcaModeloAno(
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("ano") Ano ano
    );
}
