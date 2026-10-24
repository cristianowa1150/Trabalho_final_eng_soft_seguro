package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Proteção Anti-IDOR, Isolamento Horizontal e Identificadores UUID")
public class GeminiIdorIntegridadeDadosTests extends GeminiBaseTest {

    // ------------------------------------------------------------------------------------------------
    // 1. IDENTIFICADORES UUID E ROTAS MALFORMADAS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Resiliência Defensiva com UUIDs Malformados e Inexistentes")
    class IdentificadoresUUIDEMalformadosTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 400 amigável com view de erro para URL com UUID malformado em perfil público")
        void testPerfilArtistaComUUIDMalformado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rota pública com UUID sintaticamente corrompido.");
            mockMvc.perform(get("/artistas/isto-definitivamente-nao-e-um-uuid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attributeExists("status"))
                    .andExpect(model().attributeExists("mensagem"))
                    .andExpect(content().string(containsString("Identificador ou Parâmetro Inválido")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 404 amigável com view de erro para UUID válido porém inexistente no banco")
        void testPerfilArtistaComUUIDInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rota pública com UUID inexistente.");
            UUID uuidInexistente = UUID.fromString("018f0000-0000-7000-8000-000000000000");

            mockMvc.perform(get("/artistas/" + uuidInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 404))
                    .andExpect(content().string(containsString("Recurso Não Encontrado")));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve capturar formulário de contratação com modalidade malformada e exibir tela defensiva")
        void testSolicitacaoComissaoComUUIDTipoMalformado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de comissão com UUID malformado na rota.");
            mockMvc.perform(get("/comissoes/solicitar/uuid-corrompido-xyz"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve tratar formulário de contratação com modalidade inexistente sem quebrar em HTTP 500")
        void testSolicitacaoComissaoComUUIDTipoInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de comissão com UUID inexistente.");
            UUID uuidInexistente = UUID.fromString("018f9999-9999-7999-8999-999999999999");

            mockMvc.perform(get("/comissoes/solicitar/" + uuidInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 404));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar 404 para busca de imagem com identificador inexistente")
        void testExibirImagemComIdInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando busca de imagem com identificador inexistente.");
            mockMvc.perform(get("/imagens/imagem-inexistente-12345"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve tratar promoção de usuário com UUID inexistente com mensagem flash sem estourar 500")
        void testPromoverUsuarioComUUIDInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando endpoint de promoção administrativa com usuário inexistente.");
            UUID uuidInexistente = UUID.randomUUID();

            mockMvc.perform(post("/admin/usuarios/" + uuidInexistente + "/promover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/usuarios"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 2. TENTATIVAS DE MANIPULAÇÃO CRUZADA ENTRE ARTISTAS (ANTI-IDOR & ANTI-TAMPERING)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Manipulação Cruzada Entre Artistas e Violações de Ciclo de Vida")
    class ManipulacaoCruzadaEntreArtistasTests {

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de aceitar pedido endereçado ao Artista A")
        void testArtistaBNaoPodeAceitarPedidoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando se Artista B consegue aceitar pedido do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoParaAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedidoParaAnna = pedidoComissaoRepository.save(pedidoParaAnna);

            // Cristiano (Artista B) tenta aceitar o pedido da Anna
            mockMvc.perform(post("/comissoes/" + pedidoParaAnna.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista destinatário pode aceitar este pedido"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de recusar pedido endereçado ao Artista A")
        void testArtistaBNaoPodeRecusarPedidoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando se Artista B consegue recusar pedido do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoParaAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedidoParaAnna = pedidoComissaoRepository.save(pedidoParaAnna);

            mockMvc.perform(post("/comissoes/" + pedidoParaAnna.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista destinatário pode recusar este pedido"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de registrar entrega de trabalho do Artista A")
        void testArtistaBNaoPodeEntregarPedidoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando se Artista B consegue registrar entrega de trabalho do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedidoAnna.setStatus(StatusComissao.EM_ANDAMENTO);
            pedidoAnna = pedidoComissaoRepository.save(pedidoAnna);

            mockMvc.perform(post("/comissoes/" + pedidoAnna.getId() + "/entregar")
                    .with(csrf())
                    .param("linkEntrega", "https://entrega.falsa/arte.zip"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista pode registrar a entrega do trabalho"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de cancelar pedido quando a produção já foi iniciada")
        void testClienteNaoPodeCancelarPedidoEmAndamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando bloqueio de cancelamento após início da produção.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho em produção", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedido.setStatus(StatusComissao.EM_ANDAMENTO);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro",
                            "O pedido não pode ser cancelado após o início da produção ou pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de revisão para pedido que ainda não foi entregue")
        void testSolicitacaoRevisaoEmPedidoNaoEntregue() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pedido de revisão para encomenda ainda em confecção.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho não entregue", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedido.setStatus(StatusComissao.EM_ANDAMENTO);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos entregues podem ter revisão solicitada"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir aprovação de conclusão para pedido que ainda não foi entregue")
        void testAprovacaoConclusaoEmPedidoNaoEntregue() throws Exception {
            log.info("Elaborado pelo Gemini: Testando aprovação de comissão que sequer recebeu entrega.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho inacabado", new BigDecimal("150.00"), 5, eduardo, anna,
                    tipo);
            pedido.setStatus(StatusComissao.SOLICITADA);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro",
                            "Apenas pedidos com arte entregue podem ser concluídos e aprovados"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 3. BATERIA AVANÇADA DE PROTEÇÃO ANTI-IDOR (MANIPULAÇÃO CRUZADA ENTRE USUÁRIOS)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Bateria Completa de Proteção Anti-IDOR")
    class BateriaAvancadaAntiIdorTests {

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de alternar (toggle) status do serviço do Artista A")
        void testIdorArtistaBNaoPodeToggleServicoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR na alteração de status de serviço entre artistas.");
            TipoComissao tipoAnna = obterTipoAnna();

            mockMvc.perform(post("/painel/artista/servicos/" + tipoAnna.getId() + "/toggle").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/servicos"))
                    .andExpect(flash().attribute("erro", "O serviço não pertence ao artista logado"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de remover obra do portfólio do Artista A")
        void testIdorArtistaBNaoPodeRemoverObraDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR na exclusão de obra do portfólio.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            ObraPortfolio obraAnna = new ObraPortfolio("Obra Anna", "Desc", "Aquarela", "uuid-fake", anna);
            obraAnna = obraPortfolioRepository.save(obraAnna);

            mockMvc.perform(post("/painel/artista/portfolio/" + obraAnna.getId() + "/remover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attribute("erro", "A obra não pertence ao artista logado"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de confirmar pagamento do pedido do Cliente A")
        void testIdorClienteBNaoPodeConfirmarPagamentoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de confirmação de pagamento.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode confirmar o pagamento"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de cancelar o pedido do Cliente A")
        void testIdorClienteBNaoPodeCancelarPedidoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de cancelamento de pedido.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.SOLICITADA);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode cancelar este pedido"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de aprovar a conclusão do pedido do Cliente A")
        void testIdorClienteBNaoPodeAprovarConclusaoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de aprovação de conclusão.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.ENTREGUE);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente pode aprovar a conclusão da comissão"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de solicitar revisão do pedido do Cliente A")
        void testIdorClienteBNaoPodeSolicitarRevisaoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de solicitação de revisão.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.ENTREGUE);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente pode solicitar revisão da arte entregue"));
        }
    }
}
