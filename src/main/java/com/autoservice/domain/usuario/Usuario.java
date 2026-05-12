package com.autoservice.domain.usuario;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.validation.ValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario", schema = "cadastro")
public class Usuario extends AggregateRoot<UsuarioID> {
    @EmbeddedId
    private UsuarioID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "role", nullable = false)
    private String role;

    protected Usuario() {
        super();
    }

    public Usuario(UsuarioID id, String email, String senha, String role) {
        super(id);
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    public static Usuario newUsuario(String email, String senha, String role) {
        return new Usuario(UsuarioID.unique(), email, senha, role);
    }

    @Override
    public UsuarioID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getRole() {
        return role;
    }

    @Override
    public void validate(ValidationHandler handler) {
    }
}
