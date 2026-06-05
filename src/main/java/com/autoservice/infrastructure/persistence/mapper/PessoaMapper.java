package com.autoservice.infrastructure.persistence.mapper;

import com.autoservice.domain.pessoa.Pessoa;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.infrastructure.persistence.entity.PessoaFisicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJuridicaJpaEntity;

public final class PessoaMapper {

    private PessoaMapper() {
    }

    public static Pessoa toDomain(final PessoaJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof PessoaFisicaJpaEntity pf) {
            return PessoaFisica.withId(pf.getId(), pf.getEmail(), pf.getTelefone(), pf.getNome(), pf.getCpf());
        }
        if (entity instanceof PessoaJuridicaJpaEntity pj) {
            return PessoaJuridica.withId(
                    pj.getId(),
                    pj.getEmail(),
                    pj.getTelefone(),
                    pj.getRazaoSocial(),
                    pj.getCnpj(),
                    pj.getRepresentanteLegalId()
            );
        }
        throw new IllegalArgumentException("Tipo de pessoa JPA desconhecido: " + entity.getClass().getName());
    }

    public static PessoaJpaEntity toEntity(final Pessoa domain) {
        if (domain == null) {
            return null;
        }
        if (domain instanceof PessoaFisica pf) {
            final var entity = new PessoaFisicaJpaEntity();
            entity.setId(pf.getId());
            entity.setEmail(pf.getEmail());
            entity.setTelefone(pf.getTelefone());
            entity.setNome(pf.getNome());
            entity.setCpf(pf.getCpf());
            return entity;
        }
        if (domain instanceof PessoaJuridica pj) {
            final var entity = new PessoaJuridicaJpaEntity();
            entity.setId(pj.getId());
            entity.setEmail(pj.getEmail());
            entity.setTelefone(pj.getTelefone());
            entity.setRazaoSocial(pj.getRazaoSocial());
            entity.setCnpj(pj.getCnpj());
            entity.setRepresentanteLegalId(pj.getRepresentanteLegalId());
            return entity;
        }
        throw new IllegalArgumentException("Tipo de pessoa desconhecido: " + domain.getClass().getName());
    }
}
