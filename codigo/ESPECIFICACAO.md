# Especificacao e Planejamento do MVP: Plataforma Artificio

**Sistema:** Artificio (Catalogo de Comissoes e Portfolios para Artistas e Clientes)  
**Equipe:** Anna Letycia, Cristiano Silveira, Eduardo Rolim, Luan Diniz  
**Disciplina:** Engenharia de Software Seguro  
**Objetivo do documento:** Mapear a arquitetura, regras de negocio e funcionalidades do MVP atraves de diagramas Mermaid, detalhando cada operacao do sistema sem inclusao de codigo-fonte nesta etapa de documentacao.

---

## 1. Visao Geral da Arquitetura Web

O Artificio será desenvolvido como uma aplicacao web tradicional em camadas, utilizando Spring Boot MVC e renderizacao server-side com Thymeleaf. O fluxo de requisicoes, controle de sessoes e protecao de rotas ocorre inteiramente no servidor.

```mermaid
flowchart TD
    Browser["1. Cliente (Navegador Web)<br>Requisicoes HTTP GET / POST"]
    Security["2. Seguranca e Acesso (Spring Security)<br>Filtro de Autenticacao Form-Login e Controle de Papeis"]
    
    subgraph CamadaMVC["3. Camada de Apresentacao (Spring MVC + Thymeleaf)"]
        direction LR
        CtrlPub["Modulos Publicos<br>(Catalogo, Artistas, Imagens)"]
        CtrlPriv["Modulos Autenticados<br>(Painel Cliente, Painel Artista, Admin)"]
    end

    subgraph CamadaNegocio["4. Camada de Negocio (Spring Services)"]
        direction LR
        SvcCore["Regras de Negocio<br>(Usuarios, Comissoes, Avaliacoes)"]
        SvcImg["Servico de Imagens<br>(Armazenamento BLOB no H2)"]
    end

    Repos["5. Camada de Persistencia (Spring Data JPA)<br>Repositories e Mapeamento Objeto-Relacional"]
    H2[("6. Banco de Dados Relacional (H2)<br>Tabelas de Dominio e Armazenamento BLOB")]

    Browser -->|Requisicao HTTP| Security
    Security -->|Requisicao Autorizada| CamadaMVC
    CamadaMVC -->|Executa Regras| CamadaNegocio
    CamadaNegocio -->|Persiste e Consulta| Repos
    Repos -->|Comandos SQL| H2
    CamadaMVC -.->|Devolve HTML Renderizado e Bytes de Imagens| Browser
```

---

## 2. Armazenamento de Imagens em BLOB no H2

Para o MVP, o armazenamento das imagens das obras de portfolio e perfis sera feito diretamente como **BLOB (Binary Large Object)** no banco de dados H2 atraves da entidade `ImagemBlob`. Dessa forma, todo o ambiente de desenvolvimento roda de forma auto-contida em memoria/banco local, sem necessidade de configuracao de pastas no sistema de arquivos.

```mermaid
classDiagram
    direction TB

    class ImagemBlob {
        +String id
        +String nomeOriginal
        +String contentType
        +byte[] dados
        +LocalDateTime dataCriacao
    }

    class ImagemBlobRepository {
        +save(ImagemBlob blob) ImagemBlob
        +findById(String id) Optional~ImagemBlob~
    }

    class ImagemService {
        -ImagemBlobRepository repository
        +salvar(byte[] conteudo, String nomeOriginal, String contentType) String
        +recuperar(String id) ImagemBlob
    }

    class ImagemController {
        -ImagemService imagemService
        +exibirImagem(String id) ResponseEntity
    }

    class ArtistaService {
        -ImagemService imagemService
        +adicionarObraPortfolio(Long artistaId, dadosObra, arquivo)
    }

    ImagemService --> ImagemBlobRepository : Persiste e consulta
    ImagemBlobRepository --> ImagemBlob : Mapeia entidade
    ImagemController --> ImagemService : Recupera bytes para o navegador
    ArtistaService --> ImagemService : Grava bytes no cadastro de obras
```

