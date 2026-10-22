package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArtistaPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve carregar perfil publico de artista com avaliacoes e comissoes (Anna Letycia)")
    void deveCarregarPerfilPublicoArtistaComAvaliacoes() throws Exception {
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
        mockMvc.perform(get("/artistas/" + anna.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/perfil"))
                .andExpect(model().attributeExists("perfil"))
                .andExpect(content().string(containsString("Anna Letycia")))
                .andExpect(content().string(containsString("Galeria & Portfólio")))
                .andExpect(content().string(containsString("Tabela de Comissões Disponíveis")))
                .andExpect(content().string(containsString("Nota:")));
    }

    @Test
    @DisplayName("Deve carregar perfil publico de artista sem avaliacoes com formatacao segura (Cristiano)")
    void deveCarregarPerfilPublicoArtistaSemAvaliacoes() throws Exception {
        Usuario cristiano = usuarioRepository.findByEmail("cristiano@artificio.local").orElseThrow();
        mockMvc.perform(get("/artistas/" + cristiano.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/perfil"))
                .andExpect(model().attributeExists("perfil"))
                .andExpect(content().string(containsString("Cristiano Silveira")))
                .andExpect(content().string(containsString("Sem avaliações ainda")));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar ID de artista inexistente")
    void deveRetornar404ParaArtistaInexistente() throws Exception {
        mockMvc.perform(get("/artistas/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found se o usuario existir mas nao possuir papel de artista")
    void deveRetornar404ParaUsuarioNaoArtista() throws Exception {
        Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
        mockMvc.perform(get("/artistas/" + eduardo.getId()))
                .andExpect(status().isNotFound());
    }
}
