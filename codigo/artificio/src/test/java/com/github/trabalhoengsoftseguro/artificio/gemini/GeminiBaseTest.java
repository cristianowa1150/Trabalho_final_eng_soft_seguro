package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.auditoria.AuditoriaService;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlobRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ValidadorUploadImagem;
import com.github.trabalhoengsoftseguro.artificio.usuario.TokenAtivacaoRepository;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base abstrata para as sub-suítes decompostas de resiliência e estresse do Gemini.
 * Centraliza infraestrutura de contexto Spring Boot, MockMvc transacional e repositórios compartilhados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class GeminiBaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected TipoComissaoRepository tipoComissaoRepository;

    @Autowired
    protected PedidoComissaoRepository pedidoComissaoRepository;

    @Autowired
    protected ImagemBlobRepository imagemBlobRepository;

    @Autowired
    protected ObraPortfolioRepository obraPortfolioRepository;

    @Autowired
    protected TokenAtivacaoRepository tokenAtivacaoRepository;

    @Autowired
    protected ValidadorUploadImagem validadorUploadImagem;

    @Autowired(required = false)
    protected AuditoriaService auditoriaService;

    /**
     * Helper comum para recuperar a primeira modalidade ativa da artista demo Anna.
     */
    protected TipoComissao obterTipoAnna() {
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
        return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
    }
}