### Fluxo Operacional de Upload e Exibicao de Imagens

```mermaid
sequenceDiagram
    autonumber
    actor Artista as Artista Autenticado
    participant PainelCtrl as PainelArtistaController
    participant ArtistaSvc as ArtistaService
    participant ImgSvc as ImagemService
    participant Repo as ImagemBlobRepository
    actor Visitante as Navegador do Visitante
    participant ImgCtrl as ImagemController

    Note over Artista, Repo: 1. Fluxo de Gravacao (Upload de Obra)
    Artista->>PainelCtrl: POST /painel/artista/portfolio/novo (dados + arquivo binario)
    PainelCtrl->>ArtistaSvc: cadastrarObra(artista, dados, arquivo)
    ArtistaSvc->>ImgSvc: salvar(bytes, nomeArquivo, mimeType)
    ImgSvc->>Repo: save(nova ImagemBlob)
    Repo-->>ImgSvc: Retorna registro com UUID gerado
    ImgSvc-->>ArtistaSvc: Retorna UUID
    ArtistaSvc->>ArtistaSvc: Salva ObraPortfolio associada ao UUID da imagem
    PainelCtrl-->>Artista: Redireciona com obra cadastrada

    Note over Visitante, ImgCtrl: 2. Fluxo de Leitura (Renderizacao da Imagem no Perfil)
    Visitante->>ImgCtrl: GET /imagens/{uuid} (disparado pela tag img)
    ImgCtrl->>ImgSvc: recuperar(uuid)
    ImgSvc->>Repo: findById(uuid)
    Repo-->>ImgSvc: Retorna ImagemBlob com bytes salvos
    ImgSvc-->>ImgCtrl: Retorna array de bytes e mimeType
    ImgCtrl-->>Visitante: Resposta HTTP 200 com header Content-Type e corpo binario
```

---

## 3. Diagrama de Casos de Uso Global (UML)

Mapeamento completo dos atores e das funcionalidades disponiveis no MVP do sistema:

```mermaid
flowchart LR
    subgraph Atores
        Visitante["Visitante (Nao Logado)"]
        Cliente["Cliente"]
        Artista["Artista"]
        Admin["Administrador"]
    end

    subgraph ModuloAutenticacao["Autenticacao e Acesso"]
        UC01["UC01: Cadastrar Conta (Escolhendo Papel)"]
        UC02["UC02: Realizar Login"]
        UC03["UC03: Realizar Logout"]
        UC04["UC04: Promover Cliente para Artista"]
    end

    subgraph ModuloCatalogo["Catalogo e Vitrine"]
        UC05["UC05: Listar Artistas e Destaques"]
        UC06["UC06: Filtrar Artistas por Categoria e Estilo"]
        UC07["UC07: Visualizar Perfil Publico e Portfolio"]
    end

    subgraph ModuloArtista["Area de Gestao do Artista"]
        UC08["UC08: Publicar Obras no Portfolio (Upload BLOB)"]
        UC09["UC09: Cadastrar Tipos de Comissao e Precos Base"]
        UC10["UC10: Visualizar Pedidos Recebidos"]
        UC11["UC11: Aceitar ou Recusar Pedido de Comissao"]
        UC12["UC12: Registrar Entrega da Arte"]
    end

    subgraph ModuloContratacao["Ciclo de Compra e Avaliacao"]
        UC13["UC13: Solicitar Comissao (Briefing e Prazo)"]
        UC14["UC14: Acompanhar Pedidos Solicitados"]
        UC15["UC15: Simular Pagamento do Pedido Aceito (PIX)"]
        UC16["UC16: Aprovar Entrega e Finalizar Pedido"]
        UC17["UC17: Avaliar Servico Concluido (Nota e Comentario)"]
    end

    Visitante --> UC01
    Visitante --> UC02
    Visitante --> UC05
    Visitante --> UC06
    Visitante --> UC07

    Cliente --> UC02
    Cliente --> UC03
    Cliente --> UC05
    Cliente --> UC06
    Cliente --> UC07
    Cliente --> UC13
    Cliente --> UC14
    Cliente --> UC15
    Cliente --> UC16
    Cliente --> UC17

    Artista --> UC02
    Artista --> UC03
    Artista --> UC08
    Artista --> UC09
    Artista --> UC10
    Artista --> UC11
    Artista --> UC12

    Admin --> UC02
    Admin --> UC03
    Admin --> UC04
```

