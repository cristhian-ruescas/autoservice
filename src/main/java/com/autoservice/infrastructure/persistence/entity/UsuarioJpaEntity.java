package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.usuario.UsuarioID;
import com.autoservice.infrastructure.persistence.converter.UsuarioIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario", schema = "cadastro")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = UsuarioIdConverter.class)
    private UsuarioID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "role", nullable = false)
    private String role;

    public UsuarioJpaEntity() {
    }

    public UsuarioID getId() { return id; }
    public void setId(UsuarioID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
