# Roteiro de Apresentação em Vídeo — Artifício
## Engenharia de Software Seguro

**Base:** transcrição da reunião de 16 de novembro de 2026  
**Projeto:** Artifício — plataforma web para comercialização de comissões artísticas  
**Integrantes:** Luan Diniz, Anna Letycia Reis, Eduardo Nascimento e Cristiano Silveira

---

## Orientações de gravação

- **Formato:** apresentação em vídeo com compartilhamento de tela.
- **Ordem sugerida:** contextualização do projeto → modelagem e ameaças → decisões arquiteturais → análises e resultados → demonstração prática → testes e encerramento.
- **Edição:** remover pausas, conversas de preparação, erros de gravação e trechos repetidos.
- **Apoio visual:** alternar entre slides, diagramas, relatórios, código-fonte e demonstração da aplicação.
- **Atenção:** os números e resultados abaixo foram mantidos conforme informados na transcrição. Antes da gravação final, conferir os nomes técnicos e os valores diretamente nos relatórios da aplicação.

---

# 1. Abertura e apresentação do projeto

**Apresentador:** Luan Diniz  
**Visual:** slide de abertura com o nome *Artifício*, título da disciplina e integrantes do grupo.

### Fala

Olá! Nesta apresentação, vamos demonstrar o desenvolvimento do **Artifício**, um projeto realizado para a disciplina de **Engenharia de Software Seguro**.

O grupo é formado por **Anna Letycia Reis, Eduardo Nascimento, Cristiano Silveira e Luan Diniz**.

O Artifício é uma plataforma web criada para aproximar clientes e artistas que oferecem trabalhos por encomenda. A proposta é permitir que um cliente encontre artistas de acordo com o tipo de arte desejado e solicite uma comissão diretamente pela plataforma.

Por exemplo: se um cliente procura uma pixel art, poderá filtrar esse tipo de trabalho, visualizar os artistas disponíveis e escolher aquele cujo estilo atende às suas necessidades.

A plataforma também possui um perfil administrativo, responsável por gerenciar usuários e realizar moderações relacionadas a denúncias e outras ocorrências.

---

# 2. Funcionalidades e atores do sistema

**Apresentador:** Luan Diniz  
**Visual:** diagrama de casos de uso.

### Fala

O sistema possui três atores principais: **artista, cliente e administrador**.

O artista e o cliente podem criar uma conta e realizar login, mas possuem funcionalidades diferentes.

O artista pode:
- Gerenciar seu perfil;
- Criar e atualizar seu portfólio;
- Divulgar os tipos de arte que produz;
- Receber solicitações de comissões.

O cliente pode:
- Visualizar os portfólios;
- Pesquisar e filtrar artistas;
- Filtrar os tipos de arte;
- Solicitar comissões.

O administrador possui funções gerais de gerenciamento e consulta, além de atuar na moderação da plataforma.

---

# 3. Levantamento de requisitos e modelagem de ameaças

**Apresentador:** Luan Diniz  
**Visual:** questionário respondido, diagrama de contexto, diagrama de fluxo de dados e quadro STRIDE.

### Fala

A modelagem do projeto começou com o levantamento de requisitos. Nesse processo, o cliente do projeto foi representado pelo próprio Luan, que apresentou as necessidades e expectativas para o Artifício.

Os demais integrantes elaboraram um questionário com perguntas relacionadas à metodologia **STRIDE**. Foram respondidas **64 perguntas**, cujas respostas foram catalogadas e utilizadas como base para a definição dos requisitos e para a modelagem de ameaças.

Foram elaborados diagramas para representar o funcionamento e os limites do sistema, incluindo:

- **Diagrama de contexto:** apresenta as relações dos clientes e dos demais atores com o Artifício, além das interações com serviços externos;
- **Diagrama de fluxo de dados:** representa o caminho percorrido pelas informações quando o usuário interage com a aplicação.

A partir dessa análise, identificamos **33 ameaças e 27 casos de abuso** que poderiam ocorrer na plataforma.

Em seguida, avaliamos os casos de abuso considerando a probabilidade de ocorrência e o impacto. Com esse cálculo, classificamos os riscos em níveis, como crítico, alto, médio e baixo.

Essa classificação permitiu criar uma ordem de prioridade para a implementação das correções, começando pelos casos considerados mais importantes.