---

## 4. Diagrama de Classes de Dominio (UML)

Entidades mapeadas no Spring Data JPA com seus atributos essenciais, tipos enumerados e relacionamentos de integridade referencial:

```mermaid
classDiagram
    direction TB

    class Role {
        <<enumeration>>
        ROLE_CLIENTE
        ROLE_ARTISTA
        ROLE_ADMIN
    }

    class StatusComissao {
        <<enumeration>>
        SOLICITADA
        RECUSADA
        CANCELADA
        ACEITA
        AGUARDANDO_PAGAMENTO
        PAGO
        EM_ANDAMENTO
        ENTREGUE
        CONCLUIDA
        AVALIADA
    }

    class Usuario {
        +Long id
        +String nome
        +String email
        +String senha
        +Role role
        +String bio
        +String chavePix
        +LocalDateTime dataCriacao
        +boolean isArtista()
        +boolean isAdmin()
    }

    class ObraPortfolio {
        +Long id
        +String titulo
        +String descricao
        +String categoria
        +String imagemId
        +LocalDateTime dataCriacao
        +Usuario artista
    }

    class TipoComissao {
        +Long id
        +String titulo
        +String descricao
        +String categoria
        +BigDecimal precoBase
        +Integer prazoEstimadoDias
        +boolean ativo
        +Usuario artista
    }

    class PedidoComissao {
        +Long id
        +String briefing
        +BigDecimal valorFinal
        +Integer prazoDias
        +String linkEntrega
        +StatusComissao status
        +String codigoPixSimulado
        +LocalDateTime dataSolicitacao
        +LocalDateTime dataConclusao
        +Usuario cliente
        +Usuario artista
        +TipoComissao tipoComissao
    }

    class Avaliacao {
        +Long id
        +Integer nota
        +String comentario
        +LocalDateTime dataAvaliacao
        +PedidoComissao pedidoComissao
        +Usuario cliente
        +Usuario artista
    }

    class ImagemBlob {
        +String id
        +String nomeOriginal
        +String contentType
        +byte[] dados
        +LocalDateTime dataCriacao
    }

    Usuario "1" --> "0..*" ObraPortfolio : publica
    Usuario "1" --> "0..*" TipoComissao : cadastra
    Usuario "1" --> "0..*" PedidoComissao : solicita como cliente
    Usuario "1" --> "0..*" PedidoComissao : executa como artista
    Usuario "1" --> "0..*" Avaliacao : recebe como artista
    TipoComissao "1" --> "0..*" PedidoComissao : especifica
    PedidoComissao "1" --> "0..1" Avaliacao : gera apos conclusao
    Usuario "1" --> "1" Role : possui papel
    PedidoComissao "1" --> "1" StatusComissao : transita estado
    ObraPortfolio "1" --> "1" ImagemBlob : referencia arquivo
```

---

## 5. Detalhamento dos Fluxos por Funcionalidade

### 5.1 Funcionalidade 1: Cadastro e Autenticacao (Login e Logout)

Regra de negocio fundamental: o papel inicial (`ROLE_CLIENTE` ou `ROLE_ARTISTA`) e definido estritamente na criacao da conta. A autenticacao utiliza o mecanismo nativo de formulario do Spring Security.

