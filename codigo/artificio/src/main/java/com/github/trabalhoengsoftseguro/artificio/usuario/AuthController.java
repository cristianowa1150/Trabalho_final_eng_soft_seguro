package com.github.trabalhoengsoftseguro.artificio.usuario;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller responsável pelo fluxo de login e auto-registro de novas contas na plataforma.
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String exibirLogin(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout,
                              Model model) {
        if (error != null) {
            model.addAttribute("erro", "Credenciais inválidas. Verifique seu e-mail e senha informados.");
        }
        if (logout != null) {
            model.addAttribute("sucesso", "Você encerrou sua sessão com segurança.");
        }
        return "login";
    }

    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
        if (!model.containsAttribute("cadastroDto")) {
            model.addAttribute("cadastroDto", new CadastroDto("", "", "", Role.ROLE_CLIENTE, "", ""));
        }
        model.addAttribute("rolesDisponiveis", List.of(Role.ROLE_CLIENTE, Role.ROLE_ARTISTA));
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@Valid @ModelAttribute("cadastroDto") CadastroDto cadastroDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rolesDisponiveis", List.of(Role.ROLE_CLIENTE, Role.ROLE_ARTISTA));
            return "cadastro";
        }

        try {
            usuarioService.registrarUsuario(cadastroDto);
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça login para começar a usar a plataforma.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("rolesDisponiveis", List.of(Role.ROLE_CLIENTE, Role.ROLE_ARTISTA));
            return "cadastro";
        }
    }
}
