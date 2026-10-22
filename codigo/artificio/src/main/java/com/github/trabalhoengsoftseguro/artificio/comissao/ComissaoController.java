package com.github.trabalhoengsoftseguro.artificio.comissao;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.AvaliacaoService;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.NovaAvaliacaoDto;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelo ciclo completo de encomendas de arte (solicitação, pagamento simulado PIX,
 * entrega e avaliação).
 */
@Controller
public class ComissaoController {

    private final ComissaoService comissaoService;
    private final AvaliacaoService avaliacaoService;
    private final UsuarioService usuarioService;
    private final TipoComissaoRepository tipoComissaoRepository;
    private final PedidoComissaoRepository pedidoComissaoRepository;

    public ComissaoController(ComissaoService comissaoService,
                              AvaliacaoService avaliacaoService,
                              UsuarioService usuarioService,
                              TipoComissaoRepository tipoComissaoRepository,
                              PedidoComissaoRepository pedidoComissaoRepository) {
        this.comissaoService = comissaoService;
        this.avaliacaoService = avaliacaoService;
        this.usuarioService = usuarioService;
        this.tipoComissaoRepository = tipoComissaoRepository;
        this.pedidoComissaoRepository = pedidoComissaoRepository;
    }

    @GetMapping("/painel/cliente/pedidos")
    public String listarPedidosCliente(Principal principal, Model model) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        List<PedidoComissao> pedidos = comissaoService.listarPorCliente(cliente.getId());
        model.addAttribute("pedidos", pedidos);
        return "cliente/pedidos";
    }

    @GetMapping("/comissoes/solicitar/{tipoId}")
    public String formularioSolicitacao(@PathVariable UUID tipoId,
                                        Principal principal,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        TipoComissao tipo = tipoComissaoRepository.findById(tipoId)
                .orElseThrow(() -> new IllegalArgumentException("Modalidade de serviço não encontrada com ID: " + tipoId));

        if (principal != null) {
            Usuario usuarioLogado = usuarioService.buscarPorEmail(principal.getName());
            if (usuarioLogado.isAdmin()) {
                redirectAttributes.addFlashAttribute("erro", "Administradores não têm permissão para contratar serviços.");
                return "redirect:/artistas/" + tipo.getArtista().getId();
            }
            if (tipo.getArtista().getId().equals(usuarioLogado.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não pode solicitar uma comissão para o seu próprio serviço.");
                return "redirect:/artistas/" + tipo.getArtista().getId();
            }
        }

        model.addAttribute("tipo", tipo);
        model.addAttribute("solicitacaoDto", new SolicitacaoComissaoDto(tipoId, ""));
        return "cliente/solicitar";
    }

    @PostMapping("/comissoes/solicitar")
    public String submeterSolicitacao(@Valid @ModelAttribute("solicitacaoDto") SolicitacaoComissaoDto dto,
                                      BindingResult bindingResult,
                                      Principal principal,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        if (cliente.isAdmin()) {
            redirectAttributes.addFlashAttribute("erro", "Administradores não têm permissão para contratar serviços.");
            return "redirect:/";
        }

        if (dto.tipoComissaoId() == null) {
            redirectAttributes.addFlashAttribute("erro", "Modalidade de serviço inválida ou não especificada.");
            return "redirect:/";
        }

        TipoComissao tipo = tipoComissaoRepository.findById(dto.tipoComissaoId()).orElse(null);
        if (tipo == null) {
            redirectAttributes.addFlashAttribute("erro", "Modalidade de serviço não encontrada.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tipo", tipo);
            return "cliente/solicitar";
        }

        try {
            comissaoService.criarSolicitacao(cliente.getId(), dto);
            redirectAttributes.addFlashAttribute("sucesso", "Solicitação enviada com sucesso! O artista analisará sua proposta.");
            return "redirect:/painel/cliente/pedidos";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/comissoes/{id}/aceitar")
    public String aceitarPedido(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.aceitarPedido(id, artista.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Proposta aceita! O cliente foi notificado para efetuar o pagamento.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/pedidos";
    }

    @PostMapping("/comissoes/{id}/recusar")
    public String recusarPedido(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.recusarPedido(id, artista.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Pedido recusado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/pedidos";
    }

    @PostMapping("/comissoes/{id}/cancelar")
    public String cancelarPedido(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.cancelarPedido(id, cliente.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Pedido cancelado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/cliente/pedidos";
    }

    @GetMapping("/comissoes/{id}/pagamento")
    public String exibirPagamentoPix(@PathVariable UUID id, Principal principal, Model model) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        PedidoComissao pedido = comissaoService.buscarPorId(id);

        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para acessar o pagamento deste pedido");
        }

        model.addAttribute("pedido", pedido);
        return "cliente/pagamento";
    }

    @PostMapping("/comissoes/{id}/confirmar-pagamento")
    public String confirmarPagamento(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.registrarPagamentoSimulado(id, cliente.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Pagamento confirmado com sucesso! O artista iniciará a confecção.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/cliente/pedidos";
    }

    @PostMapping("/comissoes/{id}/iniciar")
    public String iniciarProducao(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.iniciarProducao(id, artista.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Produção iniciada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/pedidos";
    }

    @PostMapping("/comissoes/{id}/entregar")
    public String registrarEntrega(@PathVariable UUID id,
                                   @RequestParam("linkEntrega") String linkEntrega,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        Usuario artista = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.registrarEntrega(id, linkEntrega, artista.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Arquivo/link de entrega submetido com sucesso! Aguarde a aprovação do cliente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/artista/pedidos";
    }

    @PostMapping("/comissoes/{id}/revisao")
    public String solicitarRevisao(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.solicitarRevisao(id, cliente.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Pedido retornado para revisão do artista.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/cliente/pedidos";
    }

    @PostMapping("/comissoes/{id}/aprovar")
    public String aprovarConclusao(@PathVariable UUID id, Principal principal, RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        try {
            comissaoService.aprovarConclusao(id, cliente.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Comissão concluída com sucesso! Deixe sua avaliação para o artista.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/painel/cliente/pedidos";
    }

    @GetMapping("/comissoes/{id}/avaliar")
    public String formularioAvaliacao(@PathVariable UUID id, Principal principal, Model model) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());
        PedidoComissao pedido = comissaoService.buscarPorId(id);

        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para avaliar este pedido");
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("novaAvaliacaoDto", new NovaAvaliacaoDto(id, 5, ""));
        return "cliente/avaliar";
    }

    @PostMapping("/comissoes/{id}/avaliar")
    public String submeterAvaliacao(@PathVariable("id") UUID id,
                                    @Valid @ModelAttribute("novaAvaliacaoDto") NovaAvaliacaoDto dto,
                                    BindingResult bindingResult,
                                    Principal principal,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        Usuario cliente = usuarioService.buscarPorEmail(principal.getName());

        UUID pedidoId = (id != null) ? id : (dto != null ? dto.pedidoId() : null);
        if (pedidoId == null) {
            redirectAttributes.addFlashAttribute("erro", "Pedido de comissão inválido ou não especificado.");
            return "redirect:/painel/cliente/pedidos";
        }

        PedidoComissao pedido = pedidoComissaoRepository.findById(pedidoId).orElse(null);
        if (pedido == null || !pedido.getCliente().getId().equals(cliente.getId())) {
            redirectAttributes.addFlashAttribute("erro", "Pedido de comissão não encontrado ou acesso não autorizado.");
            return "redirect:/painel/cliente/pedidos";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pedido", pedido);
            return "cliente/avaliar";
        }

        try {
            avaliacaoService.salvarAvaliacao(cliente.getId(), dto);
            redirectAttributes.addFlashAttribute("sucesso", "Avaliação registrada com sucesso! Obrigado pelo feedback.");
            return "redirect:/painel/cliente/pedidos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/painel/cliente/pedidos";
        }
    }
}