```mermaid
sequenceDiagram
    autonumber
    actor Usuario as Visitante / Usuario
    participant AuthCtrl as AuthController
    participant SecFilter as Spring Security
    participant UserSvc as UsuarioService
    participant UserRepo as UsuarioRepository

    Note over Usuario, UserRepo: A) Cadastro de Conta
    Usuario->>AuthCtrl: GET /cadastro
    AuthCtrl-->>Usuario: Exibe formulario com campos: nome, email, senha, perfil (Cliente/Artista)
    Usuario->>AuthCtrl: POST /cadastro (dados preenchidos)
    AuthCtrl->>UserSvc: registrarUsuario(dto)
    UserSvc->>UserSvc: Aplica algoritmo de hash na senha (BCrypt)
    UserSvc->>UserRepo: save(Usuario com papel selecionado)
    AuthCtrl-->>Usuario: Redireciona para /login com aviso de sucesso

    Note over Usuario, UserRepo: B) Login e Redirecionamento por Papel
    Usuario->>AuthCtrl: GET /login
    AuthCtrl-->>Usuario: Exibe tela de autenticacao
    Usuario->>SecFilter: POST /login (email, senha)
    SecFilter->>UserRepo: findByEmail(email)
    UserRepo-->>SecFilter: Retorna entidade Usuario
    SecFilter->>SecFilter: Valida correspondencia do hash da senha
    alt Sucesso no Login
        SecFilter->>SecFilter: Cria Sessao de Usuario no Servidor
        alt Papel e ROLE_ARTISTA
            SecFilter-->>Usuario: Redireciona para /painel/artista/pedidos
        else Papel e ROLE_CLIENTE
            SecFilter-->>Usuario: Redireciona para /
        else Papel e ROLE_ADMIN
            SecFilter-->>Usuario: Redireciona para /admin/usuarios
        end
    else Falha de Autenticacao
        SecFilter-->>Usuario: Redireciona para /login?error=true
    end
```

---

### 5.2 Funcionalidade 2: Promocao Administrativa de Usuario

Para mitigar alteracoes indevidas de privilegio (como mapeado nos riscos de abuso), um cliente nao pode se transformar em artista por edicao propria de perfil. Essa acao e restrita a administradores.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Administrador
    participant AdminCtrl as AdminController
    participant UserSvc as UsuarioService
    participant UserRepo as UsuarioRepository
    actor Cliente as Cliente

    Admin->>AdminCtrl: GET /admin/usuarios
    AdminCtrl->>UserRepo: findAll()
    UserRepo-->>AdminCtrl: Lista de contas cadastradas
    AdminCtrl-->>Admin: Exibe tabela de usuarios e seus respectivos papeis
    Admin->>AdminCtrl: POST /admin/usuarios/{id}/promover-artista
    AdminCtrl->>UserSvc: promoverParaArtista(id)
    UserSvc->>UserRepo: findById(id)
    UserRepo-->>UserSvc: Retorna usuario
    UserSvc->>UserSvc: Altera papel para ROLE_ARTISTA
    UserSvc->>UserRepo: save(usuario atualizado)
    AdminCtrl-->>Admin: Redireciona com aviso de promocao concluida
    Note over Cliente: No proximo acesso do usuario, a barra de navegacao passara a exibir as opcoes do Painel do Artista
```

---

### 5.3 Funcionalidade 3: Catalogo, Busca Facetada e Perfil Publico

Permite a navegacao publica por visitantes e clientes, com pesquisa por categorias de ilustracao e visualizacao da bio, galeria de trabalhos e precos de comissoes.

```mermaid
sequenceDiagram
    autonumber
    actor Visitante as Visitante / Cliente
    participant HomeCtrl as HomeController
    participant ArtistaCtrl as ArtistaController
    participant ArtistaSvc as ArtistaService
    participant ArtistaRepo as UsuarioRepository

    Visitante->>HomeCtrl: GET / (ou GET /?categoria=PixelArt&busca=anime)
    HomeCtrl->>ArtistaSvc: buscarArtistasComFiltros(categoria, termoBusca)
    ArtistaSvc->>ArtistaRepo: Consulta artistas ativos e suas comissoes
    ArtistaRepo-->>ArtistaSvc: Retorna lista de artistas correspondentes
    HomeCtrl-->>Visitante: Renderiza index.html com cards dos artistas e tags

    Visitante->>ArtistaCtrl: GET /artistas/{id}
    ArtistaCtrl->>ArtistaSvc: obterPerfilCompleto(id)
    ArtistaSvc-->>ArtistaCtrl: Retorna dados do artista, galeria do portfolio e tipos de comissao
    ArtistaCtrl-->>Visitante: Renderiza perfil.html com vitrine completa e botoes de solicitacao
