package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller público para visualização da vitrine, portfólio e serviços de um artista específico.
 */
@Controller
@RequestMapping("/artistas")
public class ArtistaPublicController {

    private final ArtistaService artistaService;

    public ArtistaPublicController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping("/{id}")
    public String exibirPerfilPublico(@PathVariable Long id, Model model) {
        ArtistaPerfilDto perfil = artistaService.obterPerfilCompleto(id);
        model.addAttribute("perfil", perfil);
        return "artista/perfil";
    }
}
