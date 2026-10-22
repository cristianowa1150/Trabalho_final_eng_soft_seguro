package com.github.trabalhoengsoftseguro.artificio.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class GlobalModelAdviceProdTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Nao deve exibir credenciais demo nem modal de teste quando em perfil prod")
    void naoDeveExibirCredenciaisDemoEmProd() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("exibirCredenciaisDemo", false))
                .andExpect(content().string(not(containsString("Acesso Rápido para Testes"))))
                .andExpect(content().string(not(containsString("btnCredenciaisDemo"))));
    }
}
