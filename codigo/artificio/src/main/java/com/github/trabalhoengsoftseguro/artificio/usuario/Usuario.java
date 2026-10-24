package com.github.trabalhoengsoftseguro.artificio.usuario;

import com.github.trabalhoengsoftseguro.artificio.auditoria.EntidadeAuditavel;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa os usuários da plataforma (Clientes, Artistas e Administradores).
 */
@Entity
@Table(name = "usuarios")
@Audited
public class Usuario extends EntidadeAuditavel {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(length = 2000)
    private String bio;

    @Column(name = "chave_pix", length = 100)
    private String chavePix;

    @Column(name = "email_confirmado", nullable = false)
    private boolean emailConfirmado = true;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, Role role, String bio, String chavePix) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.bio = bio;
        this.chavePix = chavePix;
    }

    public boolean isArtista() {
        return this.role == Role.ROLE_ARTISTA;
    }

    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }

    public boolean isCliente() {
        return this.role == Role.ROLE_CLIENTE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public boolean isEmailConfirmado() {
        return emailConfirmado;
    }

    public void setEmailConfirmado(boolean emailConfirmado) {
        this.emailConfirmado = emailConfirmado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