---

# 4. Caso de abuso CA22: acesso indevido à área administrativa

**Apresentadora:** Anna Letycia Reis  
**Visual:** diagrama ou registro do caso de abuso CA22, seguido do código de configuração de segurança.

### Fala

Para explicar as etapas três e quatro do processo, vamos utilizar como exemplo o **caso de abuso CA22**, que trata da tentativa de acesso direto à área administrativa por um usuário sem autorização.

Na primeira etapa, identificamos que um cliente poderia tentar adivinhar ou digitar diretamente a URL administrativa no navegador.

Na segunda etapa, avaliamos o impacto dessa ação e classificamos o problema como o **risco R28**, relacionado à elevação de privilégios e ao acesso indevido a funções administrativas.

Na terceira etapa, definimos a decisão arquitetural para proteger o sistema. A decisão foi estabelecer uma autorização centralizada no back-end para todas as operações protegidas.

Entendemos que a interface web, sozinha, não poderia ser considerada uma barreira de segurança. Esconder botões ou verificar apenas se o usuário está autenticado não seria suficiente para impedir o acesso indevido.

Por isso, antes de liberar qualquer rota restrita, o back-end precisa verificar o papel do usuário por meio das regras de **RBAC**, ou seja, controle de acesso baseado em papéis.

Na quarta etapa, levamos essa decisão para o código e validamos seu funcionamento por meio de testes de segurança.

Utilizamos o **Spring Security**, no back-end desenvolvido com Java e Spring Boot, para restringir as rotas administrativas aos usuários autorizados.

Também criamos testes automatizados com **JUnit 5 e MockMvc**. Os testes verificam dois cenários: o administrador consegue acessar normalmente a área administrativa, enquanto o cliente recebe uma resposta de acesso negado, mesmo quando tenta acessar diretamente a URL.

Assim, mantemos a rastreabilidade entre a identificação do abuso, a avaliação do risco, a decisão arquitetural, a implementação e os testes.

---

# 5. Análises de segurança e resultados

**Apresentador:** Cristiano Silveira  
**Visual:** relatórios do OWASP ZAP, cobertura de testes, análise de dependências e resultados de validação.

### Fala

Dando continuidade ao processo, após as decisões arquiteturais, realizamos as etapas de análise, validação e consolidação das medidas de segurança.

Na **etapa cinco**, realizamos uma análise dinâmica com o **OWASP ZAP**, avaliando passivamente as respostas HTTP da aplicação em execução local. Os testes foram realizados tanto no Linux, utilizando Fedora, quanto no Windows 11.

Na **etapa seis**, avaliamos a cobertura de código por meio de testes automatizados.

Na **etapa sete**, consolidamos as validações e os resultados relacionados à execução nos ambientes de desenvolvimento e produção.

Na análise realizada com o OWASP ZAP, foram identificados:
- 10 alertas no total;
- 1 alerta de risco médio;
- 5 alertas de baixo risco;
- 4 apontamentos informativos.

Além disso, 16 verificações de cenário foram aprovadas, sem falhas de execução.

A cobertura de código apresentada foi de **42,4%**. Esse percentual é esperado, pois o projeto não foi desenvolvido como um produto completo. O grupo selecionou uma parte específica do sistema para implementar e validar, considerando os casos de uso e os requisitos funcionais e não funcionais definidos.

Também realizamos verificações de dependências, utilizando a base de vulnerabilidades conhecidas. Essa análise precisa ser atualizada periodicamente para considerar novas informações sobre vulnerabilidades.

Entre as práticas e conceitos aplicados estão:
- **DAST:** análise dinâmica da aplicação em execução;
- **SAST:** análise estática do código;
- **SCA:** análise de composição de software e dependências;
- **Testes automatizados e cobertura de código;**
- **Validação dos perfis de desenvolvimento e produção;**
- **Verificação dos cabeçalhos de segurança HTTP.**

---

# 6. Diferenças entre desenvolvimento e produção

**Apresentador:** Cristiano Silveira  
**Visual:** comparação dos relatórios e das respostas HTTP nos dois ambientes.

### Fala

Um ponto importante da nossa implementação foi a separação entre o ambiente de desenvolvimento e o ambiente de produção.

