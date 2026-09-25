# Etapa 2 — Análise, Priorização e Tratamento de Riscos com o NIST CSF

## 10. Objetivo

A Etapa 2 dá continuidade à análise de segurança realizada na Etapa 1 para o sistema **Artifício**, transformando as ameaças e os casos de abuso identificados em uma base estruturada para análise de riscos.

O Artifício é uma plataforma web para divulgação e contratação de serviços artísticos. Seus principais usuários são **cliente, artista e administrador**. O cliente pesquisa artistas, consulta perfis e portfólios, solicita comissões e acompanha contratações; o artista administra seu perfil, portfólio, serviços, preços, prazos e solicitações; e o administrador atua na moderação, no gerenciamento de usuários, no tratamento de denúncias e na resolução de conflitos.

A análise da Etapa 1 identificou ameaças relacionadas a autenticação, identidade, sessões, solicitações e contratações, avaliações, arquivos, mensagens, dados pessoais, busca, disponibilidade, permissões e funcionalidades administrativas. Também foram definidos casos de abuso correspondentes a essas situações.

Nesta etapa, esses resultados são utilizados como base para avaliar os riscos associados ao funcionamento do sistema. O processo será organizado de forma a manter a rastreabilidade entre o elemento identificado na Etapa 1 e o risco correspondente:

```text
Ativo / componente
       ↓
Ameaça STRIDE
       ↓
Caso de abuso
       ↓
Evento de risco
       ↓
Probabilidade + impacto
       ↓
Nível do risco
       ↓
Prioridade
       ↓
Tratamento e controles
       ↓
NIST CSF 2.0
       ↓
Risco residual
```

A análise de riscos considerará principalmente os princípios de **confidencialidade, integridade e disponibilidade**, já identificados como objetivos de proteção para os recursos do Artifício.

Os resultados da Etapa 2 serão utilizados posteriormente para:

- classificar os riscos de acordo com sua probabilidade e impacto;
- estabelecer uma ordem de prioridade;
- selecionar estratégias de tratamento;
- relacionar os riscos às funções do NIST Cybersecurity Framework 2.0;
- definir controles de segurança;
- indicar responsáveis e formas de verificação;
- estimar o risco residual após o tratamento.


## 11. Continuidade do projeto

O sistema analisado permanece sendo o **Artifício**, cuja operação envolve:

- acesso de clientes e artistas por meio de uma aplicação web;
- pesquisa de artistas por categorias e filtros;
- visualização de perfis e portfólios;
- cadastro e gerenciamento de serviços artísticos;
- definição de preços-base, prazos e condições de trabalho;
- solicitações e negociações de comissões;
- acompanhamento de contratações;
- comunicação entre cliente e artista;
- avaliações;
- moderação e gerenciamento administrativo.

Os principais ativos identificados anteriormente são:

| Ativo ou componente | Relevância para a análise de riscos |
|---|---|
| Contas de usuários | Concentram identidade, configurações e permissões. |
| Credenciais e sessões | São utilizadas para autenticação e podem permitir acesso às contas quando comprometidas. |
| Perfis de artistas | Contêm informações utilizadas para identificação, divulgação e reputação. |
| Portfólios e obras | Representam o trabalho dos artistas e envolvem arquivos enviados pelos usuários. |
| Solicitações e contratações | Registram pedidos, propostas, preços, prazos e condições acordadas. |
| Mensagens | Contêm comunicação privada e informações das negociações. |
| Avaliações | Influenciam a reputação dos usuários e precisam manter integridade. |
| Dados financeiros | Podem estar relacionados a pagamentos e repasses. |
| Logs e registros de auditoria | Permitem rastrear ações e investigar incidentes e disputas. |
| Banco de dados | Armazena dados de usuários, obras, contratações, mensagens e demais informações da aplicação. |
| Servidor da aplicação | Processa as requisições e executa as funcionalidades do sistema. |
| Armazenamento de arquivos | Armazena imagens e outros arquivos enviados pelos usuários. |
| APIs | Realizam a comunicação entre frontend, backend e possíveis serviços externos. |
| Serviços externos | Podem fornecer autenticação, pagamentos, hospedagem ou armazenamento e representam dependências da plataforma. |

A análise também mantém as seis categorias STRIDE identificadas anteriormente:

