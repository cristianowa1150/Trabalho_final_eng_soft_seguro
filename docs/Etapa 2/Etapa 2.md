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

O impacto representa a gravidade das consequências caso o evento de risco se concretize no Artifício. Sua avaliação considera os clientes, artistas e administradores, assim como contas, portfólios, mensagens, contratações, dados privados, registros de auditoria e infraestrutura.

Será utilizada a escala estabelecida no enunciado:

| Valor | Classificação | Critério |
|---:|---|---|
| 1 | Baixo | Causa pequeno transtorno e pode ser corrigido rapidamente. |
| 2 | Moderado | Causa interrupção ou inconsistência limitada, com possibilidade de recuperação. |
| 3 | Alto | Causa prejuízo relevante aos usuários, ao negócio, à administração ou à privacidade. |
| 4 | Muito alto | Pode afetar muitos usuários, comprometer operações críticas ou causar prejuízo grave. |

Para aplicar essa escala ao sistema, serão consideradas as seguintes dimensões:

| Dimensão | Aplicação ao Artifício |
|---|---|
| Prejuízo aos usuários e às contratações | Perda de remuneração, trabalho não reconhecido, condições alteradas, contratação fraudulenta e dificuldade de resolver disputas. |
| Confidencialidade e privacidade | Exposição de credenciais, mensagens, dados cadastrais, arquivos privados, localização e hábitos dos usuários. |
| Integridade | Alteração de obras, propostas, preços, prazos, avaliações, permissões ou evidências. |
| Disponibilidade | Impedimento de autenticar, pesquisar artistas, negociar, publicar ou entregar obras; extensão e duração da interrupção. |
| Alcance | Número e tipo de usuários, registros e componentes afetados; possibilidade de propagação entre contas. |
| Recuperação e confiança | Possibilidade de restaurar dados e reconstruir acordos, permanência de cópias vazadas e prejuízo à reputação dos artistas e da plataforma. |

Para definir o impacto, será considerada a consequência mais grave que possa ser justificada para cada risco, sem somar ou fazer a média das dimensões afetadas. A quantidade de usuários atingidos será um dos fatores da avaliação, mas um dano grave a uma única pessoa também poderá receber impacto 4. Da mesma forma, o fato de uma funcionalidade ser pública não significa, por si só, que seu comprometimento terá impacto máximo.

No Artifício, uma inconsistência em um pedido que possa ser corrigida com a conferência das informações pode receber impacto 2. Já a exposição de mensagens privadas ou uma fraude que cause prejuízo relevante em uma contratação pode receber impacto 3. Situações como comprometimento do servidor, vazamento de dados privados de muitos usuários ou acesso indevido a amplas funções administrativas podem receber impacto 4. O impacto 1 será usado para pequenos transtornos, de correção rápida e sem prejuízo relevante.

Cada avaliação levará em conta as consequências do cenário descrito. Quando ainda faltarem definições sobre o alcance do problema ou a possibilidade de recuperação, a justificativa indicará o que foi considerado para atribuir a nota.

### 13.3 Cálculo e classificação dos riscos

A pontuação é obtida pela multiplicação dos valores de probabilidade e impacto:

**Pontuação = Probabilidade × Impacto**

A probabilidade segue a seção 13.1, e o impacto segue a seção 13.2. A classificação utiliza as faixas exigidas no enunciado:

| Pontuação | Nível do risco |
|---:|---|
| 1 a 3 | Baixo |
| 4 a 7 | Médio |
| 8 a 11 | Alto |
| 12 a 16 | Crítico |

A matriz resultante é:

| Probabilidade ↓ / Impacto → | 1 - Baixo | 2 - Moderado | 3 - Alto | 4 - Muito alto |
|---|---|---|---|---|
| 1 - Baixa | 1 - Baixo | 2 - Baixo | 3 - Baixo | 4 - Médio |
| 2 - Média-baixa | 2 - Baixo | 4 - Médio | 6 - Médio | 8 - Alto |
| 3 - Média-alta | 3 - Baixo | 6 - Médio | 9 - Alto | 12 - Crítico |
| 4 - Alta | 4 - Médio | 8 - Alto | 12 - Crítico | 16 - Crítico |

Por exemplo, R01 recebe probabilidade 3 e impacto 3: **3 × 3 = 9, nível Alto**. R28 recebe probabilidade 2 e impacto 4: **2 × 4 = 8, nível Alto**. Embora ambos sejam altos, o segundo envolve funções administrativas e poderá exigir prioridade diferenciada na seção 13.6.

A matriz segue os critérios definidos no enunciado do trabalho e auxilia na comparação dos riscos. A pontuação deve ser analisada junto com as consequências de cada cenário para orientar a priorização.

### 13.4 Registro de riscos

O registro apresenta a avaliação inicial dos riscos com base nas ameaças e nos casos de abuso da Etapa 1. As notas consideram os cenários descritos e os controles ainda como propostas, sem pressupor que as vulnerabilidades tenham sido confirmadas por testes. A seção 14.6 apresentará a estimativa do risco residual esperado após os tratamentos.

Os riscos R01 a R33 correspondem, respectivamente, às ameaças T01 a T33. R34 a R37 detalham situações dos casos de abuso CA04, CA15, CA16 e CA27, mantendo os identificadores originais da Etapa 1.

Os riscos R04 e R27 se aplicam caso o sistema utilize autenticação externa, e R26, caso ofereça a funcionalidade de reserva/carrinho.

Na coluna de casos de abuso, “-” indica que não há um caso específico equivalente à ameaça. Já “Relacionado” indica que o caso aborda uma situação próxima, mas não exatamente o mesmo evento.

