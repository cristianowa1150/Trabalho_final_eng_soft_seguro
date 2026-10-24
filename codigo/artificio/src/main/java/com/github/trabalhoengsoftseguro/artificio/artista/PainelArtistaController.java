package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.comissao.ComissaoService;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller exclusivo para artistas autenticados gerenciarem portfólio, serviços e pedidos.
 */
@Controller
@RequestMapping("/painel/artista")
public class PainelArtistaController {

    private final ArtistaService artistaService;
    private final UsuarioService usuarioService;
    private final ComissaoService comissaoService;
    private final TipoComissaoRepository tipoComissaoRepository;

    public PainelArtistaController(ArtistaService artistaService,
                                  UsuarioService usuarioService,
                                  ComissaoService comissaoService,
                                  TipoComissaoRepository tipoComissaoRepository) {
        this.artistaService = artistaService;
        this.usuarioService = usuarioService;
        this.comissaoService = comissaoService;
        this.tipoComissaoRepository = tipoComissaoRepository;
    }

    @GetMapping("/portfolio")
    public String gerenciarPortfolio(Principal principal, Model model) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        List<ObraPortfolio> obras = artistaService.obterPerfilCompleto(artista.getId()).obras();

        model.addAttribute("obras", obras);
        if (!model.containsAttribute("novaObraDto")) {
            model.addAttribute("novaObraDto", new NovaObraDto("", "", "Pixel Art"));
        }
        return "artista/portfolio";
    }

    @PostMapping("/portfolio/novo")
    public String adicionarObra(@Valid @ModelAttribute("novaObraDto") NovaObraDto novaObraDto,
                                BindingResult bindingResult,
                                @RequestParam("arquivo") MultipartFile arquivo,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());

        if (arquivo == null || arquivo.isEmpty()) {
            bindingResult.rejectValue("titulo", "arquivo.obrigatorio", "O arquivo da obra de arte deve ser selecionado");
        }

        if (bindingResult.hasErrors() || arquivo == null) {
            List<ObraPortfolio> obras = artistaService.obterPerfilCompleto(artista.getId()).obras();
            model.addAttribute("obras", obras);
            return "artista/portfolio";
        }

        try {
            artistaService.adicionarObra(
                    artista.getId(),
                    novaObraDto,
                    arquivo.getBytes(),
                    arquivo.getOriginalFilename(),
                    arquivo.getContentType()
            );
            redirectAttributes.addFlashAttribute("sucesso", "Obra '" + novaObraDto.titulo() + "' publicada com sucesso!");
        } catch (IOException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao publicar obra: " + ex.getMessage());
        }

        return "redirect:/painel/artista/portfolio";
    }

    @PostMapping("/portfolio/{id}/remover")
    public String removerObra(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            artistaService.removerObra(artista.getId(), id);
            redirectAttributes.addFlashAttribute("sucesso", "Obra removida do portfólio com sucesso!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/portfolio";
    }

    @GetMapping("/servicos")
    public String gerenciarServicos(Principal principal, Model model) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        List<TipoComissao> servicos = artistaService.listarTiposComissaoDoArtista(artista.getId());

        model.addAttribute("servicos", servicos);
        if (!model.containsAttribute("novoTipoDto")) {
            model.addAttribute("novoTipoDto", new NovoTipoComissaoDto("", "", "Ilustração", new BigDecimal("150.00"), 7));
        }
        return "artista/servicos";
    }

    @PostMapping("/servicos/novo")
    public String cadastrarServico(@Valid @ModelAttribute("novoTipoDto") NovoTipoComissaoDto novoTipoDto,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());

        if (bindingResult.hasErrors()) {
            List<TipoComissao> servicos = artistaService.listarTiposComissaoDoArtista(artista.getId());
            model.addAttribute("servicos", servicos);
            return "artista/servicos";
        }

        try {
            artistaService.salvarTipoComissao(artista.getId(), novoTipoDto);
            redirectAttributes.addFlashAttribute("sucesso", "Modalidade de comissão '" + novoTipoDto.titulo() + "' disponibilizada com sucesso!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }

        return "redirect:/painel/artista/servicos";
    }

    @PostMapping("/servicos/{id}/toggle")
    public String alternarStatusServico(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            TipoComissao tipo = tipoComissaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + id));
            boolean novoStatus = !tipo.isAtivo();
            artistaService.alternarStatusTipoComissao(artista.getId(), id, novoStatus);
            String status = novoStatus ? "ativado" : "pausado";
            redirectAttributes.addFlashAttribute("sucesso", "Serviço " + status + " com sucesso!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/servicos";
    }

    @GetMapping("/pedidos")
    public String visualizarPedidosRecebidos(Principal principal, Model model) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        List<PedidoComissao> pedidos = comissaoService.listarPorArtista(artista.getId());
        model.addAttribute("pedidos", pedidos);
        return "artista/pedidos";
    }
}
