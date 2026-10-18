package com.github.trabalhoengsoftseguro.artificio.catalogo;

import com.github.trabalhoengsoftseguro.artificio.artista.ArtistaService;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller da vitrine pública de artistas e busca facetada no catálogo.
 */
@Controller
public class HomeController {

    private static final List<String> CATEGORIAS_SUGERIDAS = List.of(
            "Pixel Art",
            "Aquarela",
            "Concept Art",
            "3D",
            "Cyberpunk",
            "Fantasia",
            "Retrato"
    );

    private final ArtistaService artistaService;

    public HomeController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping("/")
    public String index(@RequestParam(value = "categoria", required = false) String categoria,
                        @RequestParam(value = "busca", required = false) String busca,
                        Model model) {
        List<Usuario> artistas = artistaService.buscarArtistasComFiltros(categoria, busca);

        model.addAttribute("artistas", artistas);
        model.addAttribute("categoriaSelecionada", categoria);
        model.addAttribute("termoBusca", busca);
        model.addAttribute("categorias", CATEGORIAS_SUGERIDAS);

        return "index";
    }
}
