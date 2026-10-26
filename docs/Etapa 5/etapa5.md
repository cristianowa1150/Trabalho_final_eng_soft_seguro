# LINK PARA VERIFICAÇÃO DESTE RELATÓRIO COM OS LINKS HTML CLICÁVEIS

https://cristianowa1150.github.io/Trabalho_final_eng_soft_seguro/docs/Etapa%205/etapa5.html

# Etapa 5 — Verificação de Segurança com Ferramenta

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)
**Data da análise:** 26/10/2026
**Ambiente autorizado:** sistema próprio do grupo, executado localmente para fins educacionais

## 23. Objetivo

Esta etapa registra uma verificação de segurança do sistema **Artifício**, limitada ao
código versionado em [`codigo/`](../../codigo/) e às configurações da aplicação. O objetivo
é identificar vulnerabilidades, alertas e configurações inseguras sem explorar
completamente as falhas ou obter acesso indevido.

A análise foi restrita ao sistema próprio do grupo. Nenhum sistema de terceiro foi
testado.

## 24. Ambiente e escopo analisado

O sistema é uma aplicação web Spring Boot com Spring Security, Thymeleaf, Spring Data
JPA e banco H2 em memória. A execução documentada considera o projeto
[`codigo/artificio/`](../../codigo/artificio/), conforme as instruções do
[`README.md`](../../codigo/artificio/README.md).

### Inventário da verificação

Foi feita uma passagem pelos arquivos existentes em [`codigo/`](../../codigo/),
incluindo:

- arquivos de configuração e build (`pom.xml`, Maven Wrapper, `Makefile`,
  propriedades e metadados);
- código Java de inicialização, configuração, autenticação, autorização,
  usuários, artistas, comissões, imagens, auditoria, e-mail e tratamento de erros;
- templates Thymeleaf, CSS e recursos estáticos;
- testes automatizados e recursos de teste;
- documentação técnica do sistema.


### Ferramenta e configuração

| Item | Configuração |
|---|---|
| Tipo de verificação | análise de segurança orientada pelos arquivos de configuração e código do próprio sistema |
| Ferramenta sugerida no roteiro | OWASP ZAP |
| Sessão dinâmica com ZAP | concluída em 26/10/2026, contra `http://127.0.0.1:8080` |
| Verificação HTTP executada | `curl` contra `127.0.0.1:8080` (padrão) e `127.0.0.1:8081` (`prod`), sem exploração ou comandos SQL |
| Fonte das evidências | respostas HTTP, HTML sanitizado, status do endpoint H2 e observação sanitizada do log |
| Perfil observado | configuração padrão de desenvolvimento; o perfil `prod` foi comparado para identificar controles condicionais |
| Limitação | foi executado o `zap-baseline.py`, com rastreamento e análise passiva; não foi executada exploração ativa |

O ZAP foi executado com a imagem `zaproxy/zap-stable`, versão 2.17.0, e os relatórios
foram salvos em <a href="../../evidencias/etapa-5/relatorio-zap.html" target="_blank" rel="noopener noreferrer">relatorio-zap.html</a> e
[`relatorio-zap.json`](../../evidencias/etapa-5/relatorio-zap.json). Os relatórios
foram sanitizados para remover senhas que apareceram em URLs de demonstração.

### Evidências HTML

Os arquivos HTML abaixo podem ser abertos em uma nova aba:

- <a href="../../evidencias/etapa-5/relatorio-zap.html" target="_blank" rel="noopener noreferrer">Relatório completo do OWASP ZAP</a>
- <a href="../../evidencias/etapa-5/default-home.html" target="_blank" rel="noopener noreferrer">Página inicial no perfil padrão</a>
- <a href="../../evidencias/etapa-5/prod-home.html" target="_blank" rel="noopener noreferrer">Página inicial no perfil de produção</a>
- <a href="../../evidencias/etapa-5/A01-h2-console.html" target="_blank" rel="noopener noreferrer">Console H2 no perfil padrão</a>
- <a href="../../evidencias/etapa-5/A01-prod-h2-console.html" target="_blank" rel="noopener noreferrer">Console H2 no perfil de produção</a>

### Comandos para liberar e executar o ZAP no Fedora

O comando abaixo foi tentado automaticamente, mas o Fedora exigiu a senha do
administrador em um terminal interativo. Execute os comandos a seguir no terminal
local:

```bash
# Adiciona o usuário atual ao grupo que pode acessar o Docker.
sudo usermod -aG docker "$USER"

# Atualiza a sessão atual sem precisar reiniciar o computador.
newgrp docker

# Confirma que o Docker pode ser acessado sem sudo.
docker ps

# Baixa a imagem oficial do OWASP ZAP.
docker pull zaproxy/zap-stable

# Executa o ZAP em modo de linha de comando contra a aplicação local.
# A aplicação deve estar rodando em http://127.0.0.1:8080.
docker run --rm --network host \
  -v "$PWD/evidencias/etapa-5:/zap/wrk:rw" \
  zaproxy/zap-stable zap-baseline.py \
  -t http://127.0.0.1:8080 \
  -r relatorio-zap.html \
  -J relatorio-zap.json
```

Após executar o `usermod`, pode ser necessário sair e entrar novamente na sessão do
Fedora. O diretório atual para o último comando deve ser a raiz do repositório.
Os arquivos `relatorio-zap.html` e `relatorio-zap.json` deverão ser mantidos em
[`evidencias/etapa-5/`](../../evidencias/etapa-5/).

## 25. Achados e propostas de correção

Os três achados abaixo foram selecionados por apresentarem impacto direto sobre
confidencialidade, exposição de credenciais ou superfície administrativa. Não foram
incluídos como vulnerabilidades os resultados meramente informativos, os testes
automatizados já cobertos pela suíte do projeto ou os controles de upload que já
validam tamanho, MIME, magic bytes e conteúdo SVG.