No ambiente de desenvolvimento, algumas funcionalidades ficam disponíveis para facilitar o trabalho da equipe, como a console de administração do banco H2 e informações utilizadas durante os testes.

Entretanto, essas funcionalidades não devem ficar expostas no ambiente de produção.

Por isso, validamos que, no perfil de produção, os recursos internos e administrativos deixam de estar acessíveis.

Um exemplo é a console H2. No ambiente de desenvolvimento, ela pode estar habilitada. Já no ambiente de produção, ao tentar acessá-la, o sistema retorna **HTTP 404 — página não encontrada**.

Nesse contexto, o código 404 é um resultado esperado: ele indica que o recurso não está disponível publicamente no ambiente de produção.

Também verificamos que credenciais de demonstração, logs e outras informações internas não devem ficar expostos ao usuário final.

Entre os pontos analisados estavam:
- Console H2 e painel de administração do banco;
- Credenciais de demonstração;
- Exposição de logs;
- Cookies e cookies de sessão;
- Isolamento de origem;
- Política de segurança de conteúdo, ou CSP;
- Cabeçalhos de segurança HTTP.

Os resultados permitiram verificar que recursos úteis durante o desenvolvimento foram restringidos no ambiente de produção.

---

# 7. Correções e validações implementadas

**Apresentador:** Cristiano Silveira  
**Visual:** lista de correções e, quando possível, trechos do código correspondentes.

### Fala

Além das análises, realizamos correções para reduzir a exposição de informações e fortalecer a aplicação.

Entre as medidas implementadas estão:

- Remoção de senhas e informações sensíveis do HTML;
- Desabilitação do acesso à console H2 em produção;
- Configuração de cabeçalhos de segurança HTTP;
- Tratamento adequado de erros;
- Execução de uma suíte ampla de testes;
- Verificação das dependências e dos apontamentos de segurança.

Na rodada apresentada, foram demonstrados 16 testes automatizados, mas o projeto possui um conjunto maior de verificações. Ao todo, foram implementados **277 testes**.

Esses testes abrangem regras de negócio, autenticação, cadastro, controle de acesso, persistência no banco de dados e histórico de auditoria.

---

# 8. Estratégia de desenvolvimento e os 277 testes

**Apresentador:** Eduardo Nascimento  
**Visual:** estrutura dos testes no projeto, pastas de testes unitários e testes dos casos de abuso.

### Fala

Dando continuidade, vou explicar como organizamos o desenvolvimento e a validação da aplicação.

No total, implementamos aproximadamente **277 testes**.

Nosso fluxo começou com o desenvolvimento orientado por testes. Primeiro, criamos testes para validar as funcionalidades e, depois, implementamos o código necessário para fazê-los passar.

Em seguida, analisamos os casos de abuso identificados na modelagem de ameaças e criamos testes específicos para verificar se cada situação estava devidamente tratada.

Também desenvolvemos testes adicionais com apoio de inteligência artificial, utilizando o Gemini para sugerir verificações de situações específicas.

Esses testes foram revisados e organizados no projeto. Eles complementam os testes unitários e os testes criados a partir dos casos de abuso.

A estratégia de testes foi importante porque permite identificar regressões. Se uma funcionalidade que já estava correta for alterada por uma mudança em outra parte do sistema, o teste pode deixar de passar e indicar que algo precisa ser corrigido.

Assim, os testes ajudam a manter a consistência da aplicação durante sua evolução.

---

# 9. Demonstração do controle de acesso administrativo

**Apresentador:** Eduardo Nascimento  
**Visual:** aplicação aberta, tentativa de acesso à URL administrativa e código de configuração do Spring Security.

### Fala

Agora vou demonstrar alguns exemplos práticos das proteções implementadas.

O primeiro exemplo é o controle de acesso à área administrativa, que corresponde ao caso de abuso apresentado anteriormente.

No código, configuramos uma regra para permitir que somente administradores acessem a URL administrativa.

Primeiro, tentamos acessar essa área sem utilizar uma conta administrativa. O sistema impede o acesso.

Depois, realizamos a tentativa utilizando uma conta de cliente. Mesmo que o usuário digite diretamente a URL, ele não consegue acessar a área restrita.

Nesse caso, o sistema retorna um erro **403 — acesso proibido**. Também implementamos uma tela dentro da própria aplicação para apresentar a mensagem ao usuário, evitando a exposição desnecessária de detalhes técnicos do erro.