| ID | Origem STRIDE | Caso de abuso | Evento de risco | Vulnerabilidade ou condição | Probabilidade | Impacto | Pontuação | Nível |
|---|---|---|---|---|---:|---:|---:|---|
| R01 | T01 - Spoofing | CA02 | Tomada da conta de um artista, com acesso a negociações e atuação fraudulenta em seu nome. | Credenciais comprometidas ou recuperação de senha vulnerável. | 3 | 3 | 9 | Alto |
| R02 | T02 - Spoofing | CA01 | Clientes contratam um perfil que utiliza indevidamente a identidade ou as obras de outro artista. | Cadastro sem verificação suficiente de identidade ou autoria. | 3 | 3 | 9 | Alto |
| R03 | T03 - Spoofing | CA02 | Reutilização de sessão comprometida para executar operações como outro usuário. | Captura de token válido e possibilidade de reutilização sem revogação eficaz. | 2 | 3 | 6 | Médio |
| R04 | T04 - Spoofing | CA03 | Acesso indevido a uma conta vinculada a um provedor de autenticação externa. | Uso dessa integração e comprometimento de credencial ou token aceito pelo provedor/aplicação. | 2 | 3 | 6 | Médio |
| R05 | T05 - Tampering | - | Registro de solicitação com preço-base ou prazo adulterado antes do envio. | Servidor confia em valores enviados pelo navegador sem validar as regras do artista. | 2 | 3 | 6 | Médio |
| R06 | T06 - Tampering | CA07 | Alteração unilateral das condições de uma contratação já aceita. | Ausência de proteção do estado aceito e de validação da concordância das partes. | 2 | 3 | 6 | Médio |
| R07 | T07 - Tampering | CA08 | Manipulação da reputação por avaliações falsas ou remoção de avaliações legítimas. | Avaliações sem vínculo válido com contratação ou sem autorização adequada para alteração. | 3 | 3 | 9 | Alto |
| R08 | T08 - Tampering | CA05 | Arquivo malicioso enviado ao portfólio compromete o servidor ou sobrescreve conteúdo. | Validação insuficiente de arquivos combinada com processamento ou armazenamento inseguro. | 2 | 4 | 8 | Alto |
| R09 | T09 - Tampering | - | Atualizações simultâneas sobrescrevem condições ou deixam um pedido em estado inconsistente. | Ausência de transações, controle de versão ou validação do estado durante atualizações concorrentes. | 2 | 2 | 4 | Médio |
| R10 | T10 - Repudiation | CA10 | Uma parte nega aceite, recusa ou alteração de proposta e impede a comprovação do acordo. | Histórico insuficiente para associar ação, autor, horário e condições vigentes. | 3 | 3 | 9 | Alto |
| R11 | T11 - Repudiation | CA10 | Disputa sobre entrega de comissão não pode ser esclarecida por falta de evidências. | Ausência de registro confiável de disponibilização, versão do arquivo e eventos de acesso. | 3 | 3 | 9 | Alto |
| R12 | T12 - Repudiation | CA09, CA10 | Condições negociadas em mensagens são negadas e não podem ser reconstruídas. | Histórico incompleto, alterável ou sem identificação confiável de autoria e sequência. | 3 | 3 | 9 | Alto |
| R13 | T13 - Repudiation | CA09 | Agente privilegiado apaga ou adultera auditoria para ocultar ações indevidas. | Permissão de alteração ou exclusão de logs sem proteção independente. | 2 | 4 | 8 | Alto |
| R14 | T14 - Repudiation | CA11 | Exclusão de conta elimina evidências de uma fraude ou disputa. | Processo de exclusão remove registros relacionados sem avaliar necessidade de preservação. | 2 | 3 | 6 | Médio |
| R15 | T15 - Information Disclosure | CA12 | Publicação de imagem revela metadados privados do artista. | Disponibilização do original com localização ou outros metadados desnecessários. | 3 | 3 | 9 | Alto |
| R16 | T16 - Information Disclosure | CA13 | Usuário não autorizado consulta dados cadastrais, mensagens ou informações privadas de terceiros. | Falha de autorização ou exposição excessiva de dados nas respostas da aplicação. | 2 | 3 | 6 | Médio |
| R17 | T17 - Information Disclosure | CA14 | Terceiro baixa ou compartilha arquivo ou relatório privado por seu endereço. | URL acessível sem autorização adequada ou link com validade e escopo inadequados. | 2 | 3 | 6 | Médio |
| R18 | T18 - Information Disclosure | CA16 (relacionado) | Exploração de consultas permite extrair em massa dados privados do banco. | Construção insegura de consultas e permissões de acesso à base excessivas. | 2 | 4 | 8 | Alto |
| R19 | T19 - Information Disclosure | CA13 | Administrador consulta conversas ou arquivos fora de uma finalidade autorizada. | Privilégios amplos sem restrição contextual e supervisão dos acessos. | 2 | 3 | 6 | Médio |
| R20 | T20 - Information Disclosure | CA17 | Terceiro infere a rotina de um artista a partir de indicadores de atividade. | Exposição de status e horários detalhados sem restrição adequada de visibilidade. | 3 | 3 | 9 | Alto |
| R21 | T21 - Denial of Service | CA18 | Volume automatizado de login ou recuperação degrada a autenticação de usuários legítimos. | Limites de frequência e proteção de recursos insuficientes para a carga recebida. | 4 | 3 | 12 | Crítico |
| R22 | T22 - Denial of Service | CA19; CA16 (sobrecarga) | Buscas repetidas e custosas degradam o catálogo e o atendimento a outras requisições. | Consultas sem limites adequados de custo, frequência, paginação ou concorrência. | 4 | 3 | 12 | Crítico |
| R23 | T23 - Denial of Service | CA05, CA20 | Uploads volumosos ou simultâneos esgotam recursos e interrompem entregas ou publicação. | Ausência de cotas e limites adequados de tamanho, quantidade e concorrência. | 3 | 3 | 9 | Alto |
| R24 | T24 - Denial of Service | CA06, CA20 | Processamento de imagens grandes ou malformadas torna o serviço indisponível. | Processamento sem limites de dimensões, tempo, memória ou isolamento de recursos. | 3 | 3 | 9 | Alto |
| R25 | T25 - Denial of Service | CA19 | Solicitações automatizadas inundam a conta de um artista e impedem atender pedidos legítimos. | Ausência de limites de envio e de mecanismos para conter solicitações abusivas. | 4 | 3 | 12 | Crítico |
| R26 | T26 - Denial of Service | - | Reservas artificiais tornam obras ou serviços indisponíveis para clientes legítimos. | Existência de reserva/carrinho sem expiração eficaz ou limites contra retenção abusiva. | 2 | 3 | 6 | Médio |
| R27 | T27 - Denial of Service | CA21 | Indisponibilidade do provedor externo impede temporariamente o login pelo método afetado. | Dependência do provedor e ausência de alternativa utilizável para os usuários afetados. | 2 | 2 | 4 | Médio |
| R28 | T28 - Elevation of Privilege | CA22 | Usuário comum executa funções administrativas por acesso direto às interfaces do servidor. | Ausência de verificação de autenticação e autorização nas operações administrativas. | 2 | 4 | 8 | Alto |
| R29 | T29 - Elevation of Privilege | CA23 | Usuário atribui a si próprio papel administrativo e passa a operar com privilégios elevados. | Aceitação de parâmetros de permissão enviados pelo cliente ou escrita indevida no cadastro de papéis. | 2 | 4 | 8 | Alto |
| R30 | T30 - Elevation of Privilege | CA24 | Usuário consulta conteúdo restrito, altera ou exclui obra de outro artista ao trocar seu identificador. | Servidor não verifica autorização sobre o recurso solicitado. | 2 | 3 | 6 | Médio |
| R31 | T31 - Elevation of Privilege | CA25 | Conta bloqueada continua a executar ações por uma sessão previamente válida. | Bloqueio não verificado nas operações ou sessão não revogada de forma eficaz. | 2 | 3 | 6 | Médio |
| R32 | T32 - Elevation of Privilege | CA25 | Ex-integrante da equipe mantém acesso a dados e funções administrativas. | Desligamento sem revogação efetiva de permissões, credenciais e sessões. | 2 | 4 | 8 | Alto |
| R33 | T33 - Elevation of Privilege | CA26 | Usuário com papéis de cliente e artista aprova ação que exigiria autorização independente. | Validação apenas do papel, sem verificar a parte representada e o contexto da contratação. | 2 | 3 | 6 | Médio |
| R34 | T01, T02 (relacionadas) - Spoofing / Repudiation | CA04 | Transferência informal de conta permite que terceiro negocie usando reputação e histórico do titular. | Compartilhamento de credenciais e falta de procedimento confiável para mudança de titularidade. | 3 | 3 | 9 | Alto |
| R35 | T03, T16 (relacionadas) - Information Disclosure / Spoofing | CA15 | Inspeção de tráfego expõe mensagens, credenciais ou dados de contratação. | Canal sem proteção adequada e possibilidade de observação do tráfego pelo atacante. | 2 | 3 | 6 | Médio |
| R36 | T18, T22 (relacionadas) - Information Disclosure / Denial of Service | CA16 | Coleta automatizada pela busca acumula informações privadas indevidamente presentes nos resultados. | Busca retorna campos restritos e permite enumeração e coleta sem limites suficientes. | 3 | 4 | 12 | Crítico |
| R37 | T06, T13 (relacionadas) - Tampering / Repudiation | CA27 | Administrador altera condições de contratação sem justificativa e sem histórico confiável. | Privilégios excessivos para alteração de contratos e auditoria insuficiente. | 2 | 3 | 6 | Médio |

### 13.5 Justificativas das avaliações

As justificativas explicam como cada risco pode ocorrer, quem ou o que pode ser afetado e por que ele recebeu aquela classificação. A probabilidade 2 indica situações que dependem de uma falha ou condição específica. A probabilidade 3 se aplica a abusos que podem ocorrer em situações comuns de uso ou ataque. Já a probabilidade 4 foi atribuída aos casos em que a falta de limites adequados facilita a repetição automatizada de ações. A avaliação considera a possibilidade de o ataque causar o dano descrito, pois uma tentativa nem sempre resulta em sucesso.

#### R01 - Tomada da conta de um artista

**Probabilidade 3:** O login e a recuperação de senha estão disponíveis pela aplicação e podem ser alvo de ataques comuns. Para assumir a conta, o atacante precisa obter uma credencial válida ou explorar uma falha na recuperação.

**Impacto 3:** Pode expor mensagens, permitir propostas fraudulentas e prejudicar o artista e seus clientes. A avaliação considera os danos causados pelo comprometimento de uma conta.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R02 - Criação de perfil com identidade ou obras de outro artista

**Probabilidade 3:** Um usuário comum pode criar um perfil e copiar informações ou obras públicas de outro artista. Para realizar a fraude, ainda precisa convencer clientes a contratar seus serviços.

