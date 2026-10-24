package com.github.trabalhoengsoftseguro.artificio.gemini;

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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Comissões, Quebra de Transições e Matriz Completa da Máquina de Estados")
public class GeminiComissoesMaquinaEstadosTests extends GeminiBaseTest {

    private PedidoComissao criarPedido(StatusComissao status) {
        Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
        TipoComissao tipo = obterTipoAnna();

        PedidoComissao pedido = new PedidoComissao("Pedido de teste de estado", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
        pedido.setStatus(status);
        return pedidoComissaoRepository.save(pedido);
    }

    // ------------------------------------------------------------------------------------------------
    // 1. CICLO DE VIDA DE COMISSÕES, QUEBRA DE ESTADO E AVALIAÇÕES
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Ciclo de Vida de Comissões, Quebra de Estado e Avaliações")
    class ComissoesETransicoesDeEstadoTests {

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar solicitação com briefing excessivamente curto (menos de 10 caracteres)")
        void testSolicitacaoComBriefingCurto() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rejeição de briefing muito curto.");
            TipoComissao tipo = obterTipoAnna();

            mockMvc.perform(post("/comissoes/solicitar")
                    .with(csrf())
                    .param("tipoComissaoId", tipo.getId().toString())
                    .param("briefing", "Oi"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/solicitar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("solicitacaoDto", "briefing"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar solicitação com briefing gigante (10.000 caracteres) excedendo o limite de 4.000")
        void testSolicitacaoComBriefingGigante() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rejeição de briefing que excede 4000 caracteres.");
            TipoComissao tipo = obterTipoAnna();
            String briefingGigante = "Briefing extenso ".repeat(300);

            mockMvc.perform(post("/comissoes/solicitar")
                    .with(csrf())
                    .param("tipoComissaoId", tipo.getId().toString())
                    .param("briefing", briefingGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/solicitar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("solicitacaoDto", "briefing"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir confirmação de pagamento para pedido em estado SOLICITADA (artista ainda não aceitou)")
        void testPagamentoEmEstadoInvalido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando transição inválida de pagamento direto em pedido SOLICITADA.");
            Usuario cliente = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Quero um desenho em aquarela para teste",
                    new BigDecimal("150.00"), 5, cliente, anna, tipo);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar Cliente B tentando acessar pagamento do pedido do Cliente A (Anti-IDOR)")
        void testIdorAcessoPagamentoOutroCliente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção contra IDOR no acesso à tela de pagamento.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Briefing privado do Eduardo", new BigDecimal("150.00"),
                    5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(get("/comissoes/" + pedidoEduardo.getId() + "/pagamento"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar Cliente B tentando avaliar pedido concluído do Cliente A (Anti-IDOR)")
        void testIdorAvaliacaoPedidoOutroCliente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção contra IDOR na avaliação de pedido de terceiro.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Briefing privado do Eduardo", new BigDecimal("150.00"),
                    5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.CONCLUIDA);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(get("/comissoes/" + pedidoEduardo.getId() + "/avaliar"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar submissão de avaliação com nota inválida zero ou superior a cinco")
        void testAvaliacaoComNotaInvalida() throws Exception {
            log.info("Elaborado pelo Gemini: Testando validação de nota de avaliação fora do intervalo [1..5].");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Pedido finalizado para avaliação", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedido.setStatus(StatusComissao.CONCLUIDA);
            pedido = pedidoComissaoRepository.save(pedido);

            // Nota 0 (abaixo de 1)
            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/avaliar")
                    .with(csrf())
                    .param("pedidoId", pedido.getId().toString())
                    .param("nota", "0")
                    .param("comentario", "Nota zero não permitida"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/avaliar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novaAvaliacaoDto", "nota"));

            // Nota 999 (acima de 5)
            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/avaliar")
                    .with(csrf())
                    .param("pedidoId", pedido.getId().toString())
                    .param("nota", "999")
                    .param("comentario", "Nota extrema não permitida"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/avaliar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novaAvaliacaoDto", "nota"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de registrar entrega com link vazio")
        void testEntregaComLinkVazio() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega com link de arte em branco.");
            UUID uuidAleatorio = UUID.randomUUID();

            mockMvc.perform(post("/comissoes/" + uuidAleatorio + "/entregar")
                    .with(csrf())
                    .param("linkEntrega", "   "))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 2. MATRIZ COMPLETA DE TRANSIÇÕES INVÁLIDAS DA MÁQUINA DE ESTADOS (STATE MACHINE FUZZING)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Matriz Completa de Transições Inválidas de Pedidos")
    class MatrizCompletaMaquinaDeEstadosTests {

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de comissão para modalidade desativada (ativo = false)")
        void testTentativaSolicitarComissaoEmModalidadeInativa() throws Exception {
            log.info("Elaborado pelo Gemini: Testando contratação de modalidade inativa.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao inativo = new TipoComissao("Arte Antiga", "Desc", "Aquarela", new BigDecimal("50.00"), 3, anna);
            inativo.setAtivo(false);
            inativo = tipoComissaoRepository.save(inativo);

            mockMvc.perform(post("/comissoes/solicitar")
                            .with(csrf())
                            .param("tipoComissaoId", inativo.getId().toString())
                            .param("briefing", "Tentando encomendar serviço desativado"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("erro", "Esta modalidade de serviço não está mais disponível para novas encomendas"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido já CANCELADO")
        void testTentativaAceitarPedidoJaCancelado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido cancelado.");
            PedidoComissao pedido = criarPedido(StatusComissao.CANCELADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido já RECUSADO")
        void testTentativaAceitarPedidoJaRecusado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido recusado.");
            PedidoComissao pedido = criarPedido(StatusComissao.RECUSADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido que já está EM_ANDAMENTO")
        void testTentativaAceitarPedidoJaEmAndamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido em andamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.EM_ANDAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de recusar pedido que já está PAGO")
        void testTentativaRecusarPedidoJaPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de recusar pedido pago.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser recusados"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de recusar pedido já CONCLUIDO")
        void testTentativaRecusarPedidoJaConcluido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de recusar pedido concluído.");
            PedidoComissao pedido = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser recusados"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já foi CANCELADO")
        void testTentativaPagarPedidoJaCancelado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento em pedido cancelado.");
            PedidoComissao pedido = criarPedido(StatusComissao.CANCELADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já foi RECUSADO")
        void testTentativaPagarPedidoJaRecusado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento em pedido recusado.");
            PedidoComissao pedido = criarPedido(StatusComissao.RECUSADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já está PAGO (pagamento duplo)")
        void testTentativaPagarPedidoJaPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento duplicado.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir início de produção de pedido em estado SOLICITADA sem pagamento")
        void testTentativaIniciarProducaoPedidoSolicitadaSemPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando início prematuro de produção em pedido não pago.");
            PedidoComissao pedido = criarPedido(StatusComissao.SOLICITADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/iniciar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos pagos podem ter a produção iniciada"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir início de produção de pedido em AGUARDANDO_PAGAMENTO")
        void testTentativaIniciarProducaoPedidoAguardandoPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando início de produção de pedido aguardando pagamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/iniciar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos pagos podem ter a produção iniciada"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir registro de entrega de pedido em SOLICITADA")
        void testTentativaEntregarPedidoEmSolicitada() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega de pedido em estado SOLICITADA.");
            PedidoComissao pedido = criarPedido(StatusComissao.SOLICITADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/entregar")
                            .with(csrf())
                            .param("linkEntrega", "https://entrega.arte/123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido deve estar em andamento para receber uma entrega"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir registro de entrega de pedido em AGUARDANDO_PAGAMENTO")
        void testTentativaEntregarPedidoAguardandoPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega de pedido em AGUARDANDO_PAGAMENTO.");
            PedidoComissao pedido = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/entregar")
                            .with(csrf())
                            .param("linkEntrega", "https://entrega.arte/123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido deve estar em andamento para receber uma entrega"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de revisão para pedido PAGO que ainda não foi entregue")
        void testTentativaSolicitarRevisaoPedidoPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de revisão para pedido pago não entregue.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos entregues podem ter revisão solicitada"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir aprovação de conclusão para pedido PAGO não entregue")
        void testTentativaAprovarConclusaoPedidoPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando aprovação de conclusão em pedido pago não entregue.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos com arte entregue podem ser concluídos e aprovados"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cancelamento de pedido já CONCLUIDO")
        void testTentativaCancelarPedidoJaConcluido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando cancelamento de pedido já concluído.");
            PedidoComissao pedido = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não pode ser cancelado após o início da produção ou pagamento"));
        }
    }
}
