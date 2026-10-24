# Relatório Geral de Testes Automatizados e Casos de Abuso

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)  
**Status da Suíte:** **276 testes automatizados executados com 100% de aprovação (0 falhas, 0 erros)**  
**Comando de Execução:** `make test` ou `./mvnw test` a partir de `codigo/artificio/`

---

## 1. Visão Geral da Arquitetura de Testes

A estratégia de garantia de qualidade e resiliência da plataforma **Artifício** foi concebida sob uma abordagem defensiva em profundidade (*Defense in Depth*) e *Security Test-First* (TDD de Segurança). A suíte contempla testes unitários isolados, testes de integração transacional com banco H2 em memória, auditoria forense com Hibernate Envers e testes de requisição HTTP simulada via Spring `MockMvc` com contexto completo do Spring Security.

A suíte está estruturada em três grandes grupos:
1. **Testes Iniciais da Aplicação (Base)** (107 testes): Validações essenciais das regras de negócio, serviços, persistência relacional, segregação de schemas de banco e controle de acesso inicial em conformidade com *Package-by-Feature*.
2. **Testes Formais dos Casos de Abuso** (42 testes): Bateria mapeada diretamente a partir do documento de modelagem de ameaças e especificação de casos de abuso (*8.6 Definição de casos de abuso*), cobrindo os códigos **CA01 a CA27**.
3. **Testes de Resiliência e Casos de Borda do Gemini** (127 testes): Bateria dividida em 6 classes modulares no pacote `com.github.trabalhoengsoftseguro.artificio.gemini`, cobrindo cenários de estresse, ataques STRIDE, entradas anômalas e resiliência defensiva.

---

## 2. Testes Iniciais da Aplicação (107 Testes)

Estes testes cobrem o funcionamento fundamental dos serviços de domínio, repositórios JPA, validadores e controladores web:

| Classe de Teste | Qtd. | Foco e Cenários Cobertos |
| :--- | :---: | :--- |
| **ArtificioApplicationTests** | 1 | Inicialização e integridade do contexto do Spring Boot. |
| **ArtificioJpaTests** | 2 | Persistência relacional, integridade de `EntidadeAuditavel` e segregação estrita das tabelas do Hibernate Envers (`REVINFO`, `*_AUD`) no schema isolado `AUDITORIA`. |
| **UsuarioServiceTest** | 16 | Registro de usuários, validação estrita de e-mail normalizado, criptografia de senhas com BCrypt, geração e confirmação de tokens temporários, bloqueio de auto-atribuição de `ROLE_ADMIN`. |
| **AuthControllerTest** | 7 | Renderização de telas de autenticação, validação de formulário de cadastro, mensagens flash de sucesso/erro e confirmação de e-mail via link de token. |
| **AdminUsuarioControllerTest** | 7 | Controle de acesso vertical RBAC: bloqueio de anônimos e clientes em rotas `/admin/usuarios`, permissão para `ROLE_ADMIN`, promoção segura de usuários a artistas. |
| **ComissaoServiceTest** | 7 | Ciclo de vida da máquina de estados (`SOLICITADA` → `AGUARDANDO_PAGAMENTO` → `PAGO` → `EM_ANDAMENTO` → `ENTREGUE` → `CONCLUIDA`), isolamento por posse (*Ownership*), transições ilegais barradas. |
| **ComissaoControllerTest** | 10 | Endpoints de solicitação, pagamento simulado, início de confecção, registro de link de entrega e avaliação. |
| **PainelArtistaControllerTest** | 6 | Acesso exclusivo para `ROLE_ARTISTA`, cadastro de serviços de comissão e publicação de obras no portfólio. |
| **ArtistaServiceTest** | 8 | Gestão de tipos de comissão (ativação/desativação), integridade de preços e prazos mínimos, isolamento de obras por artista. |
| **ArtistaPublicControllerTest** | 4 | Exibição pública de perfis de artistas, portfólios e serviços com proteção contra visualização de rascunhos inativos. |
| **AvaliacaoServiceTest** | 5 | Regras de avaliação: restrita ao cliente contratante, permitida apenas após conclusão da comissão, unicidade de avaliação por pedido, nota no intervalo de 1 a 5. |
| **ImagemServiceTest** | 16 | Armazenamento de mídia binária (BLOB), validação e detecção de Magic Bytes (`PNG`, `JPEG`, `WEBP`, `SVG`), sanitização e bloqueio de payloads maliciosos. |
| **ImagemControllerTest** | 4 | Resposta HTTP com cabeçalhos defensivos de mídia: Content-Disposition `inline`, `Content-Security-Policy: sandbox` e `X-Content-Type-Options: nosniff`. |
| **AdminAuditoriaControllerTest** | 6 | Acesso administrativo à visualização e filtragem da trilha forense do Hibernate Envers. |
| **HomeControllerTest** | 4 | Vitrine principal, renderização de catálogo de artistas e filtragem de busca. |
| Testes de Configuração e Inicialização (`AdminInitializerTest`, `DataInitializerProdTest`, `GlobalModelAdviceProdTest`) | 4 | Carga defensiva inicial de contas padrão e isolamento de perfil produtivo sem credenciais hardcoded. |