| Categoria | Relação com o Artifício |
|---|---|
| **Spoofing** | Comprometimento de contas, sessões, autenticação externa e falsificação de identidade. |
| **Tampering** | Alteração de preços, prazos, contratações, avaliações, arquivos, dados e permissões. |
| **Repudiation** | Negação de ações, alterações, entregas, negociações e contratações e perda de evidências. |
| **Information Disclosure** | Exposição de dados pessoais, mensagens, arquivos, metadados e informações de contratação. |
| **Denial of Service** | Sobrecarga de login, buscas, uploads, processamento de imagens, solicitações e dependências externas. |
| **Elevation of Privilege** | Acesso indevido a funções administrativas, alteração de permissões e acesso a recursos de terceiros. |

Os casos de abuso da Etapa 1 também serão preservados como referência para a análise de riscos. Entre eles estão a criação de perfil falso **(CA01)**, tomada de conta **(CA02)**, comprometimento de autenticação externa **(CA03)**, envio de arquivo malicioso ou incompatível **(CA05)**, alteração de condições de contratação **(CA07)**, manipulação de avaliações **(CA08)**, adulteração de registros **(CA09)**, acesso indevido a dados e arquivos privados **(CA13 e CA14)**, extração automatizada de informações **(CA16)**, sobrecarga de autenticação e solicitações **(CA18 e CA19)**, acesso direto à área administrativa **(CA22)**, alteração de permissões **(CA23)**, acesso a recursos de outros artistas **(CA24)** e continuidade de acesso após bloqueio ou revogação **(CA25)**.

## 12. Estrutura da Etapa 2

A estrutura adotada é:

```text
Etapa 2 — Análise, Priorização e Tratamento de Riscos com o NIST CSF
│
├── 10. Objetivo
├── 11. Continuidade do projeto
├── 12. Estrutura mínima da Etapa 2
│
├── 13. Análise e priorização dos riscos
│   ├── 13.1 Critérios de probabilidade
│   ├── 13.2 Critérios de impacto
│   ├── 13.3 Cálculo e classificação dos riscos
│   ├── 13.4 Registro de riscos
│   ├── 13.5 Justificativas das avaliações
│   └── 13.6 Priorização dos riscos
│
├── 14. Tratamento dos riscos com o NIST CSF 2.0
│   ├── 14.1 Estratégias de tratamento
│   ├── 14.2 Funções do NIST CSF 2.0
│   ├── 14.3 Mapeamento dos riscos para o NIST CSF
│   ├── 14.4 Plano de tratamento
│   ├── 14.5 Ordem inicial de implementação
│   └── 14.6 Estimativa do risco residual
│
└── 15. Considerações finais
```

## 13. Análise e priorização dos riscos

A análise de riscos parte das ameaças e dos casos de abuso identificados na Etapa 1. Cada risco deverá representar um evento que possa afetar um ativo, usuário, componente ou função relevante do Artifício.

A avaliação será realizada separando dois fatores:

- **Probabilidade:** possibilidade de o evento ocorrer;
- **Impacto:** consequência esperada caso o evento ocorra.


Exemplo da relação:

```text
T01 — Comprometimento de conta de artista
                ↓
CA02 — Tomada de conta por comprometimento de sessão ou recuperação de senha
                ↓
Risco — Acesso não autorizado à conta de um artista
```

### 13.1 Critérios de probabilidade

A probabilidade representa a possibilidade de ocorrência de um evento de risco no contexto específico do Artifício.

Será utilizada a escala de quatro níveis estabelecida para esta etapa:

| Valor | Classificação | Critério |
|---:|---|---|
| **1** | **Baixa** | O evento depende de condições incomuns, acesso muito específico ou grande capacidade técnica. |
| **2** | **Média-baixa** | O evento é possível, mas depende de uma vulnerabilidade ou condição específica. |
| **3** | **Média-alta** | O evento é plausível e pode ocorrer em situações comuns de uso ou ataque. |
| **4** | **Alta** | O evento pode ocorrer com facilidade, frequência ou durante condições previsíveis do sistema. |

A classificação da probabilidade não será baseada somente em intuição. Para justificar cada avaliação serão consideradas as características do Artifício e as condições necessárias para que o evento ocorra.

#### Fatores considerados na avaliação

