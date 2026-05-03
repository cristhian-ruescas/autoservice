package com.autoservice.infrastructure.veiculo.persistence;

import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, VeiculoID> {
}