Essa demonstração confirma que a proteção é realizada no back-end, e não depende apenas da interface visual.

---

# 10. Demonstração dos ambientes de desenvolvimento e produção

**Apresentador:** Eduardo Nascimento  
**Visual:** cadastro de usuário no ambiente de desenvolvimento; console de e-mail; depois, acesso ao ambiente de produção.

### Fala

Outro ponto importante é a diferença entre os ambientes de desenvolvimento e produção.

No ambiente de desenvolvimento, implementamos um recurso chamado **simulador de serviço de e-mail**. Ele permite visualizar no terminal o link de confirmação enviado durante o cadastro, facilitando os testes sem depender do envio real de uma mensagem.

Vou realizar um cadastro de demonstração. Após preencher os dados, a aplicação informa que um link de confirmação foi gerado.

No ambiente de desenvolvimento, conseguimos visualizar esse link no terminal, copiá-lo e confirmar a conta para realizar o login.

Agora, ao acessar o ambiente de produção, podemos observar as diferenças.

A console H2 não está disponível e retorna HTTP 404. As informações de usuários utilizadas para facilitar os testes também não ficam expostas.

Ao tentar realizar um novo cadastro, o sistema não apresenta o link de confirmação no terminal como ocorre no ambiente de desenvolvimento. O envio é tratado pelo serviço configurado para produção.

Portanto, os recursos que facilitam o desenvolvimento foram separados das funcionalidades disponibilizadas no ambiente de produção.

---

# 11. Demonstração da validação de arquivos de imagem

**Apresentador:** Eduardo Nascimento  
**Visual:** código do validador de imagens e tentativa de envio de um arquivo não permitido.

### Fala

Outro caso de abuso tratado foi o envio de arquivos que não deveriam ser aceitos como imagens.

Anteriormente, a aplicação verificava basicamente se o arquivo estava vazio ou não. Para fortalecer essa validação, implementamos um componente específico para verificar os arquivos enviados.

O validador permite somente os formatos definidos pela aplicação: **PNG, JPG, GIF, WebP, SVG e PNG**, conforme a lista apresentada na implementação.

Qualquer arquivo fora dos formatos aceitos deve ser rejeitado.

Para demonstrar, vou tentar enviar um arquivo de texto como se fosse uma imagem.

A aplicação identifica o tipo do arquivo como texto simples e apresenta uma mensagem informando que o formato não é suportado.

Também implementamos uma proteção relacionada ao nome do arquivo.

Um usuário poderia tentar inserir um nome com conteúdo indevido ou informações que não deveriam ser exibidas. Para evitar esse problema, o sistema gera uma identificação aleatória e utiliza esse nome seguro para armazenar o arquivo.

Dessa forma, o nome original enviado pelo usuário não é utilizado diretamente como nome de armazenamento.

---

# 12. Demonstração da prevenção de autocontratação

**Apresentador:** Eduardo Nascimento  
**Visual:** catálogo de artistas, tentativa de solicitar uma comissão para o próprio perfil e trecho do controller.

### Fala

Outro caso de abuso identificado foi a possibilidade de um artista contratar a própria comissão.

Para demonstrar, primeiro vou acessar o catálogo utilizando um perfil de artista e realizar uma solicitação para outro artista.

A solicitação é aceita normalmente.

Em seguida, vou tentar solicitar uma comissão para o próprio perfil.

A aplicação identifica que o artista logado é o mesmo artista que receberia a solicitação. Nesse caso, o sistema bloqueia a operação e apresenta a mensagem:

**“Você não pode solicitar uma comissão para seu próprio serviço.”**

No código, essa validação é realizada no controller, verificando se o artista destinatário da solicitação é o mesmo usuário autenticado.

Se os identificadores forem iguais, a operação é interrompida e o usuário retorna à página correspondente.

Essa regra impede a autocontratação e protege a integridade das operações de negócio da plataforma.

---

# 13. Organização dos testes dos casos de abuso

**Apresentador:** Eduardo Nascimento  
**Visual:** documentação e arquivos de testes organizados por caso de abuso.

### Fala

Além das demonstrações, organizamos testes específicos para os casos de abuso identificados durante a análise.

Cada teste procura verificar uma condição que poderia comprometer a segurança ou o funcionamento correto da aplicação.