| Fator | Aplicação ao Artifício |
|---|---|
| **Exposição** | Verifica se a funcionalidade ou recurso está disponível diretamente pela aplicação web ou por interfaces acessíveis aos usuários. |
| **Tipo de acesso necessário** | Considera se o evento pode ser iniciado por um visitante, cliente, artista, administrador ou se exige acesso privilegiado. |
| **Complexidade da exploração** | Considera o conhecimento técnico e as condições necessárias para realizar o abuso. |
| **Existência de vulnerabilidade ou condição específica** | Verifica se o evento depende de uma falha ou configuração específica, como ausência de validação no backend ou falha de autorização. |
| **Frequência de utilização da funcionalidade** | Funcionalidades utilizadas frequentemente oferecem mais oportunidades para tentativas de abuso. |
| **Possibilidade de automação** | Considera se o evento pode ser repetido automaticamente, como tentativas de login, buscas e solicitações. |
| **Dependências externas** | Considera situações relacionadas a serviços externos, como autenticação. |
| **Controles já identificados** | Considera mecanismos previstos na Etapa 1 que podem dificultar a ocorrência, como rate limiting, MFA, validação no backend, controle de acesso e proteção de sessões. |

Esses fatores servem para fundamentar a classificação e não constituem uma segunda escala de pontuação.

#### Aplicação dos critérios aos cenários identificados na Etapa 1

Os cenários da Etapa 1 permitem observar como a escala será aplicada ao contexto real do Artifício.

| Situação identificada na Etapa 1 | Referência | Elemento relevante para a probabilidade |
|---|---|---|
| Tentativas excessivas de login ou recuperação de senha | T21 / CA18 | A funcionalidade de autenticação é exposta e pode receber requisições automatizadas; a existência ou ausência de rate limiting influencia a classificação. |
| Buscas complexas ou repetidas | T22 / CA16 e CA19 | A busca é uma funcionalidade utilizada normalmente pelos clientes e pode ser automatizada, tornando frequência e automação fatores relevantes. |
| Upload de arquivos grandes ou em grande quantidade | T23 / T24 / CA05 e CA06 | O evento depende da funcionalidade de upload e da existência de limites de tamanho, quantidade e processamento. |
| Alteração de preço, prazo ou condições de contratação | T05 / T06 / CA07 | A probabilidade depende da existência de validação no backend e da proteção das condições após o aceite. |
| Comprometimento de conta ou sessão | T01 / T03 / CA02 | A avaliação deve considerar os mecanismos de autenticação, recuperação de senha, proteção de sessão e controles adicionais. |
| Acesso direto a funções administrativas | T28 / CA22 | A probabilidade depende principalmente da existência de verificação de autorização no backend. |
| Alteração de papéis e permissões | T29 / CA23 | O evento depende da possibilidade de manipular parâmetros de autorização ou de obter acesso indevido ao armazenamento de dados. |
| Acesso a obras de outro artista | T30 / CA24 | A avaliação deve considerar se o backend verifica se o usuário autorizado é proprietário ou possui permissão sobre o recurso solicitado. |
| Persistência de acesso após bloqueio | T31 / CA25 | A possibilidade depende da invalidação das sessões e da revogação efetiva das permissões. |
| Acesso indevido a conversas ou arquivos por administrador | T19 / CA13 | A avaliação depende da segregação de privilégios, da autorização contextual e do registro das ações administrativas. |

Essa relação não atribui antecipadamente um valor de probabilidade aos riscos. Ela estabelece **como as características observadas na Etapa 1 serão utilizadas para justificar os valores de 1 a 4** no registro de riscos.

#### Interpretação da escala

**Valor 1 — Baixa**

Será utilizado quando o evento depender de condições incomuns, acesso muito específico ou grande capacidade técnica.

No contexto do Artifício, enquadram-se nessa condição eventos que dependam simultaneamente de acesso privilegiado e de uma circunstância técnica específica.

**Valor 2 — Média-baixa**

Será utilizado quando o evento for possível, mas depender de uma vulnerabilidade ou condição específica.

Exemplos de condições desse tipo no Artifício incluem situações que dependam de uma falha específica de autorização, de uma configuração inadequada ou de uma combinação de circunstâncias que não esteja presente em todas as operações.

**Valor 3 — Média-alta**

Será utilizado quando o evento for plausível e puder ocorrer em situações comuns de uso ou ataque.

No Artifício, essa classificação poderá ser considerada quando a funcionalidade estiver exposta a clientes ou artistas e o abuso puder ser realizado sem acesso administrativo, desde que ainda exista alguma condição ou dificuldade para sua ocorrência.

**Valor 4 — Alta**

Será utilizado quando o evento puder ocorrer com facilidade, frequência ou durante condições previsíveis do sistema.

No contexto do Artifício, essa condição é especialmente relevante para eventos que possam ser repetidos ou automatizados contra funcionalidades expostas, como autenticação, busca, solicitações e upload, quando os mecanismos de proteção forem insuficientes.