```

---

### 5.4 Funcionalidade 4: Gestao do Artista (Portfolio e Servicos)

Permite ao artista manter seu catalogo atualizado, publicando novas obras no portfolio com upload de imagem e cadastrando servicos com precos e prazos base.

```mermaid
sequenceDiagram
    autonumber
    actor Artista as Artista Autenticado
    participant PainelCtrl as PainelArtistaController
    participant ArtistaSvc as ArtistaService
    participant ImgSvc as ImagemService

    Note over Artista, ImgSvc: A) Publicacao de Obra no Portfolio
    Artista->>PainelCtrl: GET /painel/artista/portfolio
    PainelCtrl-->>Artista: Exibe tela com obras publicadas e formulario de nova obra
    Artista->>PainelCtrl: POST /painel/artista/portfolio/novo (titulo, categoria, arquivoImagem)
    PainelCtrl->>ImgSvc: salvar(bytes, nomeArquivo, mimeType)
    ImgSvc-->>PainelCtrl: Retorna UUID da imagem gravada
    PainelCtrl->>ArtistaSvc: associarObraAoArtista(artistaLogado, dados, uuid)
    PainelCtrl-->>Artista: Redireciona para portfolio com mensagem de sucesso

    Note over Artista, ImgSvc: B) Cadastro de Tipo de Comissao
    Artista->>PainelCtrl: GET /painel/artista/servicos
    PainelCtrl-->>Artista: Exibe tabela de servicos oferecidos
    Artista->>PainelCtrl: POST /painel/artista/servicos/novo (titulo, descricao, precoBase, prazoDias)
    PainelCtrl->>ArtistaSvc: salvarTipoComissao(artistaLogado, dados)
    PainelCtrl-->>Artista: Redireciona para lista de servicos atualizada
```

---

### 5.5 Funcionalidade 5: Ciclo de Compra e Simulacao Visual de Pagamento (PIX)

Fluxo critico de contratacao: cliente envia o briefing, artista aceita as condicoes e o sistema disponibiliza a tela visual de simulacao de pagamento.

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente Autenticado
    actor Artista as Artista Autenticado
    participant ComissaoCtrl as ComissaoController
    participant ComissaoSvc as ComissaoService
    participant PedidoRepo as PedidoComissaoRepository

    Cliente->>ComissaoCtrl: GET /comissoes/solicitar/{tipoComissaoId}
    ComissaoCtrl-->>Cliente: Exibe formulario com dados do servico e campo de briefing
    Cliente->>ComissaoCtrl: POST /comissoes/solicitar (briefing, prazoDesejado)
    ComissaoCtrl->>ComissaoSvc: criarSolicitacao(cliente, tipoId, briefing)
    ComissaoSvc->>PedidoRepo: save(Pedido com status SOLICITADA)
    ComissaoCtrl-->>Cliente: Redireciona para /painel/cliente/pedidos

    Note over Artista, PedidoRepo: Avaliacao da Proposta pelo Artista
    Artista->>ComissaoCtrl: GET /painel/artista/pedidos
    ComissaoCtrl-->>Artista: Lista de pedidos pendentes
    Artista->>ComissaoCtrl: POST /comissoes/{id}/aceitar
    ComissaoCtrl->>ComissaoSvc: aceitarPedido(id, artistaLogado)
    ComissaoSvc->>ComissaoSvc: Gera chave e codigo PIX simulados
    ComissaoSvc->>PedidoRepo: update status: AGUARDANDO_PAGAMENTO
    ComissaoCtrl-->>Artista: Atualiza painel do artista

    Note over Cliente, PedidoRepo: Simulacao Visual de Pagamento
    Cliente->>ComissaoCtrl: GET /comissoes/{id}/pagamento
    ComissaoCtrl-->>Cliente: Exibe tela com QR Code visual simulado, chave copia-e-cola e valor
    Cliente->>ComissaoCtrl: POST /comissoes/{id}/confirmar-pagamento
    ComissaoCtrl->>ComissaoSvc: registrarPagamentoSimulado(id, clienteLogado)
    ComissaoSvc->>PedidoRepo: update status: PAGO (pronto para producao)
    ComissaoCtrl-->>Cliente: Redireciona com aviso de pagamento confirmado
```