**Impacto 3:** Pode prejudicar a reputação do artista verdadeiro e causar perdas financeiras aos clientes enganados. A exclusão do perfil falso não desfaz os prejuízos já causados.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R03 - Uso de sessão comprometida para acessar a conta de outra pessoa

**Probabilidade 2:** Depende de o atacante obter um token de sessão que ainda seja válido e possa ser reutilizado. Estar na mesma rede do usuário não basta quando a comunicação está protegida.

**Impacto 3:** Permite acessar mensagens, consultar contratações e fazer alterações em nome do titular, prejudicando sua privacidade e o controle sobre a conta.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R04 - Acesso indevido por autenticação externa

**Probabilidade 2:** Depende do uso de autenticação externa e de uma credencial ou token comprometido. Isso não significa que todo o provedor tenha sido afetado.

**Impacto 3:** Pode expor informações privadas e permitir fraudes na conta vinculada. A avaliação considera o impacto sobre essa conta, sem presumir que outras contas sejam afetadas.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R05 - Alteração de preço ou prazo antes do envio da solicitação

**Probabilidade 2:** Alterar os dados enviados pelo navegador é simples, mas o problema só ocorre se o servidor aceitar valores que não seguem as regras da contratação.

**Impacto 3:** Pode prejudicar o pagamento e o planejamento do artista, levando a uma contratação com preço ou prazo incorretos.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R06 - Alteração das condições de uma contratação sem concordância da outra parte

**Probabilidade 2:** Depende de uma contratação já aceita e de uma falha que permita alterar suas condições sem a autorização da outra parte.

**Impacto 3:** Pode modificar preço, prazo e quantidade de revisões combinados, causando trabalho não remunerado, descumprimento do acordo e conflitos entre cliente e artista.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R07 - Manipulação de avaliações

**Probabilidade 3:** Se o sistema não verificar adequadamente as avaliações, usuários comuns podem publicar avaliações falsas ou tentar remover avaliações legítimas para favorecer um perfil.

**Impacto 3:** Pode distorcer a reputação dos artistas e influenciar a escolha dos clientes, levando a contratações baseadas em informações falsas.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R08 - Envio de arquivo malicioso que compromete o servidor ou altera conteúdo

**Probabilidade 2:** Além do envio do arquivo, é necessária uma falha no processamento ou no armazenamento que permita executar conteúdo malicioso ou substituir arquivos.

**Impacto 4:** Se o servidor for comprometido, o ataque pode atingir a aplicação, os arquivos e os dados de vários usuários, prejudicando funções importantes da plataforma.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R09 - Alterações simultâneas deixam um pedido com informações inconsistentes

**Probabilidade 2:** Depende de duas operações alterarem o mesmo registro ao mesmo tempo e de o sistema não controlar corretamente essas alterações.

**Impacto 2:** A avaliação considera um problema limitado a um pedido, que possa ser corrigido pela conferência das informações com as partes. Se houver perdas graves ou outros registros forem afetados, o impacto deverá ser revisto.

**Classificação:** 2 × 2 = **4 - Médio**.

#### R10 - Usuário nega ter aceitado, recusado ou alterado uma proposta

**Probabilidade 3:** Essas ações fazem parte do uso comum da plataforma e podem ser negadas por qualquer participante. A falta de registros confiáveis dificulta comprovar o que aconteceu.

**Impacto 3:** Pode dificultar a resolução de disputas e a identificação das responsabilidades de clientes e artistas, causando perdas financeiras e retrabalho.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R11 - Falta de registros impede esclarecer uma disputa sobre a entrega

**Probabilidade 3:** Um artista pode afirmar que entregou a obra, enquanto o cliente nega o recebimento. Sem registros suficientes, essa situação pode ser difícil de esclarecer.

**Impacto 3:** Pode prejudicar o pagamento do artista, o acesso do cliente à obra e a resolução do conflito. O registro de download ajuda a verificar o acesso ao arquivo, mas não comprova que o cliente aceitou a entrega.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R12 - Usuário nega condições combinadas por mensagem

**Probabilidade 3:** A negociação por mensagens faz parte do funcionamento da plataforma. Se o histórico não for preservado adequadamente, uma das partes pode negar o que foi combinado.

**Impacto 3:** Pode dificultar a comprovação de preços, prazos e características da obra, causando conflitos e prejuízos para clientes e artistas.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R13 - Usuário privilegiado altera ou apaga registros para esconder ações indevidas

**Probabilidade 2:** Depende de acesso privilegiado aos registros e de falhas na proteção contra alterações ou exclusões.

**Impacto 4:** Pode eliminar provas de ações realizadas em várias contas e funções administrativas, dificultando investigações e permitindo que fraudes continuem.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R14 - Exclusão de conta apaga provas de fraude ou disputa

**Probabilidade 2:** Ocorre se a exclusão de uma conta também apagar registros necessários para esclarecer uma fraude ou disputa.

**Impacto 3:** Pode impedir a recuperação do histórico da contratação e dificultar a identificação dos responsáveis. A perda desses registros pode ser definitiva.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R15 - Imagem publicada revela informações privadas do artista

**Probabilidade 3:** O envio de imagens é uma atividade comum na plataforma. Se os metadados não forem removidos, terceiros podem consultar informações presentes no arquivo, como a localização onde a imagem foi produzida.

**Impacto 3:** Pode revelar locais privados e prejudicar a segurança e a privacidade do artista. Excluir a imagem da plataforma não elimina as cópias já obtidas por outras pessoas.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R16 - Acesso indevido a dados e mensagens de outros usuários

**Probabilidade 2:** Depende de uma falha no controle de acesso que permita consultar informações privadas sem autorização.

**Impacto 3:** Pode expor dados pessoais, mensagens e detalhes de negociações. A avaliação considera uma exposição limitada; o vazamento de toda a base é tratado em R18.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R17 - Acesso a arquivo privado por um link desprotegido

**Probabilidade 2:** Depende de alguém obter o endereço do arquivo e conseguir acessá-lo sem que o sistema verifique sua autorização.

**Impacto 3:** Pode expor arquivos e informações de vendas ou contratações. Mesmo após a proteção do link, cópias já baixadas podem continuar circulando.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R18 - Falha nas consultas permite retirar dados privados do banco em grande quantidade

**Probabilidade 2:** Depende de uma falha nas consultas ao banco que permita acessar dados sem autorização. Fazer buscas públicas de forma automática não é suficiente para esse ataque.

**Impacto 4:** Pode expor informações privadas de muitos clientes e artistas. Corrigir a falha não desfaz o vazamento dos dados já obtidos.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R19 - Administrador acessa conversas ou arquivos sem necessidade autorizada

**Probabilidade 2:** Depende de uma pessoa com acesso administrativo e de restrições insuficientes sobre o que ela pode consultar nas atividades de suporte ou moderação.

**Impacto 3:** Prejudica a privacidade dos envolvidos e a confiança na administração. A avaliação considera acessos a casos específicos; uma exposição de muitos usuários exigiria rever o impacto.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R20 - Monitoramento da rotina de um artista

**Probabilidade 3:** Se o status e os horários de atividade forem públicos, qualquer pessoa pode acompanhá-los e registrar padrões ao longo do tempo.

**Impacto 3:** Pode revelar hábitos do artista e facilitar assédio, prejudicando sua privacidade.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R21 - Tentativas automáticas de login ou recuperação de senha sobrecarregam o serviço

**Probabilidade 4:** Se não houver limites adequados, um robô pode repetir muitas tentativas com facilidade e consumir os recursos do serviço. A nota considera essa facilidade, e não um histórico de ataques já ocorridos.

**Impacto 3:** Pode impedir temporariamente o acesso de clientes e artistas e interromper negociações. A avaliação considera uma interrupção relevante, sem perda de dados ou paralisação prolongada de toda a plataforma.

**Classificação:** 4 × 3 = **12 - Crítico**.

#### R22 - Buscas automáticas sobrecarregam a plataforma

**Probabilidade 4:** Sem limites adequados, um robô pode repetir buscas que exigem muitos recursos, sem precisar de acesso administrativo.

**Impacto 3:** Pode causar lentidão ou indisponibilidade, dificultando a busca por artistas e novas contratações. O cenário considera que o serviço possa ser recuperado após controlar a sobrecarga.

