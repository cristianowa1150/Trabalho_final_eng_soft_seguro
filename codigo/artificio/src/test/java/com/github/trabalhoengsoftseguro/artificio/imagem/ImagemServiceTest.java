package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagemServiceTest {

    @Mock
    private ImagemBlobRepository repository;

    private ImagemService service;

    @BeforeEach
    void setUp() {
        service = new ImagemService(repository);
    }

    @Test
    @DisplayName("Deve salvar imagem binaria com sucesso gerando UUID")
    void deveSalvarImagemComSucesso() {
        byte[] dados = "pixel-data".getBytes();
        when(repository.save(any(ImagemBlob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String idGerado = service.salvar(dados, "arte.png", "image/png");

        assertNotNull(idGerado);
        assertFalse(idGerado.isBlank());
        verify(repository).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar conteudo vazio sem chamar o repositorio")
    void deveRejeitarConteudoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(new byte[0], "vazio.png", "image/png"));

        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve recuperar imagem existente por ID")
    void deveRecuperarImagemExistente() {
        ImagemBlob blob = new ImagemBlob("uuid-123", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(repository.findById("uuid-123")).thenReturn(Optional.of(blob));

        ImagemBlob recuperado = service.recuperar("uuid-123");

        assertEquals("uuid-123", recuperado.getId());
        assertEquals("image/jpeg", recuperado.getContentType());
        verify(repository).findById("uuid-123");
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar recuperar ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.recuperar("inexistente"));
    }
}
