package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            ArtistaPerfilDto perfil = artistaService.obterPerfilCompleto(id);
            model.addAttribute("perfil", perfil);
            return "artista/perfil";
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
