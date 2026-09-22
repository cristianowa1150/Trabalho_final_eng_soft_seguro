# Análise de Ameaças (STRIDE) - Eduardo Nascimento de Souza Rolim

## Perguntas para Validação com o Cliente

### 1. Spoofing (Falsificação de Identidade)

* O que acontece se alguém tentar criar uma conta utilizando o nome público ou artístico de um criador já conhecido fora da plataforma?

> **Resposta:** A plataforma não deve impedir inicialmente o uso de um nome artístico apenas por existir fora dela, mas deve permitir denúncias de falsificação de identidade. Em caso de denúncia, a administração pode analisar evidências de autoria e, se necessário, alterar o nome utilizado, restringir a conta ou solicitar verificação adicional. Podemos trabalhar com o uso de selos de verificado para evitar falsificações.


* O que acontece com a segurança do login se o usuário acessar a plataforma a partir de uma rede Wi-Fi pública e um atacante conseguir capturar e reutilizar o token de sessão dele?

> **Resposta:** Toda comunicação entre o cliente e o servidor deve utilizar HTTPS/TLS, evitando que tokens sejam transmitidos em texto aberto. Os tokens de sessão devem possuir validade limitada, ser armazenados de forma segura e possuir mecanismos de revogação. Também devem ser utilizadas configurações seguras de cookies, como `HttpOnly`, `Secure` e `SameSite`, quando aplicável. Caso uma sessão comprometida seja identificada, ela deve poder ser invalidada.


* O que acontece se a provedora de autenticação externa (como Google ou Apple) sofrer um vazamento de dados e os tokens de acesso dos usuários forem expostos?

> **Resposta:** A plataforma não deve armazenar desnecessariamente tokens de acesso de provedores externos. Quando utilizados, os tokens devem possuir escopo e validade limitados e ser armazenados de forma segura. A autenticação externa deve utilizar protocolos apropriados, como OAuth 2.0/OpenID Connect. Caso uma credencial externa seja comprometida, a sessão correspondente poderá ser revogada e o usuário deverá realizar uma nova autenticação.

* O que acontece se um artista decidir vender sua conta com a reputação intacta para terceiros, ou se perder a capacidade civil e familiares tentarem assumir a operação do portfólio?

> **Resposta:** A conta deve ser considerada pessoal e intransferível, principalmente quando estiver associada à reputação, avaliações e histórico de contratações. A transferência informal da conta não deve permitir que outra pessoa assuma automaticamente a identidade e o histórico do artista. Em situações excepcionais, como incapacidade ou falecimento, eventuais alterações de titularidade deverão seguir um procedimento administrativo específico, com análise das evidências e dos requisitos legais aplicáveis.


### 2. Tampering (Adulteração)

* O que acontece se alguém alterar a extensão de um arquivo executável para `.jpg` ou `.png` e tentar enviá-lo como obra de arte para o servidor?

> **Resposta:** A plataforma não deve confiar apenas na extensão do arquivo. O servidor deve validar o tipo real do arquivo, seu conteúdo e sua estrutura antes de armazená-lo ou processá-lo. Arquivos que não corresponderem aos formatos de imagem permitidos devem ser rejeitados. Os arquivos enviados também devem ser armazenados fora de diretórios executáveis e com nomes gerados pelo sistema.


* O que acontece se um usuário enviar uma imagem maliciosamente adulterada que se expande para gigabytes ou terabytes na memória quando o servidor tenta processá-la?

> **Resposta:** O servidor deve estabelecer limites para tamanho do arquivo, dimensões da imagem e recursos utilizados durante seu processamento. Imagens que excederem os limites devem ser rejeitadas antes de serem processadas.

* O que acontece se um comprador mal-intencionado interceptar a comunicação do navegador e tentar enviar um valor diferente do preço estipulado na hora de fechar um pedido?

> **Resposta:** O preço não deve ser considerado confiável quando enviado pelo navegador. O servidor deve recuperar e validar o preço associado à comissão ou à proposta aceita e calcular novamente o valor da contratação no _backend_ (log de transações). Ambos os usuários (artista e cliente) tambem devem se atentar as informações no momento de confirmar, então tem que ficar bem claro para ambos os valores da contratação de serviço.

* O que acontece se dois administradores, ou o artista e o comprador, tentarem alterar o status do mesmo pedido ou obra de arte exatamente no mesmo milésimo de segundo?

> **Resposta:** O sistema deve tratar alterações concorrentes de forma controlada utilizando transações, controle de concorrência e validação do estado atual do registro. Uma operação deve verificar se o estado esperado ainda é válido antes de realizar a alteração. Caso duas operações sejam incompatíveis, uma delas deverá ser rejeitada ou exigir uma nova atualização da página, evitando que uma alteração sobrescreva silenciosamente a outra.


### 3. Repudiation (Repúdio)

