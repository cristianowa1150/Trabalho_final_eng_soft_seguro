# Enunciado do Trabalho

# Orientações gerais

As orientações desta seção são válidas para todas as etapas do trabalho. O mesmo sistema e o mesmo repositório deverão ser utilizados durante toda a disciplina, permitindo acompanhar a evolução da análise e das decisões de segurança.

## 1\. Repositório do grupo

O trabalho deverá ser desenvolvido em um repositório no GitHub criado especificamente para o grupo.

O documento principal deverá ser escrito em Markdown e poderá ser disponibilizado no arquivo `README.md` ou em outro arquivo claramente identificado, como:

```text
docs/modelagem-de-ameacas.md
```

Todos os arquivos produzidos para o trabalho deverão ser versionados no mesmo repositório, incluindo:

* documentos em Markdown;
* imagens;
* diagramas;
* arquivos-fonte dos diagramas;
* tabelas ou materiais complementares.

Não deverão ser enviados apenas links para diagramas armazenados em ferramentas externas. Os arquivos utilizados no trabalho também deverão estar disponíveis no repositório.

## 2\. Participação individual e uso de commits

Embora o trabalho seja realizado em grupo, a avaliação será individual.

Todos os integrantes deverão demonstrar sua participação por meio de commits próprios no repositório.

Cada estudante deverá contribuir efetivamente para a construção do trabalho, realizando atividades como:

* escrita ou revisão de seções;
* identificação de ameaças;
* elaboração de casos de abuso;
* criação de diagramas;
* organização do repositório;
* correção e evolução do documento.

Não será suficiente que apenas uma pessoa realize todos os commits em nome do grupo.

Commits deverão possuir mensagens que permitam compreender a contribuição realizada. Evitem mensagens genéricas como:

```text
alterações
```

```text
ajustes
```

```text
trabalho
```

Prefiram mensagens como:

```text
Adiciona ameaças de falsificação de identidade
```

```text
Descreve ativos e dados sensíveis do sistema
```

```text
Inclui caso de abuso de alteração de pagamento
```

```text
Atualiza diagrama de fluxo de dados
```

A quantidade de commits, isoladamente, não determinará a nota. Serão consideradas a relevância, a consistência e a evolução das contribuições de cada integrante.

Commits artificiais, alterações sem conteúdo relevante ou divisão proposital de uma pequena alteração em muitos commits não serão considerados como participação efetiva.

## 3\. Organização recomendada do repositório

O repositório deverá organizar claramente os sete entregáveis da disciplina. O `README.md` poderá funcionar como página inicial, contendo uma breve apresentação do projeto e links para os arquivos de cada etapa.

Uma possível estrutura é:

```text
nome-do-projeto/
├── README.md
│
├── docs/
│   ├── etapa-1-ameacas-stride.md
│   ├── etapa-2-riscos-nist.md
│   ├── etapa-3-arquitetura-segura.md
│   ├── etapa-4-codigo-seguro.md
│   └── etapa-5-verificacao-vulnerabilidades.md
│
├── diagramas/
│   ├── etapa-1/
│   │   └── visao-geral-do-sistema.png
│   └── etapa-3/
│       ├── arquitetura-segura.png
│       └── arquitetura-segura.drawio
│
├── codigo/
│   └── etapa-4/
│       ├── implementacao-ou-pseudocodigo
│       └── testes
│
├── evidencias/
│   └── etapa-5/
│       ├── capturas-de-tela/
│       └── relatorio-da-verificacao.md
│
└── roteiros/
    ├── etapa-6-deteccao-de-intrusoes.md
    └── etapa-7-devsecops-e-video-final.md
```

Os arquivos das Etapas 6 e 7 serão principalmente **roteiros ou descrições textuais**. Não será exigida a implementação de um sistema de detecção de intrusões nem de um pipeline DevSecOps completo.

A estrutura é uma recomendação. O grupo poderá utilizar o próprio `README.md` para concentrar os conteúdos ou adotar outra organização, desde que os sete entregáveis estejam claramente identificados e todos os arquivos e evidências estejam versionados no repositório.

## 4\. Orientação sobre o GitHub

Em breve será disponibilizado um vídeo explicando como criar e organizar o projeto no GitHub, incluindo orientações para estudantes que ainda não possuem experiência prévia com:

* criação de repositórios;
* clonagem do projeto;
* criação e edição de arquivos Markdown;
* commits;
* envio das alterações para o GitHub;
* colaboração entre os integrantes do grupo.

A falta de experiência anterior com Git ou GitHub não será um impedimento para a realização do trabalho. **Procure o professor sempre que necessário. A disciplina também conta com dois mestrandos estagiários que estão disponíveis para apoiá-los.** O vídeo apresentará os procedimentos necessários para iniciar o projeto.

## 5\. Entrega

A entrega será realizada por meio do endereço do repositório do grupo; você já pode entregar o repositório assim que o criar; você poderá atualizá-lo a qualquer momento; o repositório será avaliado **apenas no final da disciplina**, pois este trabalho é composto de várias etapas.

O repositório deverá estar acessível ao professor até a data definida para a entrega (recomendo disponibilizar o mais breve possível, após a atividade estar aberta para o envio). O vídeo final, sumarizando tudo o que foi feito, pode ser enviado ao final da disciplina. Alterações realizadas após o prazo poderão ser desconsideradas para fins de avaliação.

Todos os integrantes deverão verificar se seus commits estão corretamente associados às suas próprias contas do GitHub.

# 📊 Controle de andamento do grupo

> **Como utilizar:** marque cada item com `- [x]` quando estiver concluído.  
> O texto do enunciado foi preservado; os checklists abaixo são apenas um controle operacional do grupo.