**Classificação:** 4 × 3 = **12 - Crítico**.

#### R23 - Excesso de uploads interrompe publicações ou entregas

**Probabilidade 3:** Um usuário pode enviar arquivos repetidamente ou em grande quantidade. A sobrecarga depende dos limites de envio e da capacidade disponível no servidor.

**Impacto 3:** Pode impedir a publicação de obras e a entrega de arquivos aos clientes. A avaliação considera uma interrupção relevante, sem perda de dados ou comprometimento de todo o servidor.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R24 - Processamento de imagens grandes ou malformadas interrompe o serviço

**Probabilidade 3:** Como o envio de imagens faz parte do uso comum, arquivos que exigem muitos recursos podem chegar ao servidor. Sem limites adequados, seu processamento pode sobrecarregar o sistema.

**Impacto 3:** Pode interromper a publicação de portfólios e prejudicar outras funções da plataforma. A avaliação considera uma indisponibilidade temporária, sem perda permanente de dados.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R25 - Excesso de solicitações impede o artista de atender pedidos legítimos

**Probabilidade 4:** Se não houver limites eficazes, um robô pode enviar muitas solicitações para o mesmo artista com facilidade.

**Impacto 3:** Pode desorganizar a fila de pedidos, dificultar o atendimento e prejudicar a renda do artista. Mesmo atingindo uma única pessoa, o dano à sua atividade pode ser relevante.

**Classificação:** 4 × 3 = **12 - Crítico**.

#### R26 - Reservas abusivas bloqueiam obras ou serviços

**Probabilidade 2:** Depende de o sistema oferecer reservas e de suas regras permitirem manter obras ou serviços bloqueados de forma abusiva. A implementação dessa funcionalidade ainda não está confirmada.

**Impacto 3:** Pode impedir contratações legítimas e causar perda de receita aos artistas, mesmo que o restante do site continue funcionando.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R27 - Falha no provedor externo impede o login

**Probabilidade 2:** Depende de uma falha no serviço externo de autenticação. Não há dados sobre sua disponibilidade que permitam considerar essas falhas frequentes.

**Impacto 2:** A avaliação considera uma interrupção temporária para os usuários daquele método de login, sem perda de dados. Se todos dependerem do provedor ou a interrupção durar muito tempo, o impacto será maior.

**Classificação:** 2 × 2 = **4 - Médio**.

#### R28 - Usuário comum acessa funções administrativas

**Probabilidade 2:** Depende de uma falha na verificação de permissões pelo servidor. Conhecer o endereço de uma função administrativa não deveria ser suficiente para utilizá-la.

**Impacto 4:** Pode permitir alterações em configurações, ações de moderação e bloqueios de vários usuários, comprometendo a administração da plataforma.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R29 - Usuário altera suas permissões para se tornar administrador

**Probabilidade 2:** Depende de uma falha que permita ao usuário modificar suas próprias permissões e obter acesso administrativo.

**Impacto 4:** Pode dar acesso contínuo a funções importantes, permitindo expor dados e alterar várias contas, com prejuízos graves para a plataforma.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R30 - Usuário acessa, altera ou exclui obra de outro artista

**Probabilidade 2:** Trocar o identificador de uma obra na requisição é simples, mas o acesso indevido depende de o servidor não verificar a permissão do usuário sobre ela.

**Impacto 3:** Pode permitir a alteração ou exclusão de obras e o acesso a arquivos privados de outro artista. A visualização normal de um portfólio público não faz parte desse risco.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R31 - Usuário bloqueado continua usando uma sessão ativa

**Probabilidade 2:** Depende de o usuário já estar conectado quando for bloqueado e de o sistema não aplicar o bloqueio à sessão existente.

**Impacto 3:** Pode permitir que o usuário continue praticando a fraude ou o abuso que motivou o bloqueio, prejudicando outras pessoas e a atuação da moderação.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R32 - Ex-integrante da equipe mantém acesso administrativo

**Probabilidade 2:** Depende de uma pessoa que já tinha acesso administrativo continuar com permissões ou sessões válidas após sair da equipe.

**Impacto 4:** Pode permitir o acesso, a alteração ou a exclusão de dados de vários usuários, além do uso indevido de funções importantes da plataforma.

**Classificação:** 2 × 4 = **8 - Alto**.

#### R33 - Usuário com os papéis de cliente e artista aprova ações sem autorização da outra parte

**Probabilidade 2:** Depende de o usuário possuir os dois papéis e de o sistema não verificar qual parte ele representa naquela contratação.

**Impacto 3:** Pode permitir aprovações indevidas e vantagens financeiras ou de reputação, sem a concordância da outra parte, dificultando comprovar quem autorizou a ação.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R34 - Transferência de conta permite usar a reputação de outro artista

**Probabilidade 3:** O titular pode compartilhar suas credenciais voluntariamente. Se a identidade de quem usa a conta não for verificada, outra pessoa pode continuar negociando pelo mesmo perfil.

**Impacto 3:** Pode enganar clientes que confiam no histórico do artista e atribuir ao titular ações realizadas por terceiros. Neste caso, há compartilhamento voluntário da conta, diferente da tomada de conta tratada em R01.

**Classificação:** 3 × 3 = **9 - Alto**.

#### R35 - Interceptação da comunicação expõe informações privadas

**Probabilidade 2:** Depende de o atacante conseguir observar a comunicação e de haver falhas em sua proteção. Estar na mesma rede não permite, por si só, ler dados protegidos por TLS.

**Impacto 3:** Pode expor mensagens, dados de contratação e credenciais, permitindo seu uso indevido. A avaliação considera os usuários cuja comunicação foi interceptada.

**Classificação:** 2 × 3 = **6 - Médio**.

#### R36 - Busca automatizada coleta informações privadas exibidas indevidamente

**Probabilidade 3:** Se a busca mostrar dados privados que deveriam estar protegidos, um robô pode coletá-los por meio de consultas comuns. Neste caso, não é necessário explorar uma falha nas consultas ao banco, como em R18.

**Impacto 4:** Pode expor informações privadas de muitos usuários, que podem continuar circulando após a correção. A coleta de informações publicadas intencionalmente para consulta pública não recebe essa mesma avaliação.

**Classificação:** 3 × 4 = **12 - Crítico**.

#### R37 - Administrador altera uma contratação sem justificativa ou registro adequado

**Probabilidade 2:** Depende de acesso administrativo e da falta de controles que limitem as alterações e registrem o que foi feito.

**Impacto 3:** Pode modificar preços, prazos e condições combinadas, causando prejuízo às partes e dificultando a contestação. A avaliação considera alterações em uma contratação, sem presumir comprometimento de toda a administração.

**Classificação:** 2 × 3 = **6 - Médio**.


### 13.6 Priorização dos riscos


## 14. Tratamento dos riscos com o NIST CSF 2.0

### 14.1 Estratégias de tratamento

A resposta aos riscos identificados no **Artifício** estrutura-se em quatro estratégias: Reduzir, Compartilhar, Evitar e Aceitar.

| Estratégia | Descrição | Aplicação no Sistema Artifício | Casos de Abuso Relacionados |
|---|---|---|---|
| **Reduzir (Mitigar)** | Implementar medidas e controles técnicos ou operacionais para diminuir a probabilidade de ocorrência ou atenuar o impacto do evento. | **Estratégia predominante no projeto.** Aplicação de validações de regras e preços no backend, controle rigoroso de permissões por perfil, proteção de sessões e recuperação de conta, limite de requisições contra ataques de força bruta (*rate limiting*), proteção de logs e integridade de uploads de imagens. | `CA02`, `CA05`, `CA06`, `CA07`, `CA09`, `CA18`, `CA19`, `CA22`, `CA23`, `CA24` |
| **Compartilhar (Transferir)** | Atribuir parte da operação, da custódia de dados críticos ou das consequências a um terceiro especializado. | **Operações de pagamento e autenticação externa.** Delegação do processamento de pagamentos para gateways especializados em conformidade com normas de segurança e utilização de provedores de identidade consolidados para autenticação externa, transferindo a custódia das credenciais primárias. | `CA03`, `CA21` |
| **Evitar** | Eliminar a atividade, funcionalidade ou condição de arquitetura que dá origem ao risco. | **Decisões de escopo e arquitetura.** Restrição estrita de tipos de arquivo aceitos no upload, rejeitando categoricamente executáveis ou scripts e aceitando somente imagens; e recusa arquitetural em armazenar números de cartões de crédito na base própria da aplicação. | `CA05` |
| **Aceitar** | Reconhecer conscientemente a existência do risco e mantê-lo sob observação, quando seu impacto for muito reduzido ou o custo de eliminação for desproporcional. | **Riscos residuais toleráveis.** Exposição controlada de informações que são inerentemente públicas para a finalidade da plataforma, como visualização do portfólio e consultas públicas no catálogo de artistas por visitantes, mantendo apenas monitoramento básico sem bloquear o uso legítimo. | `CA17` |