Entre os exemplos estão:
- Impedir tentativas de XSS;
- Rejeitar cadastros com e-mail duplicado, independentemente de variações;
- Rejeitar tokens de ativação forjados;
- Impedir ataques de repetição, conhecidos como replay attacks;
- Tratar arquivos maliciosos, muito grandes ou corrompidos;
- Verificar situações de isolamento de identidade;
- Impedir tentativas de evasão de identidade.

Também foram criados testes para validar situações de cadastro, como nomes excessivamente grandes, nomes com apenas um caractere e e-mails em formatos anômalos.

O objetivo foi transformar os riscos identificados na modelagem em verificações concretas no código.

---

# 14. Encerramento

**Apresentador:** Eduardo Nascimento, com complementação dos demais integrantes  
**Visual:** slide final com o nome Artifício, integrantes e principais resultados.

### Fala

Com isso, concluímos a apresentação do Artifício.

O objetivo do trabalho foi desenvolver uma aplicação e aplicar, na prática, os conceitos de Engenharia de Software Seguro.

Partimos do levantamento de requisitos e da modelagem de ameaças, identificando 33 ameaças e 27 casos de abuso. Em seguida, avaliamos os riscos, priorizamos as correções, implementamos medidas de proteção e validamos os resultados por meio de análises e testes automatizados.

Ao longo do projeto, trabalhamos com controle de acesso, separação entre ambientes de desenvolvimento e produção, validação de arquivos, proteção de informações, análise de dependências e testes de segurança.

Também demonstramos como os casos de abuso identificados foram transformados em regras de proteção e testes específicos.

Adotamos uma abordagem didática em que desenvolvemos uma parte do sistema e, depois, aplicamos o processo de fortalecimento da segurança, corrigindo os pontos identificados. Sabemos que, em um projeto real, o ideal é incorporar os requisitos de segurança desde o início do desenvolvimento.

A experiência permitiu compreender como identificar ameaças, avaliar riscos, implementar proteções e verificar se elas realmente funcionam.

Obrigado pela atenção!

---

# 15. Checklist de edição antes de exportar o vídeo

- [ ] Inserir o slide de abertura com o nome do projeto e os integrantes.
- [ ] Exibir o diagrama de casos de uso durante a explicação dos atores.
- [ ] Exibir o questionário, o diagrama de contexto, o fluxo de dados e a análise STRIDE.
- [ ] Mostrar o caso de abuso CA22 e o risco R28.
- [ ] Mostrar a configuração do Spring Security e os testes de autorização.
- [ ] Exibir os relatórios do OWASP ZAP e a cobertura de código.
- [ ] Comparar visualmente os ambientes de desenvolvimento e produção.
- [ ] Demonstrar a restrição da console H2 em produção.
- [ ] Demonstrar a validação de arquivos.
- [ ] Demonstrar o bloqueio da autocontratação.
- [ ] Exibir a organização dos testes e o total de 277 testes.
- [ ] Remover pausas, conversas paralelas, erros de fala e trechos marcados para corte.
- [ ] Conferir os números e os nomes técnicos diretamente nos relatórios e no código antes da gravação final.

---

## Observações de conferência

1. **Formatos de imagem:** a transcrição menciona PNG, JPG, GIF, WebP, SVG e “PMG”. O roteiro normaliza a repetição aparente de PNG, mas é necessário conferir no código se o formato pretendido é PNG ou outro.
2. **Código HTTP:** a transcrição alterna entre referências a HTTP 403 e HTTP 404. O roteiro mantém 403 para a tentativa de acesso não autorizado à área administrativa e 404 para recursos internos indisponíveis em produção, conforme as demonstrações descritas.
3. **Ferramentas e siglas:** conferir a grafia dos nomes nos slides e relatórios, especialmente OWASP ZAP, JUnit 5, MockMvc, DAST, SAST e SCA.
4. **Resultados:** os números 64 perguntas, 33 ameaças, 27 casos de abuso, 42,4% de cobertura e 277 testes foram mantidos conforme a transcrição. Confirmar os valores nos artefatos do projeto antes da apresentação.
5. **Duração:** a reunião transcrita tem aproximadamente 41 minutos. O roteiro organiza e limpa as falas, mas a duração final dependerá do ritmo da narração e do tempo das demonstrações.
