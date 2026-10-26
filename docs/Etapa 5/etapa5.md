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
| Sessão dinâmica com ZAP | não executada neste ambiente; o executável não está disponível |
| Fonte das evidências | trechos versionados do código e das configurações |
| Perfil observado | configuração padrão de desenvolvimento; o perfil `prod` foi comparado para identificar controles condicionais |
| Limitação | sem captura HTTP, spider, passive scan ou active scan do ZAP; os achados abaixo são candidatos confirmados por inspeção estática e devem ser revalidados em uma sessão dinâmica autorizada |

O roteiro recomenda o ZAP, mas não permite afirmar que uma execução ocorreu quando a
ferramenta não está instalada. Por isso, este relatório separa explicitamente os
achados de código da evidência dinâmica ainda pendente.

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
- A ausência do ZAP impede apresentar um relatório passivo/ativo ou afirmar
  severidade baseada em tráfego real. A sessão dinâmica deve ser executada somente
  após instalar a ferramenta e manter o sistema localmente autorizado.

## Participação individual

| Integrante | Participação registrada |
|---|---|
| Anna Letycia Fernandes Reis | Revisão dos controles de autenticação, autorização e exposição de credenciais |
| Cristiano Silveira Silva | Inventário do código, configurações de execução e consolidação dos achados |
| Eduardo Nascimento de Souza Rolim | Análise dos impactos e relacionamento com OWASP/CWE |
| Luan Diniz Mazaro Rodovalho | Revisão das propostas de correção e limitações da verificação |