#### Critérios para escolha e formalização da aceitação

A formalização de limites para aceitação segue critérios objetivos:

- **Riscos Críticos e Altos:** Não podem ser aceitos sob nenhuma hipótese. Devem ser tratados obrigatoriamente pelas estratégias de **Reduzir**, **Compartilhar** ou **Evitar**.
- **Riscos Médios:** A prioridade é a **redução** via controles na aplicação. Quando a remediação imediata não for viável, devem ser estabelecidos controles compensatórios e prazos formais de resolução.
- **Condições para Aceitação de Riscos:**
  - **Motivo da decisão:** Justificativa clara demonstrando que o risco possui impacto insignificante ou que o controle geraria impacto inaceitável na usabilidade.
  - **Aprovação:** Qualquer decisão de aceitação deve ser formalmente registrada e acordada pela equipe de segurança e desenvolvimento do projeto.
  - **Condições e revisão:** Todo risco aceito permanece registrado no inventário de riscos e deve ser reavaliado periodicamente ou sempre que houver grandes alterações na arquitetura da plataforma.

### 14.2 Funções do NIST CSF 2.0

O NIST Cybersecurity Framework (CSF) 2.0 organiza a gestão e a mitigação de riscos de segurança da informação em seis funções nucleares: **Govern (Governança)**, **Identify (Identificação)**, **Protect (Proteção)**, **Detect (Detecção)**, **Respond (Resposta)** e **Recover (Recuperação)**.

No contexto do **Artifício**, essas funções não devem ser tratadas como controles isolados ou ferramentas prontas. Elas funcionam como uma taxonomia estruturada de objetivos: a função estabelece a área estratégica de atuação, o resultado esperado (*outcome*) define o objetivo defensivo no fluxo da aplicação, e o controle técnico representa a salvaguarda concreta implementada em código, arquitetura ou processo operacional.

A distinção clara entre esses três níveis orienta o planejamento de segurança:

| Função | Finalidade Estrutural | Resultado Esperado no Artifício | Exemplo de Controle Técnico |
|---|---|---|---|
| **Govern (GV)** | Estabelecer a estratégia de segurança, políticas, limites operacionais e critérios de responsabilidade da plataforma. | Definir formalmente os limites de acesso dos moderadores, políticas de retenção de histórico para disputas e termos de uso que coíbam abusos contratuais. | Política de autorização contextual para administradores e matriz formal de responsabilidades sobre dados transacionais. |
| **Identify (ID)** | Conhecer e catalogar ativos, fluxos de dados, dependências externas e superfícies vulneráveis do sistema. | Mapear quais tabelas, rotas de API e arquivos de portfólio concentram informações sensíveis, dados pessoais ou valor comercial. | Inventário de ativos de dados (banco relacional, buckets de armazenamento, provedores OAuth) e mapeamento do ciclo de vida das credenciais. |
| **Protect (PR)** | Aplicar salvaguardas técnicas preventivas para assegurar a continuidade dos serviços e conter ameaças na origem. | Impedir que falhas de autorização permitam adulteração de propostas, sequestro de sessões ou injeção de arquivos maliciosos nos portfólios. | Validação estrita de autorização e recálculo de preços no backend, sanitização de cabeçalhos de imagens, tokens de sessão com flags seguras e MFA para contas com acesso administrativo. |
| **Detect (DE)** | Monitorar a atividade da aplicação para identificar eventos anômalos, ataques automatizados e falhas de integridade. | Sinalizar em tempo hábil tentativas de força bruta em autenticação, varreduras abusivas no catálogo de artistas e alterações inesperadas em registros. | Registro de logs estruturados de auditoria, alarmes para falhas reiteradas de autenticação e limitação de taxa (*rate limiting*) com monitoramento de requisições anômalas. |
| **Respond (RS)** | Conter incidentes em andamento, mitigar prejuízos operacionais e executar ações corretivas imediatas. | Interromper rapidamente o abuso de contas comprometidas ou congelar negociações sob suspeita de fraude antes do fechamento financeiro. | Mecanismo de revogação imediata de sessões ativas, bloqueio operacional de contas denunciadas e isolamento de arquivos suspeitos para perícia. |
| **Recover (RC)** | Restaurar serviços e recompor a integridade de dados afetados por incidentes, falhas lógicas ou indisponibilidade. | Reconstituir portfólios danificados e recompor a cadeia de eventos de contratações a partir de registros íntegros. | Rotinas automatizadas de backup externo com validação periódica de restauração e conciliação de estado entre eventos de contratação. |

#### Aplicação contextualizada ao ciclo de vida do Artifício

##### 1. Govern (Governança)
A inclusão explícita da função *Govern* no NIST CSF 2.0 reconhece que medidas técnicas falham quando desprovidas de sustentação política e organizacional. No Artifício, a governança estabelece a segregação estrita de papéis (cliente, artista e administrador). Define os critérios sob os quais um moderador tem prerrogativa para auditar comunicações privadas em disputas, além de formalizar os prazos de retenção de registros exigidos para resolução jurídica de conflitos sobre comissões.

##### 2. Identify (Identificação)
A proteção efetiva exige conhecer os ativos do sistema e os vetores de exposição. No Artifício, os ativos públicos (catálogo, portfólios e perfis) exigem garantias de integridade e disponibilidade, enquanto os ativos restritos (mensagens de negociação, propostas comerciais, dados cadastrais e credenciais) exigem proteção rigorosa de confidencialidade. A função *Identify* assegura que os esforços de segurança foquem nas superfícies onde uma brecha causaria dano desproporcional à reputação e à subsistência dos criadores.

##### 3. Protect (Proteção)
Concentra a implementação prática das defesas de desenvolvimento seguro na aplicação. Compreende três eixos principais:
- **Identidade e Autorização:** Autenticação resistente, hashes de senha atualizados (Argon2 ou bcrypt), invalidação de sessões em eventos críticos e verificação contínua de permissões no backend (impedindo que a alteração de parâmetros em URLs conceda acesso a obras ou pedidos alheios).
- **Integridade das Contratações:** Validação no servidor de que preços, prazos e condições acordadas não possam ser adulterados pelo cliente no frontend durante o envio da proposta.
- **Higiene no Upload:** Rejeição categórica de extensões executáveis, validação de tipos MIME reais e remoção de metadados EXIF que possam expor geolocalização e rotina de artistas.

##### 4. Detect (Detecção)
A camada preventiva não elimina a necessidade de visibilidade operacional. A detecção no Artifício fundamenta-se em logs de auditoria centralizados e com proteção contra adulteração. O objetivo central é fornecer telemetria para diferenciar picos legítimos de visitas de ataques automatizados de força bruta ou varreduras de extração em massa de dados (*scraping*).

##### 5. Respond (Resposta)
Define as capacidades operacionais para neutralizar incidentes em curso. O sistema deve disponibilizar ferramentas administrativas de contenção pontual, como o cancelamento seletivo de sessões ativas de uma conta sob ataque, sem exigir a exclusão do usuário do banco, preservando o histórico para análise e possibilitando a recuperação legítima da titularidade.

##### 6. Recover (Recuperação)
Trata da restauração de estados íntegros. Envolve políticas de recuperação rápida de arquivos de imagem e dados relacionais em caso de incidentes em provedores de armazenamento ou falhas em operações concorrentes, assegurando que artistas e clientes não sofram perda irreversível de entregas ou comprovações de pagamento.

### 14.3 Mapeamento dos riscos para o NIST CSF