---

### 5.6 Funcionalidade 6: Entrega, Aprovacao e Avaliacao

Conclusao da encomenda: apos a producao, o artista envia o link ou entrega do trabalho, o cliente confere e aprova, habilitando o formulario de avaliacao.

```mermaid
sequenceDiagram
    autonumber
    actor Artista as Artista Autenticado
    actor Cliente as Cliente Autenticado
    participant ComissaoCtrl as ComissaoController
    participant ComissaoSvc as ComissaoService
    participant AvaliacaoSvc as AvaliacaoService
    participant PedidoRepo as PedidoComissaoRepository

    Note over Artista, PedidoRepo: Entrega da Arte
    Artista->>ComissaoCtrl: POST /comissoes/{id}/entregar (linkEntrega)
    ComissaoCtrl->>ComissaoSvc: registrarEntrega(id, linkEntrega, artistaLogado)
    ComissaoSvc->>PedidoRepo: update status: ENTREGUE
    ComissaoCtrl-->>Artista: Confirma envio da entrega

    Note over Cliente, PedidoRepo: Aprovacao e Conclusao
    Cliente->>ComissaoCtrl: GET /comissoes/{id}
    ComissaoCtrl-->>Cliente: Exibe detalhes do pedido com link da arte entregue
    Cliente->>ComissaoCtrl: POST /comissoes/{id}/aprovar
    ComissaoCtrl->>ComissaoSvc: aprovarConclusao(id, clienteLogado)
    ComissaoSvc->>PedidoRepo: update status: CONCLUIDA
    ComissaoCtrl-->>Cliente: Exibe tela de comissao concluida com botao 'Avaliar Artista'

    Note over Cliente, AvaliacaoSvc: Registro da Avaliacao
    Cliente->>ComissaoCtrl: GET /comissoes/{id}/avaliar
    ComissaoCtrl-->>Cliente: Exibe formulario com seletor de estrelas (1 a 5) e comentario
    Cliente->>ComissaoCtrl: POST /comissoes/{id}/avaliar (nota, comentario)
    ComissaoCtrl->>AvaliacaoSvc: salvarAvaliacao(id, nota, comentario, clienteLogado)
    AvaliacaoSvc->>PedidoRepo: update status: AVALIADA
    AvaliacaoSvc->>AvaliacaoSvc: Recalcula nota media do perfil do artista
    ComissaoCtrl-->>Cliente: Redireciona para perfil do artista com avaliacao publicada
```

---

## 6. Diagrama de Estados do Pedido de Comissao

Mapeamento rigoroso de todas as transicoes de estado possiveis no ciclo de vida da encomenda:

```mermaid
stateDiagram-v2
    [*] --> SOLICITADA: Cliente submete briefing e prazo

    SOLICITADA --> RECUSADA: Artista recusa proposta
    SOLICITADA --> CANCELADA: Cliente cancela antes da analise
    SOLICITADA --> AGUARDANDO_PAGAMENTO: Artista aceita a comissao

    AGUARDANDO_PAGAMENTO --> CANCELADA: Cliente desiste da compra
    AGUARDANDO_PAGAMENTO --> PAGO: Cliente aciona simulador de pagamento PIX

    PAGO --> EM_ANDAMENTO: Artista inicia a ilustracao
    EM_ANDAMENTO --> ENTREGUE: Artista submete link do arquivo final

    ENTREGUE --> EM_ANDAMENTO: Cliente solicita revisao / ajuste
    ENTREGUE --> CONCLUIDA: Cliente aprova o resultado entregue

    CONCLUIDA --> AVALIADA: Cliente submete nota e comentario

    RECUSADA --> [*]
    CANCELADA --> [*]
    AVALIADA --> [*]
```

