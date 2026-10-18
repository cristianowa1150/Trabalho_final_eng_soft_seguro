package com.github.trabalhoengsoftseguro.artificio.catalogo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve carregar pagina inicial publica com catalogo de artistas do seed")
    void deveCarregarPaginaInicialComArtistas() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("artistas"))
                .andExpect(model().attributeExists("categorias"))
                .andExpect(content().string(containsString("Beatriz Silveira")))
                .andExpect(content().string(containsString("Carlos Pixel")));
    }

    @Test
    @DisplayName("Deve filtrar artistas por categoria na busca facetada")
    void deveFiltrarPorCategoria() throws Exception {
        mockMvc.perform(get("/").param("categoria", "Pixel Art"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("categoriaSelecionada", "Pixel Art"))
                .andExpect(content().string(containsString("Carlos Pixel")));
    }
}