| Etapa | Entregável | Status |
|---|---|:---:|
| 1 | Casos de Abuso e Modelagem de Ameaças com STRIDE | ⚪ Em andamento |
| 2 | Análise, Priorização e Tratamento de Riscos com NIST CSF | ⚪ Em andamento |
| 3 | Projeto de uma Arquitetura Segura | ⚪ Em andamento |
| 4 | Código Seguro e Testes de Segurança | ⚪ Em andamento |
| 5 | Verificação de Vulnerabilidades | ⚪ Em andamento |
| 6 | Monitoramento e Detecção de Intrusões | ⚪ Em andamento |
| 7 | DevSecOps e Vídeo Final | ⚪ Em andamento |
| — | Artigo final | ⚪ Em andamento |
| — | Revisão e entrega do repositório | ⚪ Em andamento |

---

# Etapa 1 — Casos de Abuso e Modelagem de Ameaças com STRIDE

## 📋 Checklist de execução — Etapa 1

> Marque `- [🟢]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 6. Objetivo
- [🟢] 7. Escolha do sistema
- [🟢] 8.1 Identificação do sistema
- [🟢] 8.2 Descrição do sistema
- [🟢] 8.3 Usuários, ativos e pontos de interação
- [ ] 8.4 Visão geral da arquitetura ou fluxo
- [ ] 8.5 Modelagem de ameaças com STRIDE
- [ ] 8.6 Casos de abuso
- [ ] 8.7 Considerações finais
- [ ] 9. Critérios de avaliação


## 6\. Objetivo

O objetivo desta etapa é iniciar a análise de segurança de um sistema de software antes da implementação, identificando possíveis comportamentos maliciosos, ameaças e impactos relacionados ao funcionamento da aplicação.

Cada grupo deverá escolher um sistema de software e produzir, no repositório do grupo no GitHub, um documento em Markdown contendo:

* uma breve descrição do sistema;
* a identificação dos principais usuários, recursos e informações protegidas;
* a modelagem de ameaças utilizando o STRIDE;
* a definição de casos de abuso;
* opcionalmente, diagramas que auxiliem na compreensão do sistema e das ameaças identificadas.

## 7\. Escolha do sistema

O grupo deverá escolher um sistema que possua diferentes tipos de usuários, troca de informações e operações relevantes para a segurança.

Exemplos:

* aplicativo de delivery;
* aplicativo de transporte ou corridas;
* sistema de agendamento de consultas;
* plataforma de comércio eletrônico;
* sistema bancário;
* sistema acadêmico;
* aplicativo de hospedagem;
* rede social;
* plataforma de contratação de serviços;
* sistema de compartilhamento de arquivos.

O grupo também poderá propor outro tipo de software, desde que o sistema escolhido permita a identificação de diferentes ameaças e casos de abuso.

Não será necessário implementar o software. O foco deste trabalho é compreender o funcionamento do sistema e analisar os possíveis problemas de segurança.

## 8\. Estrutura mínima do documento

O documento deverá conter, no mínimo, as seções apresentadas a seguir.

### 8.1 Identificação do sistema

Apresentar:

* nome do sistema;
* integrantes do grupo;
* endereço do repositório;
* breve justificativa para a escolha do sistema.

### 8.2 Descrição do sistema

Descrever brevemente o funcionamento do software.

A descrição deverá permitir que uma pessoa que não conhece o sistema compreenda:

* qual problema o sistema resolve;
* quem utiliza o sistema;
* quais são as principais funcionalidades;
* quais informações são armazenadas ou transmitidas;
* quais recursos precisam ser protegidos.

Não é necessário apresentar uma especificação completa de requisitos. Entretanto, a descrição deve ser suficientemente clara para sustentar a análise de segurança.

### 8.3 Usuários, ativos e pontos de interação

Identificar os principais elementos envolvidos no sistema, como:

* usuários e perfis de acesso;
* dados pessoais ou sensíveis;
* credenciais;
* pagamentos;
* avaliações;
* mensagens;
* localização;
* documentos;
* banco de dados;
* servidores;
* APIs;
* aplicativos móveis;
* serviços externos.

O grupo deverá destacar quais desses elementos são considerados ativos importantes, isto é, recursos que podem causar prejuízos caso sejam acessados, alterados, destruídos ou indisponibilizados indevidamente.

### 8.4 Visão geral da arquitetura ou fluxo

Apresentar uma visão simplificada de como os usuários e componentes interagem.

Essa visão poderá ser apresentada por meio de texto, tabela ou diagrama.

Preferencialmente, o grupo poderá elaborar **um ou mais** diagramas, como:

* diagrama de contexto;
* diagrama de fluxo de dados;
* diagrama simplificado de componentes;
* diagrama de casos de uso;
* representação dos usuários, serviços e bancos de dados.

Os diagramas deverão estar legíveis e ser versionados no repositório do grupo. Caso você não conheça esses diagramas, não se preocupe; são apenas sugestões. Pode usar o formato textual apenas se preferir.

### 8.5 Modelagem de ameaças com STRIDE

O grupo deverá aplicar o STRIDE ao sistema escolhido.

Para cada categoria, deverão ser identificadas ameaças que façam sentido no contexto do sistema:

* **Spoofing:** falsificação de identidade;
* **Tampering:** alteração indevida de dados;
* **Repudiation:** possibilidade de negar uma ação realizada;
* **Information Disclosure:** exposição indevida de informações;
* **Denial of Service:** indisponibilidade ou degradação do serviço;
* **Elevation of Privilege:** obtenção indevida de permissões.

A análise deverá apresentar ameaças concretas e relacionadas ao funcionamento do software escolhido. Não basta apenas definir cada categoria do STRIDE.

Sugere-se organizar a análise em uma tabela semelhante à seguinte:

|ID|Categoria STRIDE|Componente ou ativo|Ameaça identificada|Possível impacto|
|-|-|-|-|-|
|T01|Spoofing|Conta do usuário|Um atacante utiliza credenciais roubadas para acessar a conta de outra pessoa|Acesso a informações privadas e realização de operações fraudulentas|
|T02|Tampering|Pedido|Um usuário altera o valor de um pedido antes do pagamento|Prejuízo financeiro|
|T03|Information Disclosure|Banco de dados|Informações pessoais são expostas por uma falha de autorização|Violação de privacidade|

O número de ameaças dependerá da complexidade do sistema. Entretanto, espera-se que o grupo analise todas as categorias do STRIDE e justifique quando alguma delas não for aplicável.

### 8.6 Casos de abuso

Os casos de abuso deverão representar formas pelas quais uma pessoa mal-intencionada, um usuário indevido ou até mesmo um usuário legítimo poderia utilizar o sistema para causar danos.

Cada caso de abuso deverá conter:

* identificador;
* título;
* ator malicioso ou agente envolvido;
* objetivo do abuso;
* condições necessárias;
* sequência de ações;
* impacto esperado;
* relação com uma ou mais categorias do STRIDE.

Exemplo:

#### CA01 — Cadastro de falso profissional

**Ator:** usuário mal-intencionado.

**Objetivo:** obter acesso a informações privadas de clientes.

**Condições:** o sistema permite o cadastro de profissionais sem verificar adequadamente sua identidade ou habilitação.

**Fluxo de abuso:**

1. O atacante cria uma conta como profissional.
2. O sistema aceita o cadastro sem validação suficiente.
3. O atacante recebe solicitações contendo informações privadas.
4. O atacante acessa ou armazena os dados das vítimas.

**Impacto:** exposição de informações pessoais ou sensíveis, fraude e perda de confiança no sistema.

**Categorias STRIDE relacionadas:** Spoofing, Information Disclosure e Elevation of Privilege.

Os casos de abuso também poderão ser representados por diagramas, desde que sejam acompanhados por uma explicação textual.

### 8.7 Considerações finais

Apresentar uma síntese contendo:

* as ameaças consideradas mais preocupantes;
* os ativos mais importantes do sistema;
* os tipos de abuso que poderiam causar maior impacto;
* as principais dificuldades encontradas pelo grupo durante a análise.

Não é obrigatório, neste primeiro trabalho, propor uma solução completa para todas as ameaças. Entretanto, o grupo poderá indicar possíveis medidas de proteção quando considerar pertinente.

## 9\. Critérios de avaliação

Serão considerados:

* clareza e qualidade da descrição do sistema;
* identificação adequada de usuários, ativos e componentes;
* aplicação correta e contextualizada do STRIDE;
* qualidade e coerência das ameaças identificadas;
* qualidade dos casos de abuso;
* relação entre os casos de abuso e as ameaças;
* organização e legibilidade do documento em Markdown;
* qualidade dos diagramas, quando utilizados;
* organização do repositório;
* histórico de evolução do trabalho;
* participação individual demonstrada por meio dos commits.

# Etapa 2 — Análise, Priorização e Tratamento de Riscos com o NIST CSF

## 📋 Checklist de execução — Etapa 2

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 10. Objetivo
- [ ] 11. Continuidade do projeto
- [ ] 12. Estrutura mínima da Etapa 2
- [ ] 13.1 Critérios de probabilidade
- [ ] 13.2 Critérios de impacto
- [ ] 13.3 Cálculo e classificação
- [ ] 13.4 Registro de riscos
- [ ] 13.5 Justificativas
- [ ] 13.6 Priorização
- [ ] 14.1 Estratégias de tratamento
- [ ] 14.2 Funções do NIST CSF 2.0
- [ ] 14.3 Mapeamento dos riscos para o NIST CSF
- [ ] 14.4 Plano de tratamento
- [ ] 14.5 Ordem inicial de implementação
- [ ] 14.6 Estimativa do risco residual
- [ ] 15. Considerações finais
- [ ] 16. Critérios de avaliação da Etapa 2


## 10\. Objetivo

O objetivo desta etapa é continuar a análise iniciada na Etapa 1, transformando as ameaças e os casos de abuso identificados em riscos que possam ser avaliados, comparados, priorizados e tratados.

O grupo deverá:

* definir critérios de probabilidade e impacto;
* transformar as ameaças da Etapa 1 em eventos de risco;
* calcular e justificar o nível de cada risco;
* definir uma ordem de prioridade;
* escolher estratégias de tratamento;
* relacionar os riscos às funções do NIST Cybersecurity Framework 2.0;
* propor controles concretos;
* indicar responsáveis e formas de verificação;
* estimar o risco residual esperado.

Nesta etapa, não será necessário implementar os controles. O objetivo é elaborar um plano de tratamento coerente com o sistema analisado e com os riscos anteriormente identificados.

## 11\. Continuidade do projeto

O grupo deverá utilizar:

* o mesmo sistema escolhido na Etapa 1;
* o mesmo repositório do GitHub;
* as mesmas ameaças STRIDE;
* os mesmos casos de abuso;
* os ativos, usuários e componentes já descritos.

A Etapa 2 deverá ser adicionada ao documento existente. O conteúdo da Etapa 1 não deverá ser substituído ou apagado.

Caso o grupo perceba algum problema na análise anterior, poderá corrigi-lo por meio de novos commits. A alteração deverá ser explicada e deverá manter a coerência entre ameaças, casos de abuso, riscos e controles.

## 12\. Estrutura mínima da Etapa 2

O documento deverá conter, no mínimo:

1. critérios de probabilidade;
2. critérios de impacto;
3. cálculo e classificação dos riscos;
4. registro de riscos;
5. justificativa das avaliações;
6. priorização dos riscos;
7. estratégias de tratamento;
8. apresentação das funções do NIST CSF 2.0;
9. mapeamento dos riscos para as funções do NIST;
10. plano de tratamento;
11. ordem inicial de implementação;
12. estimativa do risco residual;
13. considerações finais.

## 13\. Análise e priorização dos riscos

### 13.1 Critérios de probabilidade

O grupo deverá utilizar a seguinte escala:

|Valor|Classificação|Critério|
|-|-|-|
|1|Baixa|O evento depende de condições incomuns, acesso muito específico ou grande capacidade técnica|
|2|Média-baixa|O evento é possível, mas depende de uma vulnerabilidade ou condição específica|
|3|Média-alta|O evento é plausível e pode ocorrer em situações comuns de uso ou ataque|
|4|Alta|O evento pode ocorrer com facilidade, frequência ou durante condições previsíveis do sistema|

A probabilidade não deverá ser escolhida apenas por intuição. Cada valor deverá ser justificado com base nas características do sistema, nos usuários, nas vulnerabilidades, nas condições de exploração e no contexto de uso.

### 13.2 Critérios de impacto

O grupo deverá utilizar a seguinte escala:

|Valor|Classificação|Critério|
|-|-|-|
|1|Baixo|Causa pequeno transtorno e pode ser corrigido rapidamente|
|2|Moderado|Causa interrupção ou inconsistência limitada, com possibilidade de recuperação|
|3|Alto|Causa prejuízo relevante aos usuários, ao negócio, à administração ou à privacidade|
|4|Muito alto|Pode afetar muitos usuários, comprometer operações críticas ou causar prejuízo grave|

Na avaliação do impacto, o grupo poderá considerar, conforme o sistema escolhido:

* prejuízos aos usuários;
* exposição de dados;
* perdas financeiras;
* interrupção do serviço;
* comprometimento de operações importantes;
* danos jurídicos ou regulatórios;
* danos à reputação;
* dificuldade de recuperação;
* quantidade de pessoas afetadas.

### 13.3 Cálculo e classificação

A pontuação deverá ser calculada da seguinte forma:

`Pontuação = Probabilidade × Impacto`

O resultado deverá ser classificado conforme a tabela:

|Pontuação|Nível do risco|
|-|-|
|1 a 3|Baixo|
|4 a 7|Médio|
|8 a 11|Alto|
|12 a 16|Crítico|

A pontuação auxilia na comparação dos riscos, mas não substitui a análise do contexto. Dois riscos com a mesma pontuação podem receber prioridades diferentes quando suas consequências, dependências ou possibilidades de recuperação forem distintas.

### 13.4 Registro de riscos

Cada ameaça relevante da Etapa 1 deverá originar pelo menos um risco. Quando uma ameaça puder causar consequências diferentes, o grupo poderá criar mais de um risco relacionado a ela.

O registro deverá conter:

|Campo|Descrição|
|-|-|
|ID|Identificador único, como R01, R02 e R03|
|Origem STRIDE|Categoria ou ameaça da Etapa 1 relacionada ao risco|
|Evento de risco|Situação que poderá ocorrer e causar prejuízo|
|Vulnerabilidade ou condição|Fraqueza ou circunstância que permite o evento|
|Probabilidade|Valor de 1 a 4|
|Impacto|Valor de 1 a 4|
|Pontuação|Resultado da multiplicação|
|Nível|Baixo, médio, alto ou crítico|

Sugere-se utilizar uma tabela semelhante à seguinte:

|ID|Origem STRIDE|Evento de risco|Vulnerabilidade ou condição|Probabilidade|Impacto|Pontuação|Nível|
|-|-|-|-|-:|-:|-:|-|
|R01|Spoofing|Um atacante acessa a conta de um usuário e realiza operações em seu nome|Credenciais comprometidas e ausência de verificação adicional|3|4|12|Crítico|

O exemplo serve apenas para demonstrar a estrutura. Os riscos deverão ser elaborados especificamente para o sistema escolhido pelo grupo.

### 13.5 Justificativas

Para cada risco, o grupo deverá explicar:

* por que a probabilidade recebeu aquele valor;
* por que o impacto recebeu aquele valor;
* quais usuários, dados, funcionalidades ou componentes podem ser afetados;
* quais consequências podem ocorrer;
* por que o nível calculado representa adequadamente o contexto analisado.

Não será suficiente apresentar apenas os números.

### 13.6 Priorização

Depois da classificação, o grupo deverá apresentar uma ordem inicial de prioridade.

A priorização deverá considerar:

* a pontuação;
* a gravidade das consequências;
* a quantidade de usuários afetados;
* a importância do ativo;
* a possibilidade de recuperação;
* as dependências entre os riscos;
* a urgência do tratamento.

O grupo deverá explicar por que um risco deve ser tratado antes dos demais.

## 14\. Tratamento dos riscos

### 14.1 Estratégias de tratamento

Para cada risco, o grupo deverá escolher uma estratégia principal:

|Estratégia|Descrição|
|-|-|
|Evitar|Eliminar a atividade ou condição que dá origem ao risco|
|Reduzir|Implementar medidas para diminuir sua probabilidade ou seu impacto|
|Compartilhar|Atribuir parte da operação ou das consequências a um terceiro|
|Aceitar|Reconhecer e manter conscientemente o risco, com justificativa e acompanhamento|

A escolha deverá ser justificada.

Aceitar um risco não significa ignorá-lo. A aceitação deverá indicar:

* o motivo da decisão;
* quem deverá aprová-la;
* em quais condições o risco será aceito;
* quando a decisão deverá ser revisada.

### 14.2 Funções do NIST CSF 2.0

O grupo deverá utilizar as seis funções do NIST Cybersecurity Framework 2.0 para organizar os resultados de segurança esperados:

|Função|Finalidade|
|-|-|
|Govern|Definir políticas, responsabilidades, prioridades e critérios de decisão|
|Identify|Conhecer ativos, dependências, vulnerabilidades e riscos|
|Protect|Implementar salvaguardas para reduzir a probabilidade ou o impacto|
|Detect|Identificar eventos suspeitos, falhas e possíveis incidentes|
|Respond|Conter, analisar, comunicar e tratar incidentes|
|Recover|Restaurar serviços e dados e reduzir os prejuízos causados|

As funções do NIST não são controles específicos.

Por exemplo:

* **Protect** é uma função;
* proteger o acesso às contas é um resultado esperado;
* autenticação multifator é um possível controle.

Não será suficiente escrever apenas “aplicar o NIST” ou listar as funções sem relacioná-las aos riscos do sistema.

### 14.3 Mapeamento dos riscos para o NIST CSF

O grupo deverá indicar quais funções são relevantes para cada risco.

Sugere-se utilizar uma tabela como:

|Risco|Govern|Identify|Protect|Detect|Respond|Recover|
|-|:-:|:-:|:-:|:-:|:-:|:-:|
|R01|X|X|X|X|X|X|

Um risco poderá estar relacionado a várias funções. Entretanto, o grupo deverá analisar cada relação, evitando marcar todas as funções automaticamente.

### 14.4 Plano de tratamento

Para cada risco, o grupo deverá elaborar um plano contendo:

|Campo|Descrição|
|-|-|
|Risco|Identificador e título|
|Estratégia|Evitar, reduzir, compartilhar ou aceitar|
|Controles propostos|Medidas concretas para tratar o risco|
|Funções do NIST|Funções relacionadas aos controles|
|Responsáveis|Pessoas, equipes ou setores responsáveis|
|Evidências e verificação|Formas de confirmar que os controles existem e funcionam|

Sugere-se utilizar uma tabela como:

|Risco|Estratégia|Controles propostos|Funções relacionadas|Responsáveis|Evidências e verificação|
|-|-|-|-|-|-|
|R01|Reduzir|Autenticação multifator; confirmação de operações sensíveis; notificações|Protect, Detect, Respond e Recover|Desenvolvimento e infraestrutura|Testes de autenticação; logs; simulação de conta comprometida|

Os controles deverão ser específicos e observáveis.

Evitem propostas genéricas como:

* aumentar a segurança;
* usar criptografia;
* melhorar a autenticação;
* utilizar o NIST;
* monitorar o sistema.

Quando uma dessas ideias for utilizada, o grupo deverá explicar:

* onde será aplicada;
* qual problema pretende reduzir;
* como deverá funcionar;
* quem será responsável;
* como será verificada.

### 14.5 Ordem inicial de implementação

O grupo deverá definir uma ordem inicial para a implementação dos controles.

A ordem poderá considerar:

* riscos críticos e altos;
* dependências técnicas;
* controles que reduzem vários riscos;
* custo e complexidade;
* recursos disponíveis;
* necessidade de políticas ou decisões anteriores;
* urgência.

A ordem deverá ser justificada e poderá ser revisada nas próximas etapas.

### 14.6 Estimativa do risco residual

O grupo deverá estimar o nível esperado de cada risco após a implementação dos controles.

A tabela deverá conter:

|Risco|Nível inicial|Nível residual esperado|Condição para aceitar o residual|
|-|-|-|-|

O risco residual deverá ser apresentado como uma estimativa.

O grupo não poderá afirmar que o risco já foi reduzido apenas porque um controle foi proposto. A redução somente poderá ser confirmada após implementação, testes e obtenção de evidências.

## 15\. Considerações finais

A conclusão deverá apresentar:

* os riscos considerados mais importantes;
* as razões que determinaram a priorização;
* as estratégias de tratamento predominantes;
* as funções do NIST mais relevantes para o sistema;
* os controles considerados essenciais;
* as principais dificuldades encontradas;
* as limitações da avaliação;
* os pontos que precisarão ser detalhados nas próximas etapas.

## 16\. Critérios de avaliação da Etapa 2

Serão considerados:

* continuidade e coerência com a Etapa 1;
* definição clara dos critérios de probabilidade e impacto;
* correção dos cálculos;
* qualidade das justificativas;
* coerência da priorização;
* diferenciação correta entre ameaça, vulnerabilidade, ataque e risco;
* adequação das estratégias de tratamento;
* aplicação contextualizada das funções do NIST CSF;
* diferenciação entre função, resultado esperado e controle;
* qualidade e especificidade dos controles propostos;
* definição de responsáveis;
* qualidade das evidências e formas de verificação;
* coerência da ordem de implementação;
* realismo da estimativa do risco residual;
* organização e legibilidade do documento;
* evolução do trabalho demonstrada por commits;
* participação individual dos integrantes.

# Orientação para os sete dias finais

As Etapas 3 a 7 foram reduzidas ao mínimo necessário para que o trabalho seja viável no período restante.

O grupo deverá continuar utilizando o mesmo sistema e o mesmo repositório. Não será necessário implementar um software completo.

Os estudantes poderão seguir uma das duas possibilidades:

* **Realização prática:** para grupos que já possuem um sistema implementado ou conseguem produzir pequenos trechos de código;
* **Realização descritiva:** para estudantes de semestres iniciais, que poderão apresentar pseudocódigo, exemplos, configurações, diagramas e descrições detalhadas de como realizariam a implementação.

As duas possibilidades serão aceitas. A avaliação considerará principalmente a coerência com os riscos identificados, a qualidade das decisões, as justificativas, as evidências apresentadas e a participação individual.

# Etapa 3 — Projeto de uma Arquitetura Segura

## 📋 Checklist de execução — Etapa 3

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 17. Objetivo
- [ ] 18. Entregável mínimo
- [ ] 18.1 Três requisitos de segurança derivados dos riscos prioritários
- [ ] 18.2 Mapeamento de três vulnerabilidades catalogadas
- [ ] 18.3 Diagrama da arquitetura segura
- [ ] 18.4 Três decisões de arquitetura
- [ ] 19. Critérios de avaliação da Etapa 3


## 17\. Objetivo

O objetivo desta etapa é transformar os riscos e controles definidos anteriormente em requisitos de segurança e decisões de arquitetura.

O grupo deverá mostrar como o sistema seria organizado para reduzir os riscos prioritários.

## 18\. Entregável mínimo

A Etapa 3 deverá conter apenas os seguintes elementos:

1. três requisitos de segurança derivados dos riscos prioritários;
2. o mapeamento de três vulnerabilidades catalogadas;
3. um diagrama simples da arquitetura segura;
4. três decisões de arquitetura justificadas.

### 18.1 Requisitos de segurança

O grupo deverá selecionar três riscos críticos ou altos e derivar um requisito de segurança para cada um.

Os requisitos deverão ser específicos e verificáveis.

Sugere-se utilizar a tabela:

|ID|Risco de origem|Requisito de segurança|Critério de verificação|
|-|-|-|-|
|RS01|R01|O sistema deverá solicitar uma nova autenticação antes de confirmar uma operação sensível|A operação deverá ser recusada quando a nova autenticação não for realizada|

Evitem requisitos genéricos como “o sistema deverá ser seguro”.

### 18.2 Vulnerabilidades catalogadas

Para cada requisito, o grupo deverá pesquisar uma vulnerabilidade relacionada em catálogos ou referências reconhecidas, como:

* CWE;
* OWASP Top 10;
* OWASP ASVS;
* OWASP Cheat Sheet Series.

O mapeamento poderá ser apresentado assim:

|Risco|Vulnerabilidade ou categoria|Referência utilizada|Relação com o sistema|
|-|-|-|-|
|R01|Falha de autenticação ou gerenciamento de sessão|CWE ou OWASP|Pode permitir que um atacante utilize a conta de outra pessoa|

Não é necessário apresentar uma lista extensa. Três mapeamentos são suficientes.

### 18.3 Diagrama da arquitetura segura

O grupo deverá criar um diagrama simples mostrando:

* usuários;
* interface ou aplicação;
* serviço de autenticação;
* regras de autorização;
* banco de dados;
* logs ou monitoramento;
* serviços externos relevantes;
* posição dos principais controles.

O diagrama poderá ser feito no Mermaid, Draw.io, Canva ou outra ferramenta. O arquivo-fonte e a imagem deverão ser versionados no repositório.

### 18.4 Decisões de arquitetura

O grupo deverá registrar três decisões.

Cada decisão deverá conter:

* problema ou risco tratado;
* decisão tomada;
* motivo;
* componente afetado;
* resultado esperado.

Exemplo:

|Decisão|Risco tratado|Justificativa|
|-|-|-|
|Validar permissões no servidor em todas as operações administrativas|R06|Ocultar opções na interface não impede o acesso direto às funções|

## 19\. Critérios de avaliação da Etapa 3

Serão considerados:

* relação entre requisitos, riscos e controles;
* uso adequado das referências de vulnerabilidades;
* clareza do diagrama;
* qualidade das decisões;
* viabilidade da arquitetura proposta;
* participação individual demonstrada pelos commits.

# Etapa 4 — Código Seguro e Testes de Segurança

## 📋 Checklist de execução — Etapa 4

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 20. Objetivo
- [ ] 21. Entregável mínimo
- [ ] Selecionar duas práticas de código seguro
- [ ] Relacionar cada prática aos riscos e requisitos anteriores
- [ ] Definir dois testes de segurança para cada prática
- [ ] Elaborar implementação, pseudocódigo ou descrição detalhada
- [ ] Definir o resultado esperado
- [ ] Registrar a referência da OWASP utilizada
- [ ] 21.1 Testes antes da implementação
- [ ] 21.2 Forma de realização
- [ ] 22. Critérios de avaliação da Etapa 4


## 20\. Objetivo

O objetivo desta etapa é demonstrar como as decisões da arquitetura seriam transformadas em práticas de implementação segura.

O uso da OWASP Cheat Sheet Series é recomendado como referência prática. O OWASP ASVS também poderá ser utilizado para selecionar requisitos verificáveis.

Não será necessário implementar o sistema completo.

## 21\. Entregável mínimo

O grupo deverá selecionar apenas **duas práticas de código seguro** relacionadas aos riscos e requisitos anteriores.

Exemplos:

* validação de entrada;
* consultas parametrizadas;
* controle de autorização;
* armazenamento seguro de senhas;
* proteção de sessões;
* tratamento seguro de erros;
* proteção de segredos;
* geração de logs sem exposição de dados sensíveis.

Para cada prática, o grupo deverá apresentar:

1. risco e requisito relacionados;
2. dois testes de segurança definidos antes da implementação;
3. implementação, pseudocódigo ou descrição detalhada;
4. resultado esperado;
5. referência da OWASP utilizada.

### 21.1 Testes antes da implementação

Os testes deverão ser escritos antes do exemplo de implementação.

Para cada prática, descrevam pelo menos:

* um caso de uso válido;
* um caso malicioso, inválido ou não autorizado;
* o resultado seguro esperado.

Exemplo:

|Teste|Entrada ou ação|Resultado esperado|
|-|-|-|
|TS01|Estudante tenta acessar a função administrativa|A solicitação é recusada e o evento é registrado|
|TS02|Administrador autorizado acessa a função|A solicitação é permitida|

### 21.2 Forma de realização

Grupos com experiência em programação poderão incluir código e testes executáveis.

Estudantes de semestres iniciais poderão entregar:

* pseudocódigo;
* trechos ilustrativos;
* fluxos;
* configurações comentadas;
* descrição passo a passo de como implementariam a prática.

## 22\. Critérios de avaliação da Etapa 4

Serão considerados:

* coerência com a arquitetura;
* escolha adequada das práticas;
* definição dos testes antes da solução;
* qualidade da implementação ou descrição;
* uso das referências da OWASP;
* clareza dos resultados esperados;
* participação individual nos commits.

# Etapa 5 — Verificação de Vulnerabilidades

## 📋 Checklist de execução — Etapa 5

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 23. Objetivo
- [ ] 24. Ambiente recomendado
- [ ] 25. Entregável mínimo
- [ ] Definir sistema ou ambiente testado
- [ ] Definir ferramenta utilizada
- [ ] Registrar configuração básica do teste
- [ ] Registrar evidência da execução
- [ ] Analisar até três alertas ou achados
- [ ] Propor correção para cada achado
- [ ] Armazenar capturas em `evidencias/etapa-5/`
- [ ] Armazenar relatório em `evidencias/etapa-5/`
- [ ] 26. Critérios de avaliação da Etapa 5


## 23\. Objetivo

O objetivo desta etapa é utilizar uma ferramenta de teste de segurança para observar vulnerabilidades, alertas e configurações inseguras.

O grupo deverá testar somente:

* o próprio sistema;
* um sistema cuja análise tenha sido expressamente autorizada; ou
* uma aplicação deliberadamente vulnerável executada para fins educacionais.

É proibido testar sistemas de terceiros sem autorização.

## 24\. Ambiente recomendado

Os grupos que não possuem um sistema web implementado poderão utilizar o **OWASP Juice Shop**, uma aplicação criada para treinamento em segurança.

A ferramenta sugerida é o **ZAP**, que permite observar o tráfego e executar verificações de segurança em aplicações web.

O professor poderá disponibilizar um tutorial específico para a instalação e o uso básico dessas ferramentas.

## 25\. Entregável mínimo

O grupo deverá realizar uma única sessão de verificação e apresentar:

1. sistema ou ambiente testado;
2. ferramenta utilizada;
3. configuração básica do teste;
4. evidência da execução;
5. análise de três alertas ou achados;
6. proposta de correção para cada achado.

Sugere-se utilizar a tabela:

|ID|Alerta ou achado|Evidência|Possível impacto|Relação com OWASP ou CWE|Correção proposta|
|-|-|-|-|-|-|
|A01|Descrição do alerta|Captura ou trecho do relatório|Consequência possível|Categoria relacionada|Medida sugerida|

Não é necessário explorar completamente as vulnerabilidades nem obter acesso indevido. O objetivo é interpretar os resultados da ferramenta.

Caso a ferramenta apresente menos de três alertas relevantes, o grupo poderá analisar os alertas encontrados e explicar por que outros resultados foram descartados como informativos, duplicados ou possíveis falsos positivos.

As capturas de tela e o relatório deverão ser armazenados em `evidencias/etapa-5/`.

## 26\. Critérios de avaliação da Etapa 5

Serão considerados:

* uso de um ambiente autorizado;
* evidências da execução;
* interpretação dos alertas;
* relação com riscos e vulnerabilidades estudados;
* qualidade das correções propostas;
* capacidade de reconhecer limitações e possíveis falsos positivos;
* participação individual.

# Etapa 6 — Monitoramento e Detecção de Intrusões

## 📋 Checklist de execução — Etapa 6

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 27. Objetivo
- [ ] Criar `roteiros/etapa-6-deteccao-de-intrusoes.md`
- [ ] Explicar o que é detecção de intrusões
- [ ] Explicar a diferença entre prevenir e detectar
- [ ] Identificar quais eventos do sistema deveriam ser registrados
- [ ] Criar três regras simples de detecção
- [ ] Definir risco observado para cada regra
- [ ] Definir fonte de dados para cada regra
- [ ] Definir condição de alerta para cada regra
- [ ] Definir resposta inicial para cada regra
- [ ] 29. Critérios de avaliação da Etapa 6


## 27\. Objetivo

O objetivo desta etapa é compreender como um sistema pode identificar comportamentos suspeitos depois que entra em operação.

Não será necessário instalar ou implementar um sistema de detecção de intrusões.

O entregável será um **roteiro ou uma descrição textual** armazenado em:

```text
roteiros/etapa-6-deteccao-de-intrusoes.md
```

## 28\. Entregável mínimo

O roteiro deverá explicar brevemente:

1. o que é detecção de intrusões;
2. a diferença entre prevenir e detectar;
3. quais eventos do sistema deveriam ser registrados;
4. três regras simples de detecção;
5. o que deveria acontecer depois de um alerta.

Cada regra deverá conter:

|Campo|Descrição|
|-|-|
|Risco observado|Risco ou caso de abuso relacionado|
|Fonte de dados|Log, acesso, erro, requisição ou evento utilizado|
|Condição de alerta|Comportamento que será considerado suspeito|
|Resposta inicial|Ação recomendada após a detecção|

Exemplo:

|Risco observado|Fonte de dados|Condição de alerta|Resposta inicial|
|-|-|-|-|
|Uso indevido de conta|Logs de autenticação|Muitas tentativas malsucedidas seguidas para a mesma conta|Alertar a equipe e limitar temporariamente novas tentativas|

Três regras são suficientes.

## 29\. Critérios de avaliação da Etapa 6

Serão considerados:

* compreensão do conceito;
* relação com os riscos do projeto;
* escolha adequada dos eventos;
* clareza das regras;
* coerência das respostas;
* qualidade do roteiro;
* participação individual nos commits.

# Etapa 7 — DevSecOps e Vídeo Final

## 📋 Checklist de execução — Etapa 7

> Marque `- [x]` conforme cada atividade for concluída. O enunciado original desta etapa permanece abaixo.

- [ ] 30. Objetivo
- [ ] Criar descrição textual ou diagrama de um pipeline DevSecOps
- [ ] Criar roteiro do vídeo/apresentação final
- [ ] Incluir planejamento e análise de ameaças
- [ ] Incluir requisitos e decisões de arquitetura
- [ ] Incluir implementação segura
- [ ] Incluir testes automatizados
- [ ] Incluir análise de código e dependências
- [ ] Incluir teste dinâmico ou pentest
- [ ] Incluir implantação
- [ ] Incluir monitoramento e resposta
- [ ] Definir pelo menos três condições que impediriam a continuidade do pipeline
- [ ] Criar `roteiros/etapa-7-devsecops-e-video-final.md`
- [ ] Preparar apresentação final de 10 a 15 minutos
- [ ] 33. Critérios de avaliação da Etapa 7


## 30\. Objetivo

O objetivo desta etapa é integrar tudo o que foi produzido ao longo da disciplina e demonstrar como a segurança pode acompanhar continuamente o ciclo de desenvolvimento.

Não será necessário implementar um pipeline real.

O grupo deverá elaborar:

* uma descrição textual ou diagrama de um pipeline DevSecOps;
* o roteiro do vídeo final;
* o vídeo final apresentando a evolução do projeto.

## 31\. Pipeline DevSecOps proposto

O arquivo deverá ser armazenado em:

```text
roteiros/etapa-7-devsecops-e-video-final.md
```

O pipeline deverá incluir, de maneira simples:

1. planejamento e análise de ameaças;
2. requisitos e decisões de arquitetura;
3. implementação segura;
4. testes automatizados;
5. análise de código e dependências;
6. teste dinâmico ou pentest;
7. implantação;
8. monitoramento e resposta.

Sugere-se utilizar uma tabela:

|Momento|Atividade de segurança|Evidência produzida|Condição para continuar|
|-|-|-|-|
|Planejamento|STRIDE e análise de riscos|Tabela de ameaças e riscos|Riscos prioritários identificados|
|Código|Práticas seguras e testes|Código, pseudocódigo ou testes|Testes aprovados|
|Verificação|ZAP ou ferramenta equivalente|Relatório de alertas|Achados críticos analisados|
|Operação|Logs e regras de detecção|Alertas e registros|Incidentes tratados|

O grupo deverá indicar pelo menos três condições que impediriam a continuidade do pipeline, por exemplo:

* teste de segurança reprovado;
* vulnerabilidade crítica não analisada;
* segredo encontrado no repositório;
* dependência conhecida como vulnerável;
* falha no controle de acesso.

## 32\. Apresentação Final

O a apresentação deverá ter, preferencialmente, entre **10 e 15 minutos**.  O formato é obrigatoriamente presencial, salvo situações justificadas à coordenação do PPGCO; opcionalmente, pode-se também entregar um vídeo de treinamento/documentação (que não substituirá a apresentação presencial).

O grupo deverá apresentar:

1. o sistema escolhido;
2. as principais ameaças e casos de abuso;
3. os riscos prioritários;
4. as decisões de arquitetura;
5. as práticas de código seguro;
6. os principais resultados da verificação;
7. as regras de detecção;
8. o pipeline DevSecOps proposto;
9. o que o grupo aprendeu.

O roteiro da apresentação (e vídeo, caso seja gravado) deverá ser versionado no repositório.

Não é necessário demonstrar todas as tabelas. O vídeo deverá destacar as principais decisões e mostrar a evolução do software ao longo da disciplina.

Todos os integrantes deverão participar do trabalho. A participação na apresentação poderá ser dividida conforme a organização do grupo, mas a avaliação individual também considerará os commits realizados nas diferentes etapas.

## 33\. Critérios de avaliação da Etapa 7

Serão considerados:

* integração entre as etapas;
* compreensão de DevSecOps;
* coerência do pipeline;
* qualidade das condições de segurança;
* clareza e objetividade da apresentação;
* capacidade de apresentar decisões e aprendizados;
* qualidade do roteiro;
* participação individual.

# 🟢 Checklist operacional de entrega

> Este checklist complementa o checklist original do enunciado, que permanece abaixo.

## 📄 Entregáveis

- [ ] Artigo (template SBC ou IEEE)
- [ ] Etapa 1 concluída
- [ ] Etapa 2 concluída
- [ ] Etapa 3 concluída
- [ ] Etapa 4 concluída
- [ ] Etapa 5 concluída
- [ ] Etapa 6 concluída
- [ ] Etapa 7 concluída
- [ ] Roteiros revisados
- [ ] Diagramas revisados
- [ ] Evidências organizadas
- [ ] Apresentação final preparada
- [ ] Vídeo final, caso seja produzido

## 🔀 GitHub

- [ ] Commits próprios de todos os integrantes
- [ ] Arquivos versionados no GitHub
- [ ] Diagramas versionados no GitHub
- [ ] Evidências versionadas no GitHub
- [ ] Roteiros versionados no GitHub
- [ ] Commits associados corretamente às contas dos integrantes
- [ ] Links internos testados
- [ ] Imagens testadas
- [ ] Organização das pastas revisada
- [ ] `README.md` revisado
- [ ] Repositório acessível ao professor

## 🟩 Status

> Para marcar um item como concluído, altere `- [ ]` para `- [x]`.
>
> Exemplo: `- [x] Etapa 1 concluída`

---

# Checklist final simplificado

Antes da entrega, o grupo deverá verificar se o repositório contém:

* \[ ] Artigo (template SBC ou IEEE) relatando o trabalho desenvolvido, incluindo as etapas abaixo;
* \[ ] Etapa 1 — ameaças STRIDE e casos de abuso;
* \[ ] Etapa 2 — análise, priorização e tratamento dos riscos;
* \[ ] Etapa 3 — três requisitos, três vulnerabilidades, um diagrama e três decisões;
* \[ ] Etapa 4 — duas práticas de código seguro com testes;
* \[ ] Etapa 5 — uma verificação com até três achados analisados;
* \[ ] Etapa 6 — roteiro com três regras de detecção;
* \[ ] Etapa 7 — pipeline, roteiro e apresentação final; a gravação extra de um vídeo pode ser considerado um "plus";
* \[ ] commits próprios de todos os integrantes;
* \[ ] arquivos, diagramas e evidências versionados no GitHub.