### 14.4 Plano de tratamento
O plano de tratamento foi elaborado a partir dos riscos identificados na seção 13.4 e das estratégias definidas na seção 14.1. Os controles propostos têm como objetivo reduzir a probabilidade de ocorrência dos eventos ou limitar seus impactos sobre os usuários, dados e componentes do Artifício.

A estratégia predominante é "Reduzir", uma vez que a maioria dos riscos está relacionada a funcionalidades necessárias ao funcionamento da plataforma e, portanto, não pode ser simplesmente eliminada. A estratégia "Compartilhar" será utilizada principalmente quando houver dependência de serviços externos, enquanto "Evitar" será aplicada a funcionalidades ou condições que possam ser retiradas da arquitetura. A aceitação será restrita a riscos residuais considerados compatíveis com o funcionamento do sistema.


| Risco | Estratégia | Controles propostos | Funções do NIST | Responsáveis | Evidências e verificação |
|---|---|---|---|---|---|
| **R01 – Tomada da conta de um artista** | Reduzir | Armazenamento seguro de senhas; recuperação de senha protegida; limitação de tentativas de login; proteção de sessões; invalidação de sessões após eventos críticos; MFA para contas administrativas. | Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes de login e recuperação de senha; testes de sessão; verificação dos logs de autenticação; simulação de comprometimento de conta. |
| **R02 – Perfil falso utilizando identidade ou obras de outro artista** | Reduzir | Denúncia de perfis; registro da criação e alteração de perfis; mecanismos de moderação; possibilidade de remoção de conteúdo comprovadamente fraudulento; preservação de evidências para investigação. | Govern, Protect, Detect, Respond | Administração e desenvolvimento | Testes do fluxo de denúncia; registros de moderação; auditoria de alterações de perfil. |
| **R03 – Reutilização de sessão comprometida** | Reduzir | Tokens de sessão com validade limitada; cookies `HttpOnly`, `Secure` e `SameSite`; invalidação de sessões após logout, bloqueio ou alteração sensível; proteção contra reutilização indevida. | Protect, Detect, Respond | Desenvolvimento | Testes de expiração e revogação; tentativa de utilização de sessão invalidada; inspeção das configurações dos cookies. |
| **R04 – Acesso indevido por autenticação externa** | Compartilhar | Utilização de provedor de identidade especializado; uso de OAuth 2.0/OpenID Connect quando aplicável; validação de tokens; não armazenamento das credenciais primárias do provedor. | Govern, Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes de autenticação; validação de tokens; documentação da integração; testes de revogação. |
| **R05 – Alteração de preço ou prazo antes da solicitação** | Reduzir | Validação dos preços, prazos e condições no backend; não confiar nos valores enviados pelo frontend; recuperação das regras armazenadas para o artista; registro dos dados utilizados na solicitação. | Protect, Detect | Desenvolvimento | Testes alterando parâmetros no navegador; comparação entre dados enviados e valores aceitos pelo servidor. |
| **R06 – Alteração de contratação sem concordância da outra parte** | Reduzir | Controle de estados da contratação; validação da autorização de cada parte; registro das alterações; confirmação das condições antes da alteração; controle de concorrência. | Protect, Detect, Respond | Desenvolvimento | Testes de alteração sem autorização; testes concorrentes; auditoria do histórico da contratação. |
| **R07 – Manipulação de avaliações** | Reduzir | Permitir avaliação somente após contratação válida; vincular avaliação ao usuário e à contratação; restringir alterações e exclusões; registrar operações de moderação. | Protect, Detect, Respond | Desenvolvimento e administração | Testes tentando avaliar sem contratação; testes de alteração/exclusão; auditoria das avaliações. |
| **R08 – Arquivo malicioso compromete servidor ou conteúdo** | Evitar/Reduzir | Restringir formatos aceitos; validar conteúdo real do arquivo; rejeitar executáveis e scripts; armazenar arquivos fora de diretórios executáveis; isolar processamento; aplicar limites de tamanho. | Protect, Detect | Desenvolvimento e infraestrutura | Testes com extensões falsas, arquivos inválidos e conteúdo não permitido; inspeção do armazenamento. |
| **R09 – Atualizações simultâneas geram inconsistência** | Reduzir | Uso de transações; controle de concorrência; validação do estado atual antes da alteração; controle de versão quando necessário. | Protect, Detect, Recover | Desenvolvimento | Testes com requisições simultâneas; verificação da consistência dos registros após concorrência. |
| **R10 – Negação de aceite, recusa ou alteração de proposta** | Reduzir | Registro de ações com usuário, data, hora, proposta e estado; preservação do histórico das alterações; proteção dos registros contra adulteração. | Protect, Detect, Respond | Desenvolvimento | Inspeção do histórico; testes de aceite/recusa; verificação dos registros gerados. |
| **R11 – Falta de evidências sobre a entrega da comissão** | Reduzir | Registro da disponibilização da obra; identificação da versão do arquivo; registro de acesso/download; armazenamento das informações de entrega. | Protect, Detect, Recover | Desenvolvimento e infraestrutura | Testes de entrega; verificação dos registros de acesso; recuperação do histórico de uma entrega. |
| **R12 – Negação de condições combinadas por mensagem** | Reduzir | Preservação do histórico de mensagens; identificação de remetente e horário; proteção contra alteração retroativa; associação entre mensagens e contratação. | Protect, Detect, Respond | Desenvolvimento | Testes de mensagens; auditoria do histórico; tentativa de alteração de mensagens já registradas. |
| **R13 – Alteração ou exclusão de logs por usuário privilegiado** | Reduzir | Restrição de acesso aos logs; armazenamento separado; controle de permissões; registro de acesso aos próprios logs; cópias protegidas contra alteração. | Govern, Protect, Detect, Recover | Infraestrutura e administração | Testes de permissões; tentativa de alteração de logs; verificação da integridade e existência de cópias. |
| **R14 – Exclusão de conta elimina evidências** | Reduzir | Política de retenção; separação entre exclusão da conta e preservação de registros necessários; anonimização quando aplicável; preservação de evidências relacionadas a disputas. | Govern, Protect, Recover | Administração e desenvolvimento | Testes de exclusão; verificação dos registros preservados; documentação da política de retenção. |
| **R15 – Metadados privados expostos em imagens** | Reduzir | Remoção de metadados EXIF desnecessários das cópias públicas; processamento das imagens antes da disponibilização; preservação do original somente quando necessário e protegido. | Protect, Detect | Desenvolvimento | Upload de imagens com GPS/EXIF; inspeção do arquivo disponibilizado publicamente. |
| **R16 – Acesso indevido a dados e mensagens** | Reduzir | Autorização no backend; separação entre dados públicos e privados; controle de acesso por usuário e recurso; minimização dos dados retornados pela API. | Protect, Detect | Desenvolvimento | Testes de acesso horizontal e vertical; testes de API; tentativa de acessar mensagens e dados de terceiros. |
| **R17 – Acesso a arquivo privado por link desprotegido** | Reduzir | URLs não públicas; autorização antes do download; links temporários quando necessários; validação do usuário e do recurso solicitado. | Protect, Detect | Desenvolvimento e infraestrutura | Testes com URL compartilhada; tentativa de acesso após expiração; tentativa de acesso por outro usuário. |
| **R18 – Extração em massa de dados privados do banco** | Reduzir | Consultas parametrizadas; princípio do menor privilégio para o banco; validação das consultas; separação entre dados públicos e privados; monitoramento de consultas anômalas. | Identify, Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes de consulta; análise de permissões do banco; testes de extração indevida; monitoramento de consultas. |
| **R19 – Administrador acessa dados sem necessidade autorizada** | Reduzir | Privilégio mínimo; autorização contextual; registro de acessos administrativos; revisão periódica de permissões; necessidade de justificativa para acessos sensíveis. | Govern, Protect, Detect | Administração e desenvolvimento | Auditoria de acessos administrativos; revisão de permissões; testes de acesso sem autorização contextual. |
| **R20 – Monitoramento da rotina de artista** | Reduzir | Não disponibilizar status detalhado desnecessariamente; permitir configuração de visibilidade; reduzir precisão de horários; não expor informações de atividade que não sejam necessárias. | Govern, Protect | Desenvolvimento e administração | Testes de visibilidade; verificação dos dados retornados pela API; revisão das configurações de privacidade. |
| **R21 – Sobrecarga por login ou recuperação automatizada** | Reduzir | Rate limiting; limitação progressiva de tentativas; mecanismos contra automação; monitoramento de tentativas; proteção específica para recuperação de senha. | Protect, Detect, Respond, Recover | Infraestrutura e desenvolvimento | Testes de carga; simulação de tentativas automatizadas; métricas de requisições; verificação dos bloqueios. |
| **R22 – Buscas automáticas sobrecarregam a plataforma** | Reduzir | Paginação; limites de frequência; controle de custo das consultas; limites de resultados; cache quando adequado; monitoramento de consultas custosas. | Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes de carga sobre busca; métricas de tempo de resposta; verificação do rate limiting. |
| **R23 – Excesso de uploads interrompe publicações ou entregas** | Reduzir | Limite de tamanho; limite de quantidade; cotas por usuário; controle de concorrência; armazenamento separado; fila para processamento pesado. | Protect, Detect, Recover | Desenvolvimento e infraestrutura | Testes de múltiplos uploads; monitoramento de armazenamento; verificação das cotas e limites. |
| **R24 – Imagens grandes ou malformadas interrompem o serviço** | Reduzir | Limites de dimensões; limite de memória e tempo de processamento; validação antes do processamento; isolamento de workers; rejeição de arquivos excessivamente complexos. | Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes com imagens de grandes dimensões e arquivos malformados; monitoramento de CPU e memória. |
| **R25 – Solicitações automatizadas inundam artista** | Reduzir | Limite de solicitações por usuário; limitação por período; detecção de comportamento automatizado; bloqueio temporário; ferramentas para denunciar abuso. | Protect, Detect, Respond | Desenvolvimento e administração | Testes de envio repetitivo; registros de bloqueio; métricas de solicitações por usuário. |
| **R26 – Reservas abusivas bloqueiam obras ou serviços** | Evitar/Reduzir | Caso a funcionalidade seja implementada, utilizar expiração automática das reservas, limite de reservas simultâneas e liberação automática dos itens. Caso a funcionalidade não seja implementada, o risco é evitado. | Govern, Protect | Desenvolvimento | Testes de expiração; testes de múltiplas reservas; verificação da liberação automática. |
| **R27 – Indisponibilidade de provedor externo de autenticação** | Compartilhar/Reduzir | Utilização de provedor especializado; tratamento adequado de falhas; manutenção de método alternativo de autenticação quando aplicável; monitoramento da disponibilidade do serviço externo. | Govern, Protect, Recover | Infraestrutura e desenvolvimento | Testes de indisponibilidade do provedor; verificação do comportamento da aplicação durante a falha. |
| **R28 – Usuário comum acessa funções administrativas** | Reduzir | Autenticação e autorização no backend para todas as operações administrativas; controle de acesso baseado em papéis; negação por padrão; testes de acesso direto às rotas administrativas. | Govern, Protect, Detect | Desenvolvimento e administração | Testes de acesso direto; testes com conta comum; logs de tentativas negadas. |
| **R29 – Usuário altera suas próprias permissões** | Reduzir | Permissões definidas exclusivamente no servidor; separação entre dados de perfil e papéis; proibição de alteração de privilégios pelo próprio usuário; controle de acesso administrativo. | Govern, Protect, Detect | Desenvolvimento e administração | Testes alterando parâmetros de papel; inspeção das regras de autorização; auditoria de alterações de permissões. |
| **R30 – Acesso a obra de outro artista** | Reduzir | Verificação de propriedade do recurso no backend; autorização para cada operação de leitura, alteração e exclusão; não confiar no identificador enviado pelo cliente. | Protect, Detect | Desenvolvimento | Testes alterando IDs de obras; testes de acesso entre diferentes contas; logs de acesso negado. |
| **R31 – Conta bloqueada continua usando sessão ativa** | Reduzir | Revogação de sessões no bloqueio; verificação do estado da conta em operações sensíveis; invalidação de tokens; mecanismo administrativo para encerramento das sessões. | Protect, Detect, Respond | Desenvolvimento e administração | Testes bloqueando conta com sessão ativa; tentativa de realizar operações após bloqueio. |
| **R32 – Ex-integrante mantém acesso administrativo** | Reduzir | Processo de desligamento com revogação de credenciais; encerramento de sessões; revisão de permissões; princípio do menor privilégio; inventário de contas administrativas. | Govern, Protect, Detect | Administração e infraestrutura | Simulação de desligamento; verificação de revogação; auditoria das contas administrativas. |
| **R33 – Usuário com dois papéis aprova ação indevidamente** | Reduzir | Verificação do contexto da contratação; identificação da parte representada; separação das autorizações do cliente e do artista; validação independente das operações sensíveis. | Govern, Protect, Detect | Desenvolvimento | Testes com usuário possuindo múltiplos papéis; tentativa de aprovação em nome da outra parte. |
| **R34 – Transferência informal de conta** | Evitar/Reduzir | Definir conta como pessoal e intransferível; proibir compartilhamento de credenciais; procedimento administrativo para situações excepcionais; registro de alterações relevantes da conta. | Govern, Protect, Detect | Administração e desenvolvimento | Testes do fluxo de alteração de titularidade; auditoria das alterações; documentação dos termos de uso. |
| **R35 – Interceptação de comunicação** | Reduzir | HTTPS/TLS em toda comunicação; proteção de cookies e sessões; não transmitir informações sensíveis em canais inseguros; configuração adequada dos certificados. | Protect, Detect | Infraestrutura e desenvolvimento | Testes de conexão; inspeção de certificados; análise de tráfego; tentativa de comunicação sem TLS. |
| **R36 – Busca automatizada coleta informações privadas** | Reduzir | Separação entre campos públicos e privados; filtragem dos resultados da busca; autorização no backend; paginação; rate limiting; monitoramento de consultas automatizadas. | Identify, Protect, Detect, Respond | Desenvolvimento e infraestrutura | Testes de busca com diferentes usuários; inspeção das respostas da API; testes automatizados de enumeração. |
| **R37 – Administrador altera contratação sem justificativa ou histórico** | Reduzir | Limitação das alterações administrativas; autorização contextual; registro obrigatório de justificativa; auditoria das alterações; preservação do estado anterior da contratação. | Govern, Protect, Detect, Respond | Administração e desenvolvimento | Testes de alterações administrativas; auditoria dos registros; verificação da justificativa e do histórico anterior. |

