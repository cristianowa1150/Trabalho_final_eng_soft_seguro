package com.github.trabalhoengsoftseguro.artificio.usuario;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade para persistência de tokens temporários de confirmação e ativação de contas.
 */
@Entity
@Table(name = "tokens_ativacao")
public class TokenAtivacao {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    public TokenAtivacao() {
    }

    public TokenAtivacao(String token, Usuario usuario, LocalDateTime dataExpiracao) {
        this.token = token;
        this.usuario = usuario;
        this.dataExpiracao = dataExpiracao;
    }

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }
}
