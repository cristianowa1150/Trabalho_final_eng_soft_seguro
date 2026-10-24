package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.auditoria.EntidadeAuditavel;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa uma obra publicada no portfólio de um artista.
 */
@Entity
@Table(name = "obras_portfolio")
@Audited
public class ObraPortfolio extends EntidadeAuditavel {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(name = "imagem_id", nullable = false, length = 64)
    private String imagemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Usuario artista;

    public ObraPortfolio() {
    }

    public ObraPortfolio(String titulo, String descricao, String categoria, String imagemId, Usuario artista) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.imagemId = imagemId;
        this.artista = artista;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagemId() {
        return imagemId;
    }

    public void setImagemId(String imagemId) {
        this.imagemId = imagemId;
    }

    public Usuario getArtista() {
        return artista;
    }

    public void setArtista(Usuario artista) {
        this.artista = artista;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObraPortfolio that = (ObraPortfolio) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
