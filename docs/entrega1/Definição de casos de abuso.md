# Análise de Ameaças (STRIDE) — Casos de Abuso

## 1. Objetivo

Este documento apresenta os casos de abuso elaborados a partir das respostas obtidas no questionário de validação STRIDE do sistema analisado.

Os casos representam situações nas quais uma pessoa mal-intencionada, um usuário indevido ou um usuário legítimo poderia utilizar o sistema de forma inadequada para causar danos.

Cada caso contém:

- identificador;
- título;
- ator malicioso ou agente envolvido;
- objetivo do abuso;
- condições necessárias;
- sequência de ações;
- impacto esperado;
- relação com uma ou mais categorias do STRIDE.

---
Consulte os [Modelagem de ameaças STRIDE Cristiano Silveira Silva](perguntas_STRIDE_CRISTIANO_S_SILVA.md).

# 2. Casos de abuso

## 2.1 Tabela consolidada

| ID | Caso de abuso | Ator | Objetivo | Condições necessárias | Fluxo de abuso | Impacto esperado | STRIDE |
|---|---|---|---|---|---|---|---|
| **CA01** | **Criação de perfil falso de artista** | Usuário mal-intencionado | Criar perfil utilizando identidade ou trabalhos de outro artista. | Cadastro de artistas sem verificação obrigatória de identidade ou autoria. | 1. Criar conta.<br>2. Cadastrar-se como artista.<br>3. Utilizar nome ou trabalhos de terceiros.<br>4. Publicar portfólio.<br>5. Atrair clientes. | Fraude, apropriação indevida de identidade ou autoria, prejuízo ao artista legítimo e perda de confiança. | **Spoofing** |
| **CA02** | **Roubo de conta por recuperação de senha** | Atacante externo | Assumir o controle de uma conta legítima. | Falhas no token de recuperação, expiração, proteção contra tentativas ou controle de sessões. | 1. Identificar uma conta.<br>2. Solicitar recuperação.<br>3. Tentar obter ou reutilizar o token.<br>4. Redefinir a senha.<br>5. Acessar a conta. | Comprometimento da conta, acesso indevido a dados e realização de operações não autorizadas. | **Spoofing** |
| **CA03** | **Alteração indevida do preço-base** | Cliente mal-intencionado | Modificar preço ou prazo definido pelo artista. | Validação insuficiente dos valores enviados pelo cliente no backend. | 1. Selecionar comissão.<br>2. Alterar parâmetros da requisição.<br>3. Enviar valores diferentes dos cadastrados.<br>4. Tentar concluir a solicitação. | Prejuízo financeiro, contratação com condições incorretas e disputas. | **Tampering** |
| **CA04** | **Manipulação fraudulenta de avaliações** | Artista ou usuário mal-intencionado | Manipular artificialmente a reputação de um perfil. | Ausência de vínculo entre avaliação e contratação ou controle inadequado de exclusão. | 1. Criar ou utilizar conta indevida.<br>2. Inserir avaliação falsa ou tentar remover avaliação negativa.<br>3. Alterar artificialmente a reputação. | Manipulação da reputação, prejuízo aos usuários e perda de confiabilidade das avaliações. | **Tampering / Spoofing** |
| **CA05** | **Envio de arquivo malicioso no portfólio** | Usuário mal-intencionado | Utilizar o upload para enviar conteúdo não autorizado. | Validação inadequada do tipo, tamanho, formato ou local de armazenamento. | 1. Acessar upload.<br>2. Enviar arquivo não autorizado.<br>3. Tentar explorar o arquivo ou sobrescrever outro arquivo. | Comprometimento da aplicação, alteração de arquivos ou indisponibilidade. | **Tampering / Elevation of Privilege** |
| **CA06** | **Repúdio de ação em uma contratação** | Cliente ou artista mal-intencionado | Negar posteriormente uma ação realizada na plataforma. | Ausência de registros suficientes sobre usuário, data, hora e contratação. | 1. Realizar ação relevante.<br>2. Não existir registro suficiente.<br>3. Surgir disputa.<br>4. Negar a realização da ação. | Dificuldade de auditoria e resolução de disputas e possíveis prejuízos financeiros. | **Repudiation** |
| **CA07** | **Alteração do histórico de negociação** | Cliente, artista ou administrador mal-intencionado | Alterar ou apagar evidências de uma negociação. | Histórico de mensagens e propostas sem proteção adequada contra alterações. | 1. Participar de negociação.<br>2. Realizar ação comprometedora.<br>3. Alterar ou excluir registros.<br>4. Ocorrer denúncia ou disputa. | Perda de evidências e dificuldade para reconstruir os acontecimentos. | **Tampering / Repudiation** |
| **CA08** | **Exposição indevida de dados pessoais** | Atacante externo | Obter dados pessoais de clientes ou artistas. | Falha de autorização, proteção ou controle de acesso aos dados. | 1. Identificar funcionalidade vulnerável.<br>2. Acessar dados de outro usuário.<br>3. Coletar informações pessoais.<br>4. Utilizar ou divulgar os dados. | Violação de privacidade, possibilidade de fraude e consequências legais e reputacionais. | **Information Disclosure** |
| **CA09** | **Extração em massa de dados pela busca** | Robô ou atacante automatizado | Coletar grande quantidade de informações pelas buscas. | Ausência de paginação, limites de resultados ou rate limiting adequado. | 1. Automatizar requisições.<br>2. Executar buscas repetidas.<br>3. Variar filtros.<br>4. Coletar sistematicamente os resultados. | Exposição de informações, violação de privacidade e consumo excessivo de recursos. | **Information Disclosure / Denial of Service** |
| **CA10** | **Sobrecarga da plataforma por consultas automatizadas** | Atacante automatizado | Degradar ou interromper a disponibilidade do sistema. | Ausência ou insuficiência de rate limiting, limites de consulta e monitoramento. | 1. Automatizar requisições.<br>2. Enviar grande volume de buscas.<br>3. Executar consultas repetidamente.<br>4. Consumir recursos do servidor. | Lentidão, degradação ou indisponibilidade para usuários legítimos. | **Denial of Service** |
| **CA11** | **Elevação de usuário comum para administrador** | Usuário mal-intencionado | Obter privilégios administrativos sem autorização. | Falha na autorização do backend ou confiança em dados enviados pelo cliente. | 1. Criar conta comum.<br>2. Identificar parâmetros de perfil/permissão.<br>3. Tentar alterá-los.<br>4. Acessar funções administrativas. | Acesso indevido à administração, alteração de dados e comprometimento da plataforma. | **Elevation of Privilege** |
| **CA12** | **Acesso direto a endpoints administrativos** | Cliente ou artista mal-intencionado | Executar funções administrativas utilizando conta comum. | Endpoints administrativos sem validação adequada de papel e permissão no backend. | 1. Utilizar conta comum.<br>2. Identificar endpoint administrativo.<br>3. Enviar requisição diretamente.<br>4. Tentar executar a operação. | Alteração de conteúdos, bloqueio indevido de contas e comprometimento da administração. | **Elevation of Privilege / Tampering** |
| **CA13** | **Manipulação de permissões no banco de dados** | Atacante com acesso indevido ao banco | Alterar perfil de uma conta para obter privilégios administrativos. | Acesso indevido ao banco ou controles insuficientes de integridade e privilégios. | 1. Obter acesso ao banco.<br>2. Localizar conta.<br>3. Alterar perfil/permissão.<br>4. Utilizar a conta com privilégios elevados. | Comprometimento das permissões e acesso administrativo não autorizado. | **Tampering / Elevation of Privilege** |
| **CA14** | **Exploração de inconsistências em contratações** | Cliente ou artista mal-intencionado | Provocar inconsistência em propostas, contratações ou condições negociadas. | Validações, transações ou constraints insuficientes para garantir consistência. | 1. Identificar operação com múltiplos registros.<br>2. Manipular a sequência das requisições.<br>3. Interromper ou alterar a operação.<br>4. Explorar estado inconsistente. | Dados inconsistentes, disputas, prejuízo financeiro e dificuldade de recuperação. | **Tampering / Repudiation** |