#### Justificativa da probabilidade

A justificativa deverá explicar por que o evento recebeu determinado valor, relacionando o valor à realidade do sistema.

A estrutura utilizada será:

| Campo | Descrição |
|---|---|
| **Probabilidade** | Valor de 1 a 4. |
| **Classificação** | Baixa, Média-baixa, Média-alta ou Alta. |
| **Justificativa** | Explicação objetiva das condições de ocorrência, considerando exposição, acesso necessário, complexidade, vulnerabilidades, possibilidade de automação e controles existentes. |

### 13.2 Critérios de impacto


### 13.3 Cálculo e classificação dos riscos


### 13.4 Registro de riscos


### 13.5 Justificativas das avaliações


### 13.6 Priorização dos riscos


## 14. Tratamento dos riscos com o NIST CSF 2.0

### 14.1 Estratégias de tratamento

Para cada risco analisado, é necessário definir a estratégia de resposta mais adequada. Conforme as diretrizes da disciplina e as boas práticas de gestão de riscos, foram adotadas quatro estratégias principais no contexto do **Artifício**:

| Estratégia | Descrição | Aplicação no Sistema Artifício | Casos de Abuso Relacionados |
|---|---|---|---|
| **Reduzir (Mitigar)** | Implementar medidas e controles técnicos ou operacionais para diminuir a probabilidade de ocorrência ou atenuar o impacto do evento. | **Estratégia predominante no projeto.** Aplicação de validações de regras e preços no backend, controle rigoroso de permissões por perfil, proteção de sessões e recuperação de conta, limite de requisições contra ataques de força bruta (*rate limiting*), proteção de logs e integridade de uploads de imagens. | `CA02`, `CA05`, `CA06`, `CA07`, `CA09`, `CA18`, `CA19`, `CA22`, `CA23`, `CA24` |
| **Compartilhar (Transferir)** | Atribuir parte da operação, da custódia de dados críticos ou das consequências a um terceiro especializado. | **Operações de pagamento e autenticação externa.** Delegação do processamento de pagamentos para gateways especializados em conformidade com normas de segurança e utilização de provedores de identidade consolidados para autenticação externa, transferindo a custódia das credenciais primárias. | `CA03`, `CA21` |
| **Evitar** | Eliminar a atividade, funcionalidade ou condição de arquitetura que dá origem ao risco. | **Decisões de escopo e arquitetura.** Restrição estrita de tipos de arquivo aceitos no upload, rejeitando categoricamente executáveis ou scripts e aceitando somente imagens; e recusa arquitetural em armazenar números de cartões de crédito na base própria da aplicação. | `CA05` |
| **Aceitar** | Reconhecer conscientemente a existência do risco e mantê-lo sob observação, quando seu impacto for muito reduzido ou o custo de eliminação for desproporcional. | **Riscos residuais toleráveis.** Exposição controlada de informações que são inerentemente públicas para a finalidade da plataforma, como visualização do portfólio e consultas públicas no catálogo de artistas por visitantes, mantendo apenas monitoramento básico sem bloquear o uso legítimo. | `CA17` |

#### Critérios para escolha e formalização da aceitação

Para assegurar uma tomada de decisão fundamentada:

- **Riscos Críticos e Altos:** Não podem ser aceitos sob nenhuma hipótese. Devem ser tratados obrigatoriamente pelas estratégias de **Reduzir**, **Compartilhar** ou **Evitar**.
- **Riscos Médios:** A prioridade é a **redução** por meio de controles de desenvolvimento. Caso algum aspecto não possa ser corrigido de imediato, deve ser acompanhado de perto.
- **Condições para Aceitação de Riscos:**
  - **Motivo da decisão:** Justificativa clara demonstrando que o risco possui impacto insignificante ou que o controle geraria impacto inaceitável na usabilidade.
  - **Aprovação:** Qualquer decisão de aceitação deve ser formalmente registrada e acordada pela equipe de segurança e desenvolvimento do projeto.
  - **Condições e revisão:** Todo risco aceito permanece registrado no inventário de riscos e deve ser reavaliado periodicamente ou sempre que houver grandes alterações na arquitetura da plataforma.

### 14.2 Funções do NIST CSF 2.0


### 14.3 Mapeamento dos riscos para o NIST CSF


### 14.4 Plano de tratamento


### 14.5 Ordem inicial de implementação


### 14.6 Estimativa do risco residual


## 15. Considerações finais


## 16. Critérios de avaliação da Etapa 2

