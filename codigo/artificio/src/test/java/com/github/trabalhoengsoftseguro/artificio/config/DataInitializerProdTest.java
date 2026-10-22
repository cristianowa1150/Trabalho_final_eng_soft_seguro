package com.github.trabalhoengsoftseguro.artificio.config;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:proddb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
class DataInitializerProdTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObraPortfolioRepository obraPortfolioRepository;

    @Autowired
    private PedidoComissaoRepository pedidoComissaoRepository;

    @Test
    @DisplayName("Em perfil prod, deve inicializar admin com papel ROLE_ADMIN mas nao deve carregar dados demo")
    void deveInicializarApenasAdminEmProd() {
        // Admin deve existir em prod
        assertTrue(usuarioRepository.existsByRole(Role.ROLE_ADMIN),
                "O Administrador inicial deve ser criado em qualquer perfil, inclusive prod.");

        // Usuários demo NÃO devem existir em prod
        assertFalse(usuarioRepository.existsByEmail("anna@artificio.local"),
                "Usuários demo de artistas não devem ser carregados em perfil prod.");
        assertFalse(usuarioRepository.existsByEmail("eduardo@artificio.local"),
                "Usuários demo de clientes não devem ser carregados em perfil prod.");

        // Obras e pedidos demo NÃO devem existir em prod
        assertEquals(0, obraPortfolioRepository.count(),
                "Obras de demonstração não devem ser carregadas em perfil prod.");
        assertEquals(0, pedidoComissaoRepository.count(),
                "Pedidos de demonstração não devem ser carregados em perfil prod.");
    }
}
