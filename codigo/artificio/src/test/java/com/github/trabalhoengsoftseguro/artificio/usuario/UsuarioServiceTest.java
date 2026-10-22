package com.github.trabalhoengsoftseguro.artificio.usuario;

import com.github.trabalhoengsoftseguro.artificio.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenAtivacaoRepository tokenAtivacaoRepository;

    @Mock
    private EmailService emailService;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioService(usuarioRepository, passwordEncoder, tokenAtivacaoRepository, emailService);
    }

    @Test
    @DisplayName("Deve registrar novo usuario criptografando a senha com BCrypt e emitindo token de ativacao")
    void deveRegistrarUsuarioComSucesso() {
        CadastroDto dto = new CadastroDto("Alice", "alice@artificio.local", "senha123", Role.ROLE_CLIENTE, "Bio teste", null);

        when(usuarioRepository.existsByEmail("alice@artificio.local")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario criado = service.registrarUsuario(dto);

        assertNotNull(criado);
        assertEquals("alice@artificio.local", criado.getEmail());
        assertEquals("$2a$10$hashedPassword", criado.getSenha());
        assertEquals(Role.ROLE_CLIENTE, criado.getRole());
        assertFalse(criado.isEmailConfirmado(), "Novo usuario deve iniciar com email nao confirmado");
        verify(usuarioRepository).save(any(Usuario.class));
        verify(tokenAtivacaoRepository).save(any(TokenAtivacao.class));
        verify(emailService).enviarEmailConfirmacao(eq("alice@artificio.local"), eq("Alice"), contains("/confirmar-email?token="));
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com email duplicado sem salvar no banco")
    void deveRejeitarEmailDuplicado() {
        CadastroDto dto = new CadastroDto("Bob", "bob@artificio.local", "senha123", Role.ROLE_ARTISTA, "Bio", "bob@pix");
        when(usuarioRepository.existsByEmail("bob@artificio.local")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.registrarUsuario(dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve normalizar email para minusculo ao verificar duplicidade no cadastro")
    void deveNormalizarEmailAoVerificarDuplicidadeNoCadastro() {
        CadastroDto dto = new CadastroDto("Bob", "  Bob@Artificio.Local  ", "senha123", Role.ROLE_ARTISTA, "Bio", "bob@pix");
        when(usuarioRepository.existsByEmail("bob@artificio.local")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.registrarUsuario(dto));
        verify(usuarioRepository).existsByEmail("bob@artificio.local");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve rejeitar atribuicao direta de papel de administrador no cadastro")
    void deveRejeitarCadastroDiretoDeAdmin() {
        CadastroDto dto = new CadastroDto("Hacker", "hacker@artificio.local", "senha123", Role.ROLE_ADMIN, null, null);

        assertThrows(IllegalArgumentException.class, () -> service.registrarUsuario(dto));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve promover usuario cliente para artista com sucesso")
    void devePromoverClienteParaArtista() {
        Usuario cliente = new Usuario("Leticia", "leticia@artificio.local", "hash", Role.ROLE_CLIENTE, null, null);
        UUID id = UUID.randomUUID();
        cliente.setId(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario promovido = service.promoverParaArtista(id);

        assertEquals(Role.ROLE_ARTISTA, promovido.getRole());
        assertTrue(promovido.isArtista());
        verify(usuarioRepository).save(cliente);
    }

    @Test
    @DisplayName("Deve rejeitar promocao a artista quando usuario for administrador ou ja for artista")
    void deveRejeitarPromocaoParaUsuarioNaoCliente() {
        Usuario admin = new Usuario("Admin", "admin@artificio.local", "hash", Role.ROLE_ADMIN, null, null);
        UUID id = UUID.randomUUID();
        admin.setId(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(admin));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.promoverParaArtista(id));

        assertTrue(ex.getMessage().contains("Apenas contas com papel de Cliente"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve carregar UserDetails pelo email para o Spring Security")
    void deveCarregarUserDetailsPeloEmail() {
        Usuario usuario = new Usuario("Carlos", "carlos@artificio.local", "$2a$10$hash", Role.ROLE_ARTISTA, "Bio", "pix");
        when(usuarioRepository.findByEmail("carlos@artificio.local")).thenReturn(Optional.of(usuario));

        var userDetails = service.loadUserByUsername("carlos@artificio.local");

        assertNotNull(userDetails);
        assertEquals("carlos@artificio.local", userDetails.getUsername());
        assertEquals("$2a$10$hash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ARTISTA")));
    }

    @Test
    @DisplayName("Deve carregar UserDetails com email em caixa mista e espacos")
    void deveCarregarUserDetailsComEmailEmCaixaMistaEEspacos() {
        Usuario usuario = new Usuario("Carlos", "carlos@artificio.local", "$2a$10$hash", Role.ROLE_ARTISTA, "Bio", "pix");
        when(usuarioRepository.findByEmail("carlos@artificio.local")).thenReturn(Optional.of(usuario));

        var userDetails = service.loadUserByUsername("  Carlos@Artificio.LOCAL  ");

        assertNotNull(userDetails);
        assertEquals("carlos@artificio.local", userDetails.getUsername());
        verify(usuarioRepository).findByEmail("carlos@artificio.local");
    }

    @Test
    @DisplayName("Deve lancar UsernameNotFoundException quando email de login for nulo ou vazio")
    void deveLancarUsernameNotFoundExceptionParaEmailNuloOuVazio() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(null));
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("   "));
    }

    @Test
    @DisplayName("Deve buscar usuario por email normalizando caixa alta e espacos")
    void deveBuscarPorEmailComNormalizacao() {
        Usuario usuario = new Usuario("Carlos", "carlos@artificio.local", "$2a$10$hash", Role.ROLE_ARTISTA, "Bio", "pix");
        when(usuarioRepository.findByEmail("carlos@artificio.local")).thenReturn(Optional.of(usuario));

        Usuario encontrado = service.buscarPorEmail("  Carlos@Artificio.LOCAL  ");

        assertNotNull(encontrado);
        assertEquals("carlos@artificio.local", encontrado.getEmail());
        verify(usuarioRepository).findByEmail("carlos@artificio.local");
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar email nulo ou vazio")
    void deveLancarExcecaoParaEmailNuloOuVazioNaBusca() {
        assertThrows(IllegalArgumentException.class, () -> service.buscarPorEmail(null));
        assertThrows(IllegalArgumentException.class, () -> service.buscarPorEmail("   "));
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar id inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        UUID idInexistente = UUID.randomUUID();
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(idInexistente));
    }

    @Test
    @DisplayName("Deve confirmar email com sucesso e remover token apos ativacao")
    void deveConfirmarEmailComSucesso() {
        Usuario usuario = new Usuario("Maria", "maria@artificio.local", "senha", Role.ROLE_CLIENTE, null, null);
        usuario.setEmailConfirmado(false);
        TokenAtivacao token = new TokenAtivacao("token-valido-123", usuario, java.time.LocalDateTime.now().plusHours(1));

        when(tokenAtivacaoRepository.findByToken("token-valido-123")).thenReturn(Optional.of(token));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario ativado = service.confirmarEmail("token-valido-123");

        assertTrue(ativado.isEmailConfirmado());
        verify(usuarioRepository).save(usuario);
        verify(tokenAtivacaoRepository).delete(token);
    }

    @Test
    @DisplayName("Deve rejeitar confirmacao quando token nao for encontrado")
    void deveRejeitarConfirmacaoComTokenInexistente() {
        when(tokenAtivacaoRepository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.confirmarEmail("token-inexistente"));

        assertTrue(ex.getMessage().contains("Token de confirmação inválido"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve rejeitar confirmacao quando token estiver expirado e exclui-lo")
    void deveRejeitarConfirmacaoComTokenExpirado() {
        Usuario usuario = new Usuario("Maria", "maria@artificio.local", "senha", Role.ROLE_CLIENTE, null, null);
        usuario.setEmailConfirmado(false);
        TokenAtivacao tokenExpirado = new TokenAtivacao("token-expirado", usuario, java.time.LocalDateTime.now().minusHours(1));

        when(tokenAtivacaoRepository.findByToken("token-expirado")).thenReturn(Optional.of(tokenExpirado));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.confirmarEmail("token-expirado"));

        assertTrue(ex.getMessage().contains("expirou"));
        verify(tokenAtivacaoRepository).delete(tokenExpirado);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve marcar UserDetails como disabled quando email nao estiver confirmado")
    void deveCarregarUserDetailsDesabilitadoQuandoEmailNaoConfirmado() {
        Usuario usuario = new Usuario("Inativo", "inativo@artificio.local", "$2a$10$hash", Role.ROLE_CLIENTE, null, null);
        usuario.setEmailConfirmado(false);
        when(usuarioRepository.findByEmail("inativo@artificio.local")).thenReturn(Optional.of(usuario));

        var userDetails = service.loadUserByUsername("inativo@artificio.local");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled(), "Usuario com email nao confirmado deve ter isEnabled() == false");
    }
}

