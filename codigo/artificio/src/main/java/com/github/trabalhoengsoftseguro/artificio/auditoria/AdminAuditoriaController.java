package com.github.trabalhoengsoftseguro.artificio.auditoria;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller restrito ao papel de Administrador para visualização da Trilha de Auditoria Forense.
 */
@Controller
@RequestMapping("/admin/auditoria")
public class AdminAuditoriaController {

    private final AuditoriaService auditoriaService;

    public AdminAuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listarTrilhaAuditoria(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(value = "busca", required = false) String busca,
            Model model) {
        Page<RegistroAuditoriaDto> paginaRevisoes = auditoriaService.listarRevisoesPaginadas(pagina, tamanho, busca);
        model.addAttribute("pagina", paginaRevisoes);
        model.addAttribute("revisoes", paginaRevisoes.getContent());
        model.addAttribute("paginaAtual", paginaRevisoes.getNumber());
        model.addAttribute("totalPaginas", paginaRevisoes.getTotalPages());
        model.addAttribute("totalItens", paginaRevisoes.getTotalElements());
        model.addAttribute("termoBusca", busca);
        return "admin/auditoria";
    }
}