| ID | Alerta ou achado | Evidência | Possível impacto | Relação com OWASP ou CWE | Correção proposta |
|---|---|---|---|---|---|
| **A01** | Console H2 e sua rota estão liberados na configuração padrão | [`SecurityConfig.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/config/SecurityConfig.java) libera `/h2-console/**` com `permitAll()` e ignora CSRF; [`application.properties`](../../codigo/artificio/src/main/resources/application.properties) habilita o console | Exposição da interface de administração do banco e possibilidade de consulta ou alteração de dados caso a aplicação seja iniciada fora de um ambiente isolado | OWASP Top 10 2021 **A05 — Security Misconfiguration**; **CWE-489 — Active Debug Code** | Desabilitar o console por padrão, permitir sua ativação somente por propriedade de desenvolvimento e restringi-lo a ambiente local/autenticado. Não ignorar CSRF em ambiente de produção. Adicionar teste que confirme que `/h2-console/**` não é acessível no perfil `prod`. |
| **A02** | Credenciais de demonstração são renderizadas na interface e podem ser enviadas na URL | [`base.html`](../../codigo/artificio/src/main/resources/templates/layout/base.html) mostra e-mail e senha; [`login.html`](../../codigo/artificio/src/main/resources/templates/login.html) preenche a senha no navegador; links usam parâmetros `email` e `senha` em `/login` | Senhas podem aparecer no HTML, histórico do navegador, favoritos, logs de proxy ou cabeçalho `Referer`. Uma senha reutilizada ou uma conta de demonstração exposta pode permitir acesso indevido | OWASP Top 10 2021 **A07 — Identification and Authentication Failures**; **CWE-200 — Exposure of Sensitive Information**; **CWE-598 — Information Exposure Through Query Strings in GET Request** | Remover senhas do HTML e de parâmetros GET. Manter apenas contas descartáveis em ambiente local, usar preenchimento sem revelar o segredo no documento e substituir credenciais demo por fluxo de seed controlado. Garantir que o perfil de produção não renderize nenhum facilitador de login. |
| **A03** | A senha aleatória inicial do administrador é impressa em texto claro no console | [`AdminInitializer.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/config/AdminInitializer.java) imprime o valor da senha com `System.out.println` durante a inicialização | Logs de CI/CD, containers, servidores ou agregadores de log podem reter a credencial e permitir tomada da conta administrativa | OWASP Top 10 2021 **A09 — Security Logging and Monitoring Failures**; **CWE-532 — Insertion of Sensitive Information into Log File** | Não imprimir a senha em logs. Entregar a credencial por mecanismo de provisionamento seguro, exigir redefinição no primeiro acesso ou usar variável/secret manager fora do log. Se uma saída temporária for indispensável em desenvolvimento, torná-la opt-in e impedir sua ativação no perfil `prod`. |

### A01 — Console H2 habilitado e liberado por padrão

**Evidência observada:** o arquivo de propriedades padrão habilita o console H2,
enquanto a configuração de segurança o inclui nas rotas públicas e exclui a rota da
proteção CSRF. O perfil de produção apenas desabilita o console; portanto, o controle
depende da seleção correta do perfil na inicialização.

**Interpretação:** o comportamento é aceitável para inspeção local controlada, mas é
uma configuração insegura como padrão de execução. O risco aumenta quando o JAR é
iniciado sem o perfil `prod`, exposto por um proxy reverso ou executado em uma rede
compartilhada.

**Validação segura sugerida:** iniciar a aplicação apenas em `localhost`, verificar a
resposta de `/h2-console` no perfil padrão e repetir no perfil `prod`, sem enviar
comandos SQL destrutivos. A evidência dinâmica correspondente deve ser salva em
[`evidencias/etapa-5/`](../../evidencias/etapa-5/) após a execução autorizada.

**Resultado executado:** no perfil padrão, `GET /h2-console/` retornou **HTTP 200**;
no perfil `prod`, a mesma requisição retornou **HTTP 404**. Os registros estão em
[`A01-h2-console-headers.txt`](../../evidencias/etapa-5/A01-h2-console-headers.txt)
e [`A01-prod-resultado.txt`](../../evidencias/etapa-5/A01-prod-resultado.txt).

### A02 — Credenciais demo no HTML e na URL

**Evidência observada:** o template base exibe senhas de contas de teste e constrói
links com `senha` como parâmetro de consulta. O template de login também recebe a
senha para preencher o formulário. O recurso é condicionado à propriedade
`artificio.demo.exibir-credenciais`, que está `true` no perfil padrão e `false` no
perfil de produção.

**Interpretação:** a condicional reduz o risco no perfil `prod`, mas não elimina o
problema do perfil padrão. A senha em URL é especialmente inadequada porque URLs são
copiadas e registradas por componentes intermediários com frequência.

**Validação segura sugerida:** abrir a página inicial em um navegador local sem
autenticar, salvar o HTML recebido e verificar se há senhas; em seguida, consultar o
histórico de navegação após usar um link de conta demo. Não é necessário efetuar
tentativas de login de terceiros.

**Resultado executado:** o HTML do perfil padrão apresentou o modal de contas de
teste, e os links continham os parâmetros `email` e `senha`. No perfil `prod`, não
foram encontradas credenciais nos indicadores pesquisados. Os arquivos
[`A02-credenciais-demo-observacao.txt`](../../evidencias/etapa-5/A02-credenciais-demo-observacao.txt)
e [`A02-prod-credenciais-observacao.txt`](../../evidencias/etapa-5/A02-prod-credenciais-observacao.txt)
registram os resultados sem armazenar senhas reais.

### A03 — Senha administrativa em saída de processo

**Evidência observada:** o inicializador cria a senha com `SecureRandom`, armazena
somente o hash no usuário e, em seguida, imprime o valor original em texto claro.
O uso de `BCryptPasswordEncoder` reduz o risco de armazenamento direto no banco, mas
não protege a senha depois que ela é enviada aos logs.

**Interpretação:** a proteção em repouso do banco está correta para esse ponto, porém
o canal de provisionamento da senha não é seguro. Logs devem ser tratados como
dados potencialmente persistentes e acessíveis a mais pessoas e serviços que o banco.

**Validação segura sugerida:** inspecionar somente a saída da inicialização em um
ambiente local descartável e confirmar que o valor não é encaminhado a um
agregador. A senha exibida durante esse teste deve ser revogada e o ambiente
descartado após a verificação.

**Resultado executado:** a saída de inicialização do perfil padrão confirmou a
impressão da credencial administrativa em texto claro. O valor foi omitido
deliberadamente da evidência versionada; a ocorrência e a origem no código estão em
[`A03-admin-log-observacao.txt`](../../evidencias/etapa-5/A03-admin-log-observacao.txt).

## Resultados da sessão do OWASP ZAP

O ZAP identificou dez tipos de alerta no alvo local: um de risco médio, cinco de
risco baixo e quatro informativos. Os três primeiros achados abaixo foram
selecionados para análise detalhada conforme o entregável mínimo da Etapa 5.

| ID | Alerta do ZAP | Evidência | Possível impacto | Relação com OWASP ou CWE | Correção proposta |
|---|---|---|---|---|---|
| **Z01** | Cabeçalho Content-Security-Policy não definido — **Médio**, confiança alta | [`relatorio-zap.json`](../../evidencias/etapa-5/relatorio-zap.json), alerta `10038`, 5 ocorrências; a página inicial não retornou `Content-Security-Policy` | Maior exposição a XSS e injeção de conteúdo caso uma entrada não confiável alcance o HTML ou um recurso incorporado | OWASP Top 10 **A05 — Configuração de segurança inadequada**; CWE-693 | Definir uma política CSP explícita em `SecurityConfig`, começando por `default-src 'self'`, restringindo scripts, estilos, imagens e frames; revisar os recursos inline antes de remover exceções |
| **Z02** | Cookie sem atributo SameSite — **Baixo**, confiança média | <a href="../../evidencias/etapa-5/relatorio-zap.html" target="_blank" rel="noopener noreferrer">relatorio-zap.html</a>, alerta `10054`, 2 ocorrências | O navegador pode enviar o cookie em contextos entre sites, aumentando o risco de requisições cross-site e contribuindo para ataques CSRF | OWASP Top 10 **A05 — Configuração de segurança inadequada**; CWE-1275 | Configurar `SameSite=Lax` ou `Strict`, além de `Secure` em HTTPS e `HttpOnly`; manter e testar a proteção CSRF do Spring Security |
| **Z03** | Cabeçalhos de isolamento entre origens ausentes ou inválidos — **Baixo**, confiança média | [`relatorio-zap.json`](../../evidencias/etapa-5/relatorio-zap.json), alertas `90004-1`, `90004-2` e `90004-3`; ocorrências em páginas públicas e login | Reduz as garantias contra interação indevida entre origens e alguns cenários de vazamento lateral entre documentos e recursos | OWASP Top 10 **A05 — Configuração de segurança inadequada**; CWE-693 | Avaliar e configurar `Cross-Origin-Opener-Policy: same-origin`, `Cross-Origin-Resource-Policy: same-origin` e `Cross-Origin-Embedder-Policy` conforme os recursos necessários; validar compatibilidade antes de aplicar |

### Alertas informativos e resultados não priorizados

O ZAP também apontou `Permissions-Policy` ausente (baixo), comentários
potencialmente informativos, conteúdo não armazenável, identificação de resposta de
sessão e possível atributo HTML controlável. Os quatro últimos foram mantidos como
informativos pelo próprio ZAP e não foram tratados como vulnerabilidades confirmadas.
O possível XSS exige revisão manual; não houve exploração nem evidência de execução de
JavaScript. O alerta de `Permissions-Policy` deve ser tratado junto com os demais
cabeçalhos em uma correção posterior.

## Resultados descartados e limitações

- As validações de upload em [`ValidadorUploadImagem.java`](../../codigo/artificio/src/main/java/com/github/trabalhoengsoftseguro/artificio/imagem/ValidadorUploadImagem.java)
  foram consideradas controles existentes, não um novo alerta: há limite de tamanho,
  lista de MIME permitidos, verificação de assinatura binária e bloqueio de padrões
  perigosos em SVG.
- Os testes automatizados de autorização, CSRF, IDOR/BOLA e integridade de
  comissões foram tratados como evidência de controle já implementado, não como
  alertas descobertos pela ferramenta.
- A análise não prova exploração remota dos três achados. O impacto depende do
  modo de execução, exposição de rede, retenção de logs e configuração do ambiente.
- A sessão executou rastreamento e análise passiva, mas não uma análise ativa
  exploratória. As severidades são as classificações do ZAP e devem ser confirmadas
  por revisão manual antes de qualquer correção abrangente.
- O rastreamento encontrou URLs de login com parâmetros de senha; esses valores foram
  removidos dos relatórios versionados para não preservar credenciais.

## Participação individual

| Integrante | Participação registrada |
|---|---|
| Anna Letycia Fernandes Reis | Revisão dos controles de autenticação, autorização e exposição de credenciais |
| Cristiano Silveira Silva | Inventário do código, configurações de execução e consolidação dos achados |
| Eduardo Nascimento de Souza Rolim | Análise dos impactos e relacionamento com OWASP/CWE |
| Luan Diniz Mazaro Rodovalho | Revisão das propostas de correção e limitações da verificação |
