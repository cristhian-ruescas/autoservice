package com.autoservice.infrastructure.peca.persistence;

import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PecaRepository extends JpaRepository<Peca, PecaID> {
}