---

# 3. Matriz de cobertura STRIDE

A matriz abaixo permite verificar se os casos de abuso contemplam todas as categorias do modelo STRIDE.

| Caso | Spoofing | Tampering | Repudiation | Information Disclosure | Denial of Service | Elevation of Privilege |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| CA01 | X | | | | | |
| CA02 | X | | | | | |
| CA03 | | X | | | | |
| CA04 | X | X | | | | |
| CA05 | | X | | | | X |
| CA06 | | | X | | | |
| CA07 | | X | X | | | |
| CA08 | | | | X | | |
| CA09 | | | | X | X | |
| CA10 | | | | | X | |
| CA11 | | | | | | X |
| CA12 | | X | | | | X |
| CA13 | | X | | | | X |
| CA14 | | X | X | | | |

---

# 4. Relação entre questionário e casos de abuso

| Pergunta do questionário | Caso(s) de abuso relacionado(s) |
|---|---|
| Validação da identidade dos artistas | CA01 |
| Recuperação segura de contas | CA02 |
| MFA para administradores | CA02, CA11, CA12 |
| Proteção do preço-base e prazo | CA03 |
| Proteção das avaliações | CA04 |
| Proteção dos uploads do portfólio | CA05 |
| Logs de ações relevantes | CA06 |
| Histórico de mensagens e negociações | CA07 |
| Proteção de dados pessoais | CA08 |
| Busca e filtros contra extração de dados | CA09 |
| Rate limiting e disponibilidade | CA09, CA10 |
| Controle de perfil e permissões no backend | CA11, CA13 |
| Proteção de endpoints administrativos | CA12 |
| Integridade e consistência do banco | CA13, CA14 |