### 14.5 Ordem inicial de implementação

A ordem inicial de implementação considera a pontuação dos riscos, a extensão das consequências e as dependências entre os controles. Dessa forma, a ordem não é determinada exclusivamente pela pontuação: controles estruturais que servem de base para diversas funcionalidades devem ser implementados antes de controles mais específicos.

| Ordem | Riscos relacionados | Controles prioritários | Justificativa |
|---:|---|---|---|
| **1** | R28, R29, R30, R31, R32, R33 | Autenticação, autorização no backend, controle de papéis e revogação de permissões | O controle de acesso é uma dependência de praticamente todas as funcionalidades sensíveis. |
| **2** | R01, R03, R04, R34, R35 | Proteção de contas, sessões, recuperação e comunicação | Protege a identidade dos usuários e reduz riscos de tomada de contas e exposição de credenciais. |
| **3** | R21, R22, R25 | Rate limiting, proteção contra automação e limites de recursos | São riscos críticos e podem afetar diretamente a disponibilidade do sistema. |
| **4** | R36, R16, R17, R18, R19, R20 | Separação de dados públicos/privados, autorização e proteção de arquivos | Reduz riscos de exposição de dados pessoais, mensagens, arquivos e informações privadas. |
| **5** | R05, R06, R09, R37 | Validação de contratações, controle de concorrência e auditoria | Protege a integridade de preços, prazos e condições negociadas. |
| **6** | R07, R10, R11, R12, R13, R14 | Auditoria, histórico e preservação de evidências | Permite reconstruir eventos, resolver disputas e investigar alterações indevidas. |
| **7** | R08, R15, R23, R24 | Segurança de uploads e processamento de imagens | Protege o servidor, o armazenamento e os dados presentes nos arquivos. |
| **8** | R02 | Moderação e mecanismos contra falsificação de identidade | Reduz fraudes relacionadas à identidade e à reputação dos artistas. |
| **9** | R26, R27 | Expiração de reservas e tratamento de dependências externas | R26 depende da existência da funcionalidade de reserva; R27 depende da utilização de autenticação externa. |