* O que acontece após a alteração de informações críticas da conta, como e-mail de acesso ou dados bancários para repasse de pagamentos?

> **Resposta:** Alterações de informações críticas devem gerar registros de auditoria contendo, no mínimo, o usuário responsável, data e hora, tipo de alteração e recurso afetado. Alterações especialmente sensíveis, como dados bancários, podem exigir confirmação adicional (2FA) e notificação ao usuário. Os registros de auditoria devem possuir proteção contra alterações indevidas.


* O que acontece se um artista excluir uma arte do seu perfil logo após uma reclamação formal de plágio ou disputa de direitos autorais?

> **Resposta:** A exclusão da obra do perfil público não deve necessariamente apagar imediatamente seus registros internos. Caso exista uma denúncia, disputa ou investigação em andamento, a plataforma deve preservar as informações necessárias para análise do caso, respeitando as obrigações legais e as regras de retenção de dados. A obra pode deixar de ser exibida publicamente, enquanto seus registros permanecem protegidos para fins de auditoria ou resolução da disputa.


* O que acontece se a plataforma precisar provar legalmente a autoria de uma publicação ou fraude?

> **Resposta:** A plataforma deve manter registros de auditoria relacionados às ações relevantes, como criação e alteração de publicações, contratações, mensagens, avaliações e alterações de conta. Os registros devem conter informações suficientes para estabelecer a sequência dos acontecimentos e possuir mecanismos de proteção contra adulteração. Quando necessário, esses registros poderão ser utilizados conforme os procedimentos legais aplicáveis.


* O que acontece se um usuário cometer uma fraude financeira ou enviar um arquivo ilícito e, em seguida, exigir a exclusão imediata de todos os seus rastros apelando para a LGPD?

> **Resposta:** A solicitação de exclusão de dados deve ser analisada de acordo com a LGPD e com as demais obrigações legais aplicáveis. O direito de exclusão não significa necessariamente que todos os dados devam ser apagados imediatamente. Informações que precisem ser mantidas para cumprimento de obrigação legal, exercício regular de direitos, prevenção de fraudes ou resolução de disputas poderão precisar ser preservadas pelo período necessário. Dados que não possuam justificativa para retenção devem ser excluídos ou anonimizados.


* O que acontece se um membro da própria equipe de desenvolvimento for subornado ou coagido para apagar os rastros de uma transação diretamente no banco de dados do servidor?

> **Resposta:** O acesso direto ao banco de dados deve ser restrito e controlado, seguindo o princípio do menor privilégio. Operações administrativas devem ser autenticadas, registradas e, quando possível, realizadas por mecanismos que permitam auditoria. Logs críticos devem possuir proteção contra alteração ou exclusão pelo mesmo usuário que possui acesso à aplicação ou ao banco. O ambiente de produção também deve possuir backups e mecanismos de recuperação para reduzir o impacto de alterações indevidas.


### 4. Information Disclosure (Divulgação de Informações)

* O que acontece com os metadados das fotos originais enviadas (como coordenadas de GPS e modelo da câmera) quando a arte passa a ser exibida publicamente?

> **Resposta:** Os metadados que não forem necessários para o funcionamento da plataforma devem ser removidos das imagens destinadas à publicação. Isso é especialmente importante para metadados que possam revelar informações privadas, como coordenadas de localização. A plataforma deve processar uma cópia destinada à publicação, preservando o arquivo original somente quando houver justificativa e controle de acesso adequado.


* O que acontece se alguém inspecionar o tráfego de dados no navegador?

> **Resposta:** A comunicação entre navegador e servidor deve utilizar HTTPS/TLS para impedir que terceiros na rede visualizem ou alterem os dados transmitidos. Informações sensíveis, como credenciais, mensagens privadas e dados de contratação, não devem ser enviadas por conexões não criptografadas. Também devem ser aplicadas configurações adequadas de segurança para sessões e cookies.


* O que acontece depois que a plataforma gera, por exemplo, uma planilha de vendas e libera o download: o arquivo fica travado exclusivamente para quem pediu, ou existe a chance de o link ser acessado por terceiros antes de expirar?

> **Resposta:** Arquivos contendo informações privadas devem possuir controle de acesso e não devem ser disponibilizados por URLs públicas permanentes. O download deve ser autorizado pelo servidor para o usuário que possui permissão e, quando forem utilizados links temporários, eles devem possuir validade limitada e, quando possível, estar associados à autorização do usuário. O sistema também deve registrar a geração e o acesso ao arquivo.


* O que acontece se um atacante monitorar os horários de postagem ou o status "online" de um artista para descobrir a rotina dele?

> **Resposta:** Pessoalmente não acho que seja necessário o _status_ de online. Porém no caso onde haja o _status_ "online" ele deve ser opcional ou substituído por informações menos precisas, quando possível. Horários e dados internos de atividade não devem ser disponibilizados publicamente. Dessa forma, reduz-se a possibilidade de utilização da plataforma para inferir a rotina do usuário.


