package com.autoservice.infrastructure.estoque.persistence;

import com.autoservice.domain.estoque.Estoque;
import com.autoservice.domain.estoque.EstoqueID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, EstoqueID> {
}
