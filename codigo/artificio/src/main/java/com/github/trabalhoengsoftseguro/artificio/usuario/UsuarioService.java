package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("E-mail não pode ser nulo ou vazio");
        }
        String emailNormalizado = normalizarEmail(email);
        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(new SimpleGrantedAuthority(usuario.getRole().name()))
                .build();
    }

    @Transactional
    public Usuario registrarUsuario(CadastroDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de cadastro não podem ser nulos");
        }
        String emailNormalizado = normalizarEmail(dto.email());
        if (emailNormalizado == null || emailNormalizado.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("E-mail já cadastrado na plataforma");
        }
        if (dto.role() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Não é permitido cadastrar administradores diretamente");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
                dto.nome(),
                emailNormalizado,
                senhaCriptografada,
                dto.role(),
                dto.bio(),
                dto.chavePix()
        );

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail não pode ser nulo ou vazio");
        }
        String emailNormalizado = normalizarEmail(email);
        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para o e-mail: " + email));
    }

    private String normalizarEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarArtistas() {
        return usuarioRepository.findByRoleOrderByNomeAsc(Role.ROLE_ARTISTA);
    }

    @Transactional
    public Usuario promoverParaArtista(Long usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        if (usuario.getRole() != Role.ROLE_CLIENTE) {
            throw new IllegalArgumentException("Apenas contas com papel de Cliente podem ser promovidas a Artista");
        }
        usuario.setRole(Role.ROLE_ARTISTA);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarPerfil(Long usuarioId, String bio, String chavePix) {
        Usuario usuario = buscarPorId(usuarioId);
        usuario.setBio(bio);
        usuario.setChavePix(chavePix);
        return usuarioRepository.save(usuario);
    }
}