### 5. Denial of Service (Negação de Serviço)

* O que acontece se um programa automatizado (robô) fizer centenas de tentativas consecutivas de login ou buscas com filtros pesados em um curto intervalo de tempo?

> **Resposta:** A plataforma deve implementar rate limiting e mecanismos de proteção contra automação abusiva. Tentativas excessivas de login podem gerar bloqueios temporários ou desafios adicionais, enquanto buscas devem possuir paginação, limites de resultados e filtros controlados. Também devem ser monitorados padrões anormais de utilização para identificar possíveis ataques.

* O que acontece se a plataforma de login externo escolhida pelo usuário (Google/Apple) sair do ar na origem?

> **Resposta:** A indisponibilidade do provedor externo pode impedir novos logins por esse método enquanto o serviço estiver indisponível. A plataforma deve tratar esse erro de forma controlada e informar o usuário sem expor informações internas. 

* O que acontece se um usuário tentar enviar um arquivo com tamanho excessivo ou dimensões em pixels gigantescas?

> **Resposta:** O servidor deve rejeitar arquivos que ultrapassem os limites definidos para tamanho, dimensões ou formato. Esses limites devem ser verificados antes de operações custosas de processamento. A plataforma também deve limitar a quantidade de uploads realizados em determinado período para evitar abuso do armazenamento e dos recursos computacionais.


* O que acontece com a navegação dos demais usuários da plataforma enquanto o servidor processa ou converte dezenas de imagens pesadas enviadas simultaneamente?

> **Resposta:** O processamento pesado de imagens deve ser separado das operações principais da aplicação, preferencialmente utilizando filas e trabalhadores de processamento. Dessa forma, os uploads podem ser colocados em uma fila e processados gradualmente, evitando que uma grande quantidade de imagens consuma todos os recursos do servidor. Também devem existir limites de concorrência e monitoramento da utilização de CPU, memória e armazenamento.


* O que acontece se uma rede de robôs começar a colocar dezenas de obras limitadas no carrinho apenas para "reservar" o estoque, sem nunca finalizar o pagamento?

> **Resposta:** Os itens adicionados ao carrinho não devem permanecer reservados indefinidamente. Como a plataforma possua recursos limitados, a reserva deve possuir um tempo de expiração e ser liberada automaticamente caso a contratação não seja concluída. Também devem ser aplicados limites de quantidade e frequência para reduzir abusos automatizados.


### 6. Elevation of Privilege (Elevação de Privilégios)

* O que acontece se um visitante comum tentar acessar diretamente o endereço das telas do painel administrativo pelo navegador?

> **Resposta:** O acesso deve ser bloqueado no servidor independentemente de o usuário conseguir visualizar ou descobrir a URL. Cada requisição às funcionalidades administrativas deve verificar a autenticação e as permissões do usuário. Um visitante sem privilégios deve receber uma resposta de acesso negado ou ser direcionado para a autenticação, sem ter acesso aos dados administrativos.


* O que acontece se um usuário logado alterar manualmente o identificador da obra no endereço web (trocando de `/editar-arte/10` para `/editar-arte/11`)?

> **Resposta:** O servidor deve verificar se o usuário autenticado possui permissão sobre a obra solicitada. A simples alteração do identificador na URL não pode conceder acesso. Caso a obra pertença a outro artista ou o usuário não possua autorização, a operação deve ser recusada. Essa validação deve ocorrer no backend em todas as operações de consulta, edição e exclusão.


* O que acontece se a assinatura de um plano de um artista ou o tempo de sessão dele vencerem no exato milésimo de segundo em que ele estiver salvando configurações de valores ou de portfólio?

> **Resposta:** O servidor deve validar a sessão e as permissões no momento da operação. Caso a sessão ou o plano expire antes da confirmação da alteração, a operação deve ser recusada ou tratada de acordo com uma regra transacional previamente definida. O sistema não deve confiar apenas no estado apresentado na interface do usuário. Em caso de falha, os dados já salvos devem permanecer consistentes e o usuário deve ser informado para realizar uma nova autenticação ou renovação.


* O que acontece se um artista convidar outro para ser "coautor" de uma peça, e o convidado tentar explorar essa permissão para alterar as configurações ou dados bancários do dono do perfil principal?

> **Resposta:**  Atualmente, a plataforma não prevê a existência de coautores, colaboradores ou assistentes. Cada conta será individual e terá controle apenas sobre seus próprios dados, obras, configurações e informações financeiras. Dessa forma, um artista não poderá conceder permissões de edição ou administração de suas obras e perfil a outro usuário. Caso esse recurso seja implementado futuramente, será necessário definir permissões específicas para cada função, impedindo que colaboradores acessem dados sensíveis, como informações bancárias, credenciais ou configurações do proprietário.
