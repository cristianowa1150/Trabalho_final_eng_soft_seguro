package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ImagemService {

    private final ImagemBlobRepository repository;

    public ImagemService(ImagemBlobRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String salvar(byte[] conteudo, String nomeOriginal, String contentType) {
        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException("O arquivo de imagem não pode estar vazio");
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String id = UUID.randomUUID().toString();
        ImagemBlob blob = new ImagemBlob(id, nomeOriginal != null ? nomeOriginal : "sem-nome", contentType, conteudo);
        repository.save(blob);
        return id;
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
