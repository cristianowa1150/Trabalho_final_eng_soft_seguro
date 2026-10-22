package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller restrito ao papel de Administrador para supervisão e promoção de usuários.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @PostMapping("/{id}/promover")
    public String promoverParaArtista(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            Usuario promovido = usuarioService.promoverParaArtista(id);
            redirectAttributes.addFlashAttribute("sucesso", "Usuário '" + promovido.getNome() + "' promovido com sucesso para Artista!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
