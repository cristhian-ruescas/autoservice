package com.autoservice.infrastructure.tipoveiculo.persistence;

import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVeiculoRepository extends JpaRepository<TipoVeiculoJpaEntity, TipoVeiculoID> {

    @Query("""
            select tipo
            from TipoVeiculoJpaEntity tipo
            where lower(tipo.marca.value) = lower(:marca)
              and lower(tipo.modelo.value) = lower(:modelo)
              and tipo.ano.value = :ano
            """)
    Optional<TipoVeiculoJpaEntity> findByMarcaModeloAno(
            @Param("marca") String marca,
            @Param("modelo") String modelo,
            @Param("ano") Integer ano
    );
}