---

## 7. Mapa de Navegacao e Telas da Aplicacao

Estrutura visual das rotas e interconexoes de tela no modelo Spring MVC + Thymeleaf:

```mermaid
flowchart TD
    subgraph Publico[Area Publica]
        Home["/ (Catalogo e Vitrine)"]
        PerfilArtista["/artistas/{id} (Perfil, Portfolio e Servicos)"]
        Login["/login (Formulario de Acesso)"]
        Cadastro["/cadastro (Registro Cliente ou Artista)"]
    end

    subgraph AreaCliente[Painel do Cliente - ROLE_CLIENTE]
        SolicitarComissao["/comissoes/solicitar/{tipoId}"]
        MeusPedidosCliente["/painel/cliente/pedidos"]
        DetalhePedidoCliente["/comissoes/{id} (Acompanhamento)"]
        TelaPagamento["/comissoes/{id}/pagamento (Simulador PIX)"]
        TelaAvaliacao["/comissoes/{id}/avaliar (Nota e Comentario)"]
    end

    subgraph AreaArtista[Painel do Artista - ROLE_ARTISTA]
        PainelPedidosArtista["/painel/artista/pedidos (Fila de Comissoes)"]
        GerenciarPortfolio["/painel/artista/portfolio (Upload de Obras)"]
        GerenciarServicos["/painel/artista/servicos (Tipos de Comissao)"]
    end

    subgraph AreaAdmin[Administracao - ROLE_ADMIN]
        PainelAdmin["/admin/usuarios (Lista de Usuarios e Promocao)"]
    end

    Home --> PerfilArtista
    Home --> Login
    Home --> Cadastro
    Cadastro --> Login
    Login --> Home
    Login --> PainelPedidosArtista
    Login --> PainelAdmin

    PerfilArtista --> SolicitarComissao
    SolicitarComissao --> MeusPedidosCliente
    MeusPedidosCliente --> DetalhePedidoCliente
    DetalhePedidoCliente --> TelaPagamento
    DetalhePedidoCliente --> TelaAvaliacao

    PainelPedidosArtista --> DetalhePedidoCliente
```

---

## 8. Roteiro de Validacao Manual do MVP

Para atestar o funcionamento correto do fluxo feliz e dos casos de uso implementados:

1. **Inicializacao da Aplicacao:**
   - Executar `./mvnw spring-boot:run` a partir da pasta `codigo/artificio`.
   - Acessar `http://localhost:8080/` e constatar a vitrine inicial populada pelos dados de exemplo da classe `DataInitializer`.

2. **Validacao de Imagens (H2 BLOB):**
   - Verificar se as imagens dos cards de artistas e obras carregam corretamente a partir das URLs `/imagens/{id}`.

3. **Cadastro e Restricao de Papel:**
   - Cadastrar um novo usuario com o perfil `CLIENTE`.
   - Tentar acessar diretamente `/painel/artista/portfolio` e confirmar que o Spring Security bloqueia com erro 403 (Acesso Negado).

4. **Ciclo Completo de Contratacao:**
   - Navegar ate o perfil de um artista cadastrado.
   - Selecionar um tipo de comissao e preencher o briefing.
   - Logar com a conta do respectivo artista, acessar a fila de pedidos e aceitar a comissao.
   - Retornar ao usuario cliente, acessar a tela de pagamento simulado e confirmar a transacao PIX.
   - Retornar ao artista, registrar a entrega da obra.
   - Retornar ao cliente, aprovar o resultado final e enviar nota e comentario de avaliacao.
   - Acessar novamente o perfil publico do artista e constatar que a avaliacao e a nota media foram computadas.

5. **Promocao de Usuario por Administrador:**
   - Logar como `admin@artificio.local`.
   - Acessar `/admin/usuarios` e promover uma conta comum de cliente para artista.
   - Deslogar, logar com a conta promovida e verificar se as opcoes de gestao de portfolio e servicos foram liberadas.
