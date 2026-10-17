package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(CadastroDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados de cadastro não podem ser nulos");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado na plataforma");
        }
        if (dto.role() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Não é permitido cadastrar administradores diretamente");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
                dto.nome(),
                dto.email().trim().toLowerCase(),
                senhaCriptografada,
                dto.role(),
                dto.bio(),
                dto.chavePix()
        );

        return usuarioRepository.save(novoUsuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para o e-mail: " + email));
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