---

## 3. Testes dos Casos de Abuso (42 Testes — Suíte CasosAbusoTests)

Implementados na classe **CasosAbusoTests**, estes 42 testes mapeiam e verificam com precisão cada caso de abuso identificado no relatório pedagógico (CA01 a CA27), aplicando uma subnumeração decimal padronizada:

| Código | Subcaso | Método do Teste | Cenário de Abuso Avaliado | Comportamento de Defesa / Resultado Esperado |
| :---: | :---: | :--- | :--- | :--- |
| **CA01** | `CA01.1` | `testCA01_1_StoredXssBioArtista` | Injeção de Stored XSS na bio do artista (`<script>alert('xss')</script>`) | Renderização com escapamento contextual no Thymeleaf (`&lt;script&gt;`), neutralizando a execução. |
| | `CA01.2` | `testCA01_2_EvasaoEmailDuplicadoCaseInsensitive` | Tentativa de evasão de unicidade de e-mail por variação de maiúsculas/minúsculas | Normalização via `toLowerCase()` e bloqueio com mensagem amigável de duplicidade. |
| **CA02** | `CA02.1` | `testCA02_1_ReplayAttackTokenAtivacao` | Ataque de *Replay* reutilizando token de ativação de conta já consumido | Segunda tentativa rejeitada sumariamente com expurgo e bloqueio de reutilização. |
| | `CA02.2` | `testCA02_2_TokenAtivacaoInexistente` | Submissão de token forjado ou sintaticamente aleatório | Redirecionamento defensivo para `/login` com flash attribute de erro. |
| | `CA02.3` | `testCA02_3_TokenAtivacaoExpirado` | Submissão de token com prazo de 24 horas já expirado | Rejeição por expiração temporal e invalidação do vínculo da conta. |
| **CA04** | `CA04.1` | `testCA04_1_IsolamentoSessaoCliente` | Cliente B tenta cancelar pedido pertencente a Cliente A autenticado | Bloqueio de autorização com mensagem de violação de posse (*Ownership*). |
| **CA05** | `CA05.1` | `testCA05_1_UploadFalsificacaoMagicBytes` | Upload de binário executável PE/EXE disfarçado com extensão `.png` | Validador inspeciona Magic Bytes (`89 50 4E 47`) e lança `IllegalArgumentException`. |
| | `CA05.2` | `testCA05_2_UploadSvgComTagScript` | Upload de vetor SVG com tag `<SCRIPT>` ativa | Bloqueio pelo validador de tags perigosas em imagens vetoriais. |
| | `CA05.3` | `testCA05_3_UploadSvgComManipuladorEvento` | Upload de SVG com manipuladores JavaScript (`onmouseover`, `onload`) | Bloqueio imediato por presença de manipuladores de eventos em atributos. |
| | `CA05.4` | `testCA05_4_UploadSvgComUriJavascript` | Upload de SVG com esquema de URI `javascript:` em links embutidos | Bloqueio pelo validador de esquemas e protocolos inseguros. |
| | `CA05.5` | `testCA05_5_UploadSvgComTagsProibidas` | Upload de SVG com tags `foreignObject`, `iframe` ou `embed` | Rejeição estrita por estrutura XML potencialmente explorável. |
| **CA06** | `CA06.1` | `testCA06_1_UploadExcedeLimiteMaximo` | Envio de imagem excedendo o teto configurado de 5MB | Rejeição automática de buffer com mensagem de excesso de tamanho. |
| | `CA06.2` | `testCA06_2_UploadArquivoTruncado` | Upload de arquivo corrompido com cabeçalho incompleto (< 8 bytes) | Rejeição por falha na identificação de assinatura de formato válido. |
| **CA07** | `CA07.1` | `testCA07_1_ConcluirPedidoAntesEntrega` | Cliente tenta aprovar e concluir comissão antes do artista submeter entrega | Violação de transição da máquina de estados rejeitada com erro amigável. |
| | `CA07.2` | `testCA07_2_EntregarPedidoAntesPagamento` | Artista tenta submeter entrega antes do pagamento ser confirmado | Bloqueio de transição prematura antes de estado `EM_ANDAMENTO`/`PAGO`. |
| | `CA07.3` | `testCA07_3_CancelarPedidoEmProducao` | Cliente tenta cancelamento unilateral após a produção já ter sido iniciada | Preservação do trabalho do artista; cancelamento permitido apenas nos estados iniciais. |
| | `CA07.4` | `testCA07_4_RecusarPedidoJaPago` | Artista tenta recusar encomenda após o cliente já ter pago | Bloqueio de recusa indevida com exigência de conciliação. |
| **CA08** | `CA08.1` | `testCA08_1_AvaliarPedidoDeTerceiro` | Anti-IDOR: Cliente tenta submeter avaliação em encomenda alheia | Bloqueio por checagem de `clienteId` vinculado ao pedido. |
| | `CA08.2` | `testCA08_2_AvaliarPedidoNaoConcluido` | Cliente tenta avaliar comissão que ainda se encontra em produção | Bloqueio por estado inválido (`CONCLUIDA` é pré-requisito obrigatório). |
| | `CA08.3` | `testCA08_3_AvaliacaoNotaInvalida` | Submissão de nota fora do intervalo aceitável (ex: nota 10 ou zero) | Bean Validation `@Min(1)` e `@Max(5)` rejeita submissão com erro de campo. |
| **CA09** | `CA09.1` | `testCA09_1_PostEmAuditoriaRetorna405` | Tentativa de forjar novos registros via `POST` na trilha forense `/admin/auditoria` | `GlobalExceptionHandler` captura e retorna `HTTP 405 Method Not Allowed`. |
| | `CA09.2` | `testCA09_2_DeleteEmAuditoriaRetorna405` | Tentativa de expurgo malicioso via `DELETE` na trilha de auditoria | Rejeição com `HTTP 405 Method Not Allowed`, garantindo não-destrutibilidade. |
| **CA10** | `CA10.1` | `testCA10_1_ImutabilidadeDataCriacao` | Alteração posterior em entidade para tentar sobrescrever `dataCriacao` | Metadados anotados com `@Column(updatable=false)` preservam valor original. |
| **CA11** | `CA11.1` | `testCA11_1_PreservacaoRevisoesEnvers` | Modificações em entidades auditadas (`@Audited`) | Hibernate Envers persiste linha imutável na tabela `_AUD` a cada transação. |
| **CA12** | `CA12.1` | `testCA12_1_ServirImagemComHeadersSeguros` | Acesso direto a binários de imagem via rota `/imagens/{id}` | Resposta com `Content-Disposition: inline`, `CSP sandbox` e `nosniff`. |
| **CA13** | `CA13.1` | `testCA13_1_ClienteNaoPagaPedidoAlheio` | Anti-IDOR: Cliente B tenta forçar confirmação de pagamento do Cliente A | Bloqueio imediato com `IllegalArgumentException` de autorização. |
| | `CA13.2` | `testCA13_2_ClienteNaoPedeRevisaoPedidoAlheio` | Anti-IDOR: Cliente B tenta requisitar revisão na arte do Cliente A | Bloqueio de ação transacional sem correspondência de contratante. |
| **CA14** | `CA14.1` | `testCA14_1_IdentificadoresUUIDv7NaoSequenciais` | Tentativa de inferência sequencial de chaves primárias numéricas | Chaves primárias utilizam RFC 9562 UUIDv7 com ordenação cronológica e não-previsibilidade. |
| | `CA14.2` | `testCA14_2_TratamentoUUIDMalformado` | Submissão de UUID sintaticamente corrompido na URL | Captura por `MethodArgumentTypeMismatchException` retornando HTTP 400 sem stack trace. |
| **CA16** | `CA16.1` | `testCA16_1_BuscaCatalogoPayloadGigantesco` | Tentativa de exaustão de buffer no catálogo público com busca de 20.000 caracteres | Aplicação processa requisição com resiliência sem estouro de memória ou crash. |
| | `CA16.2` | `testCA16_2_BuscaAuditoriaPayloadGigantesco` | Submissão de 20.000 caracteres no filtro de auditoria | Filtragem processada defensivamente retornando view administrativa intacta. |
| **CA22** | `CA22.1` | `testCA22_1_AnonimoBarradoAreaAdministrativa` | Visitante sem credenciais tenta acessar rota `/admin/**` | Redirecionamento 302 para `/login` pelo filtro de autorização do Spring Security. |
| | `CA22.2` | `testCA22_2_ClienteBarradoAreaAdministrativa` | Usuário comum (`ROLE_CLIENTE`) requisita rota administrativa | Resposta `HTTP 403 Forbidden` impedindo acesso a dados administrativos. |
| | `CA22.3` | `testCA22_3_ArtistaBarradoAreaAdministrativa` | Artista (`ROLE_ARTISTA`) requisita rota administrativa | Resposta `HTTP 403 Forbidden` garantindo segregação de papéis. |
| | `CA22.4` | `testCA22_4_ClienteBarradoAuditoriaForense` | Usuário comum tenta acessar painel de auditoria `/admin/auditoria` | Resposta `HTTP 403 Forbidden` garantindo sigilo dos logs forenses. |
| **CA23** | `CA23.1` | `testCA23_1_AutoCadastroNaoPermiteRoleAdmin` | Tentativa de escalada vertical forjando `role=ROLE_ADMIN` no formulário público | O backend rejeita com erro explícito e impede a criação da conta maliciosa. |
| | `CA23.2` | `testCA23_2_PromocaoUsuarioExclusivaAdmin` | Usuário não autenticado como administrador tenta promover outras contas | Bloqueio automático com `HTTP 403 Forbidden`. |
| **CA24** | `CA24.1` | `testCA24_1_ArtistaNaoAlteraServicoAlheio` | Anti-IDOR: Artista B tenta alternar visibilidade de serviço do Artista A | Rejeição por ownership com mensagem de serviço não pertencente ao artista. |
| | `CA24.2` | `testCA24_2_ArtistaNaoRemoveObraAlheia` | Anti-IDOR: Artista B tenta deletar obra do portfólio do Artista A | Rejeição por ownership com integridade preservada da obra do autor legítimo. |
| **CA25** | `CA25.1` | `testCA25_1_LoginContaNaoConfirmadaBloqueado` | Tentativa de login com conta cujo e-mail ainda não foi confirmado | `UserDetails.disabled=true` faz o Spring Security barrar o acesso no login. |
| **CA26** | `CA26.1` | `testCA26_1_AutocontratacaoBloqueada` | Artista tenta contratar seu próprio serviço de comissão para forjar métricas | Validação de regra de negócio rejeita autocontratação com mensagem clara. |
| **CA27** | `CA27.1` | `testCA27_1_AuditoriaPromocaoAdministrativa` | Execução de promoção de usuário por administrador | Operação auditada com sucesso, gerando revisão forense e timestamp de atualização. |

