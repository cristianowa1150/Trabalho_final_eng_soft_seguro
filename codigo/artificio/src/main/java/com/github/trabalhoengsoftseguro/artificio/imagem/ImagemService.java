package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ImagemService {

    private final ImagemBlobRepository repository;
    private final ValidadorUploadImagem validador;

    public ImagemService(ImagemBlobRepository repository, ValidadorUploadImagem validador) {
        this.repository = repository;
        this.validador = validador;
    }

    @Transactional
    public String salvar(byte[] conteudo, String nomeOriginal, String contentType) {
        String mimeSeguro = validador.validarEObterMimeType(conteudo, contentType);

        String id = UUID.randomUUID().toString();
        // Descarta completamente o nomeOriginal fornecido e gera um nome aleatório e seguro
        String nomeSeguro = id + obterExtensaoPorMime(mimeSeguro);

        ImagemBlob blob = new ImagemBlob(id, nomeSeguro, mimeSeguro, conteudo);
        repository.save(blob);
        return id;
    }

    private String obterExtensaoPorMime(String mimeType) {
        return switch (mimeType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default -> ".img";
        };
    }

    public ImagemBlob recuperar(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID da imagem é obrigatório");
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada com ID: " + id));
    }

    @Transactional
    public void remover(String id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