A primeira prioridade é estabelecer a **base de autenticação e autorização**, pois outros controles dependem de saber corretamente quem é o usuário e quais recursos ele pode acessar. Em seguida, devem ser tratados os riscos críticos relacionados à disponibilidade e à exposição de informações. Os demais controles podem ser implementados progressivamente, acompanhando a implementação das respectivas funcionalidades.



### 14.6 Estimativa do risco residual

O risco residual apresentado nesta seção representa uma **estimativa esperada após a implementação e validação dos controles propostos**. Os valores não representam resultados já obtidos, pois os controles ainda não foram implementados e testados.

A estimativa considera que os controles sejam implementados corretamente e que as evidências previstas na seção 14.4 confirmem seu funcionamento.

| Risco | Nível inicial | Nível residual esperado | Condição para aceitar o residual |
|---|---|---|---|
| **R01** | Alto | Médio | Proteção de autenticação, recuperação de conta e sessões implementada e testada. |
| **R02** | Alto | Médio | Mecanismos de denúncia, moderação e preservação de evidências implementados. |
| **R03** | Médio | Baixo | Sessões com expiração, revogação e proteção contra reutilização implementadas. |
| **R04** | Médio | Baixo | Integração externa utilizar tokens adequadamente validados e escopos apropriados. |
| **R05** | Médio | Baixo | Valores de preço e prazo forem sempre validados no backend. |
| **R06** | Médio | Baixo | Alterações exigirem autorização e concordância adequadas. |
| **R07** | Alto | Médio | Avaliações estiverem vinculadas a contratações e protegidas contra alterações indevidas. |
| **R08** | Alto | Baixo | Uploads forem validados, isolados e processados com limites de recursos. |
| **R09** | Médio | Baixo | Operações concorrentes utilizarem transações ou controle de versão. |
| **R10** | Alto | Baixo | Histórico das propostas for preservado e protegido contra adulteração. |
| **R11** | Alto | Baixo | Eventos de entrega e acesso forem registrados adequadamente. |
| **R12** | Alto | Baixo | Histórico das mensagens for preservado e protegido contra alterações retroativas. |
| **R13** | Alto | Médio | Logs forem armazenados com controle de acesso e proteção contra adulteração. |
| **R14** | Médio | Baixo | Exclusão de contas preservar registros necessários para disputas e investigação. |
| **R15** | Alto | Baixo | Metadados desnecessários forem removidos das imagens públicas. |
| **R16** | Médio | Baixo | Autorização for aplicada em todas as operações sobre dados privados. |
| **R17** | Médio | Baixo | Arquivos privados exigirem autorização e links possuírem escopo adequado. |
| **R18** | Alto | Médio | Consultas e permissões do banco forem restringidas e monitoradas. |
| **R19** | Médio | Baixo | Acessos administrativos forem limitados, registrados e revisados. |
| **R20** | Alto | Baixo | Informações de atividade não essenciais deixarem de ser expostas publicamente. |
| **R21** | Crítico | Médio | Rate limiting e proteção contra automação forem testados sob carga. |
| **R22** | Crítico | Médio | Consultas de busca possuírem limites de frequência, custo e concorrência. |
| **R23** | Alto | Médio | Uploads possuírem cotas, limites de tamanho e controle de concorrência. |
| **R24** | Alto | Médio | Processamento de imagens possuir limites de memória, tempo e dimensões. |
| **R25** | Crítico | Médio | Limites de solicitações e mecanismos de detecção de abuso forem implementados. |
| **R26** | Médio | Baixo | Caso a funcionalidade exista, reservas possuírem expiração e limites; caso contrário, o risco é evitado. |
| **R27** | Médio | Baixo | Houver tratamento adequado de falhas do provedor e método alternativo quando aplicável. |
| **R28** | Alto | Baixo | Todas as funções administrativas realizarem autorização no backend. |
| **R29** | Alto | Baixo | Papéis e permissões forem controlados exclusivamente pelo servidor. |
| **R30** | Médio | Baixo | Cada operação verificar a propriedade ou autorização sobre o recurso. |
| **R31** | Médio | Baixo | Bloqueios revogarem imediatamente as sessões existentes. |
| **R32** | Alto | Baixo | Desligamentos provocarem revogação de permissões, credenciais e sessões. |
| **R33** | Médio | Baixo | O contexto da contratação for validado independentemente do papel do usuário. |
| **R34** | Alto | Médio | Contas forem pessoais e procedimentos excepcionais de titularidade forem controlados. |
| **R35** | Médio | Baixo | Toda comunicação sensível utilizar TLS adequadamente configurado. |
| **R36** | Crítico | Médio | Resultados de busca forem limitados aos dados públicos e houver proteção contra automação. |
| **R37** | Médio | Baixo | Alterações administrativas exigirem autorização, justificativa e registro de auditoria. |

A redução estimada não significa que os riscos serão eliminados. Mesmo após a implementação dos controles, podem permanecer ameaças decorrentes de falhas humanas, vulnerabilidades desconhecidas, indisponibilidade de serviços externos, novas formas de abuso ou erros de configuração. Por isso, os riscos residuais deverão ser reavaliados após a realização dos testes.


## 15. Considerações finais

A análise realizada nesta etapa transformou as ameaças e os casos de abuso identificados na Etapa 1 em **37 riscos de segurança**, mantendo a rastreabilidade entre as ameaças STRIDE, os casos de abuso e os eventos de risco. A avaliação considerou a probabilidade e o impacto de cada cenário, permitindo classificar os riscos em níveis baixo, médio, alto e crítico.

Os riscos críticos identificados foram **R21, R22, R25 e R36**. Os três primeiros estão relacionados principalmente à disponibilidade da plataforma diante de automação e sobrecarga, enquanto R36 está relacionado à exposição de informações privadas por meio da funcionalidade de busca. Esses cenários exigem atenção devido à facilidade de automação ou ao potencial de atingir informações e funcionalidades utilizadas por diversos usuários.

Entre os riscos altos, destacam-se também aqueles relacionados ao comprometimento de contas, manipulação de avaliações, integridade de arquivos e contratações, ausência de evidências, exposição de metadados, monitoramento da atividade de artistas e acesso indevido a funções administrativas. Esses riscos demonstram que a segurança do Artifício não depende de um único mecanismo, sendo necessário combinar controles de autenticação, autorização, proteção de dados, auditoria, validação de arquivos e mecanismos de disponibilidade.

A estratégia de tratamento predominante é **Reduzir**, utilizando controles técnicos e administrativos para diminuir a probabilidade ou o impacto dos eventos. A estratégia **Compartilhar** é aplicável principalmente a dependências externas, como provedores de autenticação e processamento de pagamentos. A estratégia **Evitar** pode ser utilizada quando uma funcionalidade ou condição arquitetural não for necessária ao sistema, enquanto a **Aceitação** deve ser limitada a situações de risco residual compatíveis com o objetivo da plataforma e acompanhada de revisão periódica.

As funções do **NIST CSF 2.0** foram utilizadas para organizar o tratamento ao longo do ciclo de segurança. A função **Govern** estabelece responsabilidades, políticas e limites de acesso; **Identify** permite conhecer os ativos, dados e dependências; **Protect** concentra os controles preventivos; **Detect** fornece visibilidade sobre comportamentos anômalos; **Respond** define ações de contenção; e **Recover** permite restaurar dados e serviços após incidentes.

A ordem inicial de implementação prioriza primeiro os mecanismos estruturais de **autenticação, autorização e gerenciamento de sessões**, pois esses controles são dependências para várias outras funcionalidades. Em seguida, são priorizados os mecanismos de proteção contra sobrecarga, exposição de dados, manipulação de contratações, perda de evidências e problemas relacionados a arquivos.

Por fim, os níveis de risco residual apresentados são **estimativas**, pois os controles ainda não foram implementados nem validados. A efetividade das medidas deverá ser confirmada posteriormente por meio de testes, inspeções, registros de auditoria, testes de carga e procedimentos de recuperação. Dessa forma, a avaliação poderá ser revisada conforme novos resultados forem obtidos ou conforme a arquitetura e as funcionalidades do Artifício evoluírem.

## 16. Critérios de avaliação da Etapa 2

