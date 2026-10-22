package com.github.trabalhoengsoftseguro.artificio.config;

import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Inicializador de segurança essencial do Administrador.
 * Executa em qualquer cenário ou perfil (inclusive 'prod').
 * Caso nenhum usuário com ROLE_ADMIN exista, gera uma conta de administrador
 * com senha aleatória criptograficamente segura e imprime em texto claro no console/log.
 */
@Component
@Order(1)
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private static final String CARACTERES_PERMITIDOS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static volatile String senhaAdminGerada;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static String getSenhaAdminGerada() {
        return senhaAdminGerada;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.existsByRole(Role.ROLE_ADMIN)) {
            logger.info("Conta de administrador já existente no banco de dados. Nenhuma ação necessária.");
            return;
        }

        String senhaAleatoria = gerarSenhaAleatoria(16);
        senhaAdminGerada = senhaAleatoria;

        Usuario admin = new Usuario(
                "Administrador da Plataforma",
                "admin@artificio.local",
                passwordEncoder.encode(senhaAleatoria),
                Role.ROLE_ADMIN,
                "Equipe de moderação e governança da plataforma Artifício.",
                null
        );
        usuarioRepository.save(admin);

        imprimirCredenciaisConsole(admin.getEmail(), senhaAleatoria);
    }

    private String gerarSenhaAleatoria(int tamanho) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            int idx = RANDOM.nextInt(CARACTERES_PERMITIDOS.length());
            sb.append(CARACTERES_PERMITIDOS.charAt(idx));
        }
        return sb.toString();
    }

    private void imprimirCredenciaisConsole(String email, String senha) {
        String borda = "=".repeat(78);
        System.out.println();
        System.out.println(borda);
        System.out.println("  [SEGURANÇA] CREDENCIAIS DO ADMINISTRADOR INICIAL GERADAS COM SUCESSO");
        System.out.println(borda);
        System.out.println("  E-mail : " + email);
        System.out.println("  Senha  : " + senha);
        System.out.println("  Papel  : ROLE_ADMIN");
        System.out.println("  Aviso  : Esta senha aleatória foi gerada e exibida apenas uma vez.");
        System.out.println("           Guarde-a em local seguro ou redefina-a após o primeiro acesso.");
        System.out.println(borda);
        System.out.println();

        logger.info("Conta inicial de administrador gerada para o e-mail: {}", email);
    }
}
