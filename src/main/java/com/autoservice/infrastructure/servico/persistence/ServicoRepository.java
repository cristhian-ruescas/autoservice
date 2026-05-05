package com.autoservice.infrastructure.servico.persistence;

import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, ServicoID> {
}
