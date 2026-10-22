package com.github.trabalhoengsoftseguro.artificio.config;

import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        adminInitializer = new AdminInitializer(usuarioRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Deve gerar senha aleatoria de 16 caracteres e persistir admin quando nao houver admin")
    void deveCriarAdminQuandoNaoExistir() {
        when(usuarioRepository.existsByRole(Role.ROLE_ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "hash_" + invocation.getArgument(0));

        adminInitializer.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario adminSalvo = captor.getValue();
        assertEquals("admin@artificio.local", adminSalvo.getEmail());
        assertEquals("Administrador da Plataforma", adminSalvo.getNome());
        assertEquals(Role.ROLE_ADMIN, adminSalvo.getRole());
        assertTrue(adminSalvo.getSenha().startsWith("hash_"));

        assertNotNull(AdminInitializer.getSenhaAdminGerada());
        assertEquals(16, AdminInitializer.getSenhaAdminGerada().length());
    }

    @Test
    @DisplayName("Nao deve recriar admin quando ja existir um usuario com ROLE_ADMIN")
    void naoDeveCriarAdminSeJaExistir() {
        when(usuarioRepository.existsByRole(Role.ROLE_ADMIN)).thenReturn(true);

        adminInitializer.run();

        verify(usuarioRepository, never()).save(any());
    }
}
