package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa uma obra publicada no portfólio de um artista.
 */
@Entity
@Table(name = "obras_portfolio")
public class ObraPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(name = "imagem_id", nullable = false, length = 64)
    private String imagemId;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

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
        this.dataCriacao = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.dataCriacao == null) {
            this.dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
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
