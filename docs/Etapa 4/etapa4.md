# Etapa 4 — Código Seguro e Testes de Segurança

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)  
**Integrantes:**
- Anna Letycia Fernandes Reis
- Cristiano Silveira Silva
- Eduardo Nascimento de Souza Rolim
- Luan Diniz Mazaro Rodovalho  

---

## 20. Objetivo

O objetivo desta etapa é demonstrar como os riscos prioritários identificados na [Etapa 2](../Etapa%202/Etapa%202.md) e as decisões de arquitetura estabelecidas na [Etapa 3](../Etapa%203/Etapa3.md) foram materializados em práticas concretas de implementação e testes automatizados de segurança no código-fonte da aplicação **Artifício** ([`codigo/artificio/`](../../codigo/artificio/)).

Conforme orientado no roteiro pedagógico, foram selecionadas **duas práticas de código seguro**, acompanhadas da definição e execução de **testes de segurança antes da implementação** (abordagem *Security Test-First* / TDD voltado a segurança), abrangendo casos de uso válidos, tentativas maliciosas/não autorizadas e a verificação do comportamento resiliente esperado, fundamentado em referências normativas da **OWASP** (*Cheat Sheet Series* e *ASVS 4.0*).

---

## 21. Entregável Mínimo

A tabela a seguir sumariza as duas práticas de código seguro selecionadas, sua rastreabilidade direta com as etapas anteriores e suas respectivas referências normativas:

| Prática | Nome da Prática | Riscos Tratados | Requisitos e Decisões de Arquitetura | Referência OWASP | Implementação no Repositório |
| :---: | :--- | :---: | :---: | :---: | :--- |
| **Prática 1** | **Controle de Autorização e Isolamento por Posse (*Ownership / Broken Object Level Authorization - BOLA*)** | **R28** (Acesso indevido a funções administrativas)<br>**R29** (Elevação de privilégio)<br>**R30** (Manipulação/Acesso a recursos de terceiros) | **RNF03** (Controle de acesso por papel e recurso)<br>**Decisão 1** (Autorização centralizada no backend)<br>**Decisão 2** (Controle de acesso a arquivos e pedidos) | • [OWASP Authorization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html)<br>• OWASP ASVS v4.0 (V4 - Access Control) | [`SecurityConfig.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/config/SecurityConfig.java)<br>[`ComissaoService.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/comissao/ComissaoService.java)<br>[`UsuarioService.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/usuario/UsuarioService.java) |
| **Prática 2** | **Validação Estrita de Entrada, Integridade Transacional e Armazenamento Criptográfico Seguro de Credenciais** | **R01** (Comprometimento de conta)<br>**R06** (Alteração indevida de valores e condições de contratação)<br>**R18** (Vazamento de credenciais em repouso) | **RNF01** (Autenticação robusta)<br>**RNF02** (Armazenamento seguro de senhas com hashing salgado)<br>**RNF05** (Proteção de dados dos usuários) | • [OWASP Input Validation Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html)<br>• [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) | [`CadastroDto.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/usuario/CadastroDto.java)<br>[`SolicitacaoComissaoDto.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/comissao/SolicitacaoComissaoDto.java)<br>[`UsuarioService.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/usuario/UsuarioService.java) |

---

## 21.1 Prática de Código Seguro 1: Controle de Autorização e Isolamento por Posse (*Ownership*)

### 1. Risco e Requisitos Relacionados
- **Riscos de Origem:** 
  - **R28:** Usuário comum tenta invocar diretamente rotas e operações administrativas de supervisão e moderação.
  - **R29:** Usuário tenta manipular payloads de cadastro/atualização para injetar `ROLE_ADMIN` e elevar privilégios.
  - **R30:** Usuário (ou artista malicioso) manipula identificadores de chave estrangeira (`pedidoId`, `artistaId`) para aceitar, alterar ou cancelar encomendas pertencentes a outro usuário.
- **Requisito de Arquitetura:** **RNF03** e **Decisão 1** da Etapa 3 — *Autorização centralizada no Backend aplicando RBAC + Verificação de Posse (*Ownership*)*.

### 2. Testes de Segurança Definidos Antes da Implementação
Foram implementados testes automatizados cobrindo acessos legítimos, tentativas maliciosas e tentativas de usurpação de autoridade horizontal e vertical:

| ID Teste | Natureza | Cenário / Entrada | Ação Realizada | Resultado Esperado | Teste Automatizado no Repositório |
| :---: | :--- | :--- | :--- | :--- | :--- |
| **TS01** | Malicioso / Não Autorizado | Visitante anônimo requisita rota `/admin/usuarios` | `GET /admin/usuarios` sem sessão ativa | **Bloqueio e redirecionamento (302)** imediato para `/login`. Nenhuma informação de usuários é renderizada. | [`AdminUsuarioControllerTest.java#L27-L33`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/AdminUsuarioControllerTest.java#L27-L33) |
| **TS02** | Malicioso / Não Autorizado | Usuário autenticado com perfil `ROLE_CLIENTE` tenta acessar o painel de administração | `GET /admin/usuarios` com `@WithMockUser(roles={"CLIENTE"})` | **Acesso Negado (HTTP 403 Forbidden)** pelo filtro do Spring Security. | [`AdminUsuarioControllerTest.java#L35-L41`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/AdminUsuarioControllerTest.java#L35-L41) |
| **TS03** | Malicioso / BOLA Horizontal | Artista B (`id: 99`) tenta aceitar uma proposta de comissão destinada exclusivamente ao Artista A (`id: 20`) | Invocação de `comissaoService.aceitarPedido(pedidoId=100L, artistaId=99L)` | O sistema lança `IllegalArgumentException("Apenas o artista destinatário pode aceitar este pedido")` e o status da comissão permanece intacto (`SOLICITADA`). | [`ComissaoServiceTest.java#L121-L134`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/comissao/ComissaoServiceTest.java#L121-L134) |
| **TS04** | Válido / Autorizado | Administrador com perfil `ROLE_ADMIN` acessa a supervisão de contas | `GET /admin/usuarios` com `@WithMockUser(roles={"ADMIN"})` | **Acesso Permitido (HTTP 200 OK)**, retornando o modelo populado e a view `admin/usuarios`. | [`AdminUsuarioControllerTest.java#L51-L60`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/AdminUsuarioControllerTest.java#L51-L60) |

#### Evidência de Código dos Testes Pré-Implementação:
```java
@Test
@WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
@DisplayName("Deve negar acesso (403 Forbidden) para cliente comum no painel administrativo")
void deveNegarAcessoParaClienteComum() throws Exception {
    mockMvc.perform(get("/admin/usuarios"))
            .andExpect(status().isForbidden());
}

@Test
@DisplayName("Deve impedir que outro artista aceite pedido destinado a terceiro (Defesa contra BOLA)")
void deveRejeitarAceitePorArtistaDiferente() {
    PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
    pedido.setId(100L);
    when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

    // Artista com ID 99 tentando aceitar pedido destinado ao artista ID 20
    assertThrows(IllegalArgumentException.class, () -> service.aceitarPedido(100L, 99L));
    assertEquals(StatusComissao.SOLICITADA, pedido.getStatus());
}
```

### 3. Implementação e Código Seguro
A mitigação foi estruturada em duas camadas complementares:

#### A) Configuração Centralizada de Rotas com Spring Security (`SecurityConfig.java`)
Garante que privilégios de rotas administrativas e painéis restritos sejam avaliados no nível de infraestrutura web antes de alcançar qualquer controlador:
```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/artistas/**", "/imagens/**", "/login", "/cadastro", "/css/**", "/js/**", "/error").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/painel/artista/**").hasRole("ARTISTA")
        .requestMatchers("/painel/cliente/**", "/comissoes/**").authenticated()
        .anyRequest().authenticated()
);
```

#### B) Validação Contextual de Propriedade na Camada de Negócio (`ComissaoService.java`)
Garante que mesmo que um usuário malicioso forje requisições POST para endpoints de alteração de estado com identificadores de terceiros, a camada de serviço valida a correspondência estrita com a identidade autenticada:
```java
@Transactional
public PedidoComissao aceitarPedido(Long pedidoId, Long artistaId) {
    PedidoComissao pedido = buscarPorId(pedidoId);

    // Validação estrita de posse (Ownership)
    if (!pedido.getArtista().getId().equals(artistaId)) {
        throw new IllegalArgumentException("Apenas o artista destinatário pode aceitar este pedido");
    }
    if (pedido.getStatus() != StatusComissao.SOLICITADA) {
        throw new IllegalStateException("Apenas pedidos em estado SOLICITADA podem ser aceitos");
    }

    pedido.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
    pedido.setCodigoPixSimulado("00020126580014BR.GOV.BCB.PIX0136" + UUID.randomUUID().toString().replace("-", ""));
    return pedidoRepository.save(pedido);
}
```

### 4. Resultado Esperado
- Usuários não autorizados recebem códigos de erro HTTP 403 (ou 302 para login), sem exposição de dados de auditoria ou telas sensíveis.
- Tentativas de ataque BOLA (*Broken Object Level Authorization*) são bloqueadas e geram exceções de validação no backend, impedindo qualquer modificação em banco de dados.

### 5. Referência OWASP
- **OWASP Authorization Cheat Sheet:** Diretrizes de controle de acesso contextual e validação de posse sobre cada objeto manipulado no lado servidor.
- **OWASP ASVS 4.0 - Item V4.1.1:** *"Verify that the application enforces access control rules on a trusted service layer, especially for data queries and business operations."*

---

## 21.2 Prática de Código Seguro 2: Validação Estrita de Entrada, Integridade Transacional e Hashing de Senhas

### 1. Risco e Requisitos Relacionados
- **Riscos de Origem:**
  - **R01:** Acesso não autorizado por vazamento ou quebra de credenciais fracas ou armazenadas em texto claro.
  - **R06:** Alteração arbitrária de preços e prazos em contratações através de manipulação de parâmetros no formulário HTML.
  - **R29:** Injeção direta de `ROLE_ADMIN` no formulário público de auto-cadastro.
- **Requisito de Arquitetura:** **RNF01**, **RNF02** e **Decisão 1** da Etapa 3 — *Uso de DTOs tipados com Bean Validation, transformação criptográfica via BCrypt e bloqueio de parametrização insegura no backend*.

### 2. Testes de Segurança Definidos Antes da Implementação
Foram implementados testes unitários e de integração validando os limites de entrada, sanitização e blindagem de privilégios:

| ID Teste | Natureza | Cenário / Entrada | Ação Realizada | Resultado Esperado | Teste Automatizado no Repositório |
| :---: | :--- | :--- | :--- | :--- | :--- |
| **TS05** | Malicioso / Entrada Inválida | Submissão de cadastro com campos em branco, e-mail malformado e senha curta (< 6 caracteres) | `POST /cadastro` com payload malicioso | **Rejeição com erros de validação (Bean Validation)**. Nenhum registro é persistido no banco e a view reexibe os erros de campo. | [`AuthControllerTest.java#L73-L85`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/AuthControllerTest.java#L73-L85) |
| **TS06** | Malicioso / Elevação de Privilégio | Atacante submete formulário de cadastro forçando o parâmetro `role=ROLE_ADMIN` | Invocação de `usuarioService.registrarUsuario(dtoComAdmin)` | **Rejeição imediata com `IllegalArgumentException`** ("Não é permitido cadastrar administradores diretamente"). Operação de persistência abortada. | [`UsuarioServiceTest.java#L84-L91`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/UsuarioServiceTest.java#L84-L91) |
| **TS07** | Malicioso / Manipulação de Preço | Usuário envia solicitação de comissão forjando valores financeiros na requisição | Submissão de `SolicitacaoComissaoDto` apenas com `tipoComissaoId` e `briefing` | O sistema **ignora quaisquer valores externos** e preenche `precoBase` e `prazoDias` exclusivamente a partir do registro oficial no banco de dados. | [`ComissaoServiceTest.java#L59-L74`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/comissao/ComissaoServiceTest.java#L59-L74) |
| **TS08** | Válido / Hashing Seguro | Usuário cadastra senha "senha123" | Invocação de `usuarioService.registrarUsuario(dtoValido)` | Senha é transformada com algoritmo adaptativo **BCrypt** (`$2a$10$...`). A senha em texto plano jamais alcança o repositório. | [`UsuarioServiceTest.java#L42-L61`](../../codigo/artificio/src/test/java/com/github/trabalhoengsoftseguro/artificio/usuario/UsuarioServiceTest.java#L42-L61) |

#### Evidência de Código dos Testes Pré-Implementação:
```java
@Test
@DisplayName("Deve rejeitar atribuicao direta de papel de administrador no cadastro")
void deveRejeitarCadastroDiretoDeAdmin() {
    CadastroDto dto = new CadastroDto("Hacker", "hacker@artificio.local", "senha123", Role.ROLE_ADMIN, null, null);

    assertThrows(IllegalArgumentException.class, () -> service.registrarUsuario(dto));
    verify(usuarioRepository, never()).save(any(Usuario.class));
}

@Test
@DisplayName("Deve registrar novo usuario criptografando a senha com BCrypt")
void deveRegistrarUsuarioComSucesso() {
    CadastroDto dto = new CadastroDto("Alice", "alice@artificio.local", "senha123", Role.ROLE_CLIENTE, "Bio teste", null);

    when(usuarioRepository.existsByEmail("alice@artificio.local")).thenReturn(false);
    when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$hashedPassword");
    when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Usuario criado = service.registrarUsuario(dto);

    assertNotNull(criado);
    assertEquals("$2a$10$hashedPassword", criado.getSenha());
    verify(usuarioRepository).save(any(Usuario.class));
}
```

### 3. Implementação e Código Seguro

#### A) DTO Seguro com Anotações Declarativas de Validação (`CadastroDto.java`)
Impede o recebimento de dados truncados, vazios ou fora do padrão esperado:
```java
public record CadastroDto(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String senha,

        @NotNull(message = "O papel é obrigatório")
        Role role,

        String bio,
        String chavePix
) {}
```

#### B) Regras Defensivas e Criptografia no Serviço (`UsuarioService.java`)
Garante o hashing seguro com BCrypt e a impossibilidade de auto-atribuição de perfis administrativos:
```java
@Transactional
public Usuario registrarUsuario(CadastroDto dto) {
    if (dto == null) {
        throw new IllegalArgumentException("Dados de cadastro não podem ser nulos");
    }
    if (usuarioRepository.existsByEmail(dto.email())) {
        throw new IllegalArgumentException("E-mail já cadastrado na plataforma");
    }
    if (dto.role() == Role.ROLE_ADMIN) {
        throw new IllegalArgumentException("Não é permitido cadastrar administradores diretamente");
    }

    String senhaCriptografada = passwordEncoder.encode(dto.senha());
    Usuario novoUsuario = new Usuario(
            dto.nome(),
            dto.email().trim().toLowerCase(),
            senhaCriptografada,
            dto.role(),
            dto.bio(),
            dto.chavePix()
    );

    return usuarioRepository.save(novoUsuario);
}
```

#### C) Blindagem contra Manipulação de Preços (`ComissaoService.java`)
O cliente apenas indica a modalidade de comissão desejada (`tipoComissaoId`). O preço e o prazo são extraídos diretamente do banco de dados oficial, tornando inócua qualquer alteração de campos na interface web:
```java
@Transactional
public PedidoComissao criarSolicitacao(Long clienteId, SolicitacaoComissaoDto dto) {
    TipoComissao tipo = tipoComissaoRepository.findById(dto.tipoComissaoId())
            .orElseThrow(() -> new IllegalArgumentException("Tipo de comissão não encontrado"));

    // O preço e prazo são fixados estritamente pelo cadastro do artista no banco
    PedidoComissao pedido = new PedidoComissao(
            dto.briefing(),
            tipo.getPrecoBase(),
            tipo.getPrazoEstimadoDias(),
            cliente,
            tipo.getArtista(),
            tipo
    );
    return pedidoRepository.save(pedido);
}
```

### 4. Resultado Esperado
- Entradas maliciosas ou fora dos padrões do DTO são bloqueadas imediatamente pelo Spring MVC, sem sobrecarga ou poluição do banco de dados.
- Credenciais são protegidas com algoritmos de derivação de chave com salt aleatório embutido (*BCrypt*), mitigando ataques de dicionário e tabelas rainbow mesmo em caso de extração física do banco de dados.
- Parâmetros críticos de negócio (preços, prazos e papéis de segurança) são calculados e atribuídos exclusivamente pela aplicação servidora.

### 5. Referência OWASP
- **OWASP Input Validation Cheat Sheet:** Princípio da lista positiva (*allowlist*) e validação sintática e semântica antes de qualquer processamento negocial.
- **OWASP Password Storage Cheat Sheet:** Recomendação prioritária de algoritmos de hashing adaptativos (BCrypt, Argon2, PBKDF2) com fator de trabalho adequado.

---

## 22. Critérios de Avaliação e Evidências de Execução

A implementação foi realizada na prática dentro da base de código do projeto em [`codigo/artificio/`](../../codigo/artificio/), cumprindo rigorosamente os critérios da Etapa 4:

1. **Continuidade com a Arquitetura da Etapa 3:** As decisões de autorização em duas etapas (RBAC + Ownership) e isolamento da persistência de dados sensíveis foram traduzidas fielmente no código do Spring Security e dos serviços.
2. **Definição de Testes de Segurança Antes da Implementação:** Os testes unitários e de integração foram elaborados para simular ataques e casos válidos, servindo como especificação executável dos requisitos.
3. **Execução Automatizada:** Todos os testes de segurança podem ser reproduzidos e validados através do comando padrão do Maven Wrapper:
   ```bash
   cd codigo/artificio
   ./mvnw test
   ```
4. **Alinhamento com Padrões OWASP:** Todas as práticas fundamentam-se no *OWASP Top 10*, *OWASP ASVS v4.0* e nas *Cheat Sheets* oficiais de Autorização, Validação e Armazenamento de Senhas.