---

## 4. Testes de Resiliência do Gemini (127 Testes — 6 Classes Modulares)

A suíte do Gemini foi refatorada e decomposta em **6 classes especializadas e desacopladas**, localizadas no pacote `com.github.trabalhoengsoftseguro.artificio.gemini`, herdando o contexto compartilhado de `GeminiBaseTest`. Esta arquitetura elimina arquivos monolíticos, viabiliza execução paralela e isolamento claro por vetor de segurança:

| Classe Modular de Teste | Qtd. | Foco e Vetores de Teste |
| :--- | :---: | :--- |
| **GeminiAutenticacaoControleAcessoTests** | 23 | Prevenção de escalada de privilégios vertical (RBAC), tentativas de auto-atribuição de `ROLE_ADMIN`, proteção obrigatória contra CSRF em endpoints `POST` sensíveis, e isolamento de rotas administrativas. |
| **GeminiComissoesMaquinaEstadosTests** | 23 | Integridade estrita da máquina de estados transacional de comissões artísticas (tentativas de cancelamento, solicitação de revisão fora de estado permitido, entrega prematura, aprovação sem arte, recusa de pedido já quitado). |
| **GeminiIdorIntegridadeDadosTests** | 18 | Violação de autorização e posse (*Anti-IDOR / BOLA*): tentativas de manipulação ou exclusão de obras de portfólio de terceiros, confirmação de pagamentos ou entregas por contas não-proprietárias. |
| **GeminiUploadSegurancaMidiaTests** | 16 | Falsificação de mídia e upload malicioso: validação de Magic Bytes (`PNG`, `JPEG`, `WEBP`, `SVG`), bloqueio de vetores SVG contendo scripts embutidos, esquemas `javascript:` e tags perigosas, e respeito ao teto de 5MB. |
| **GeminiValidacaoResilienciaHttpTests** | 32 | Entradas espúrias e extremas (strings gigantescas, valores monetários negativos, unicode), blindagem a injeções de SQL parametrizadas, proteção contra XSS refletido/persistido e resiliência uniforme do `GlobalExceptionHandler`. |
| **GeminiWebAuditoriaTests** | 15 | Auditoria forense e integridade do Hibernate Envers: métodos HTTP não permitidos (405) na trilha forense, paginação defensiva, imutabilidade temporal e integridade de acesso administrativo. |

---

## 5. Resumo Consolidado da Suíte

```
========================================================================================
RESUMO CONSOLIDADO DE EXECUÇÃO DOS TESTES
========================================================================================
Testes Iniciais da Aplicação (Base / Feature Packages)      : 107 testes  [100% PASS]
Suíte Formal de Casos de Abuso (CA01 a CA27)               :  42 testes  [100% PASS]
Suíte Modular de Resiliência e Casos de Borda (Gemini)      : 127 testes  [100% PASS]
----------------------------------------------------------------------------------------
TOTAL GERAL DE TESTES AUTOMATIZADOS                        : 276 testes  [100% PASS]
FALHAS: 0 | ERROS: 0 | IGNORADOS: 0
TEMPO TOTAL DE EXECUÇÃO: ~24 segundos
========================================================================================
```
