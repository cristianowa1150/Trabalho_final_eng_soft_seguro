# Análise de Ameaças (STRIDE) - Anna Letycia

## Perguntas para Validação com o Luan Diniz Mazaro Rodovalho


### Sobre o fluxo e o que tem valor

* Você pode descrever uma contratação completa, desde a busca pelo artista até a conclusão? Quem participa de cada etapa?
> **Resposta:** O cliente inicia o processo pesquisando artistas por meio de filtros, como estilo de arte, tipo de comissão e outras características. Em seguida, visualiza os perfis, portfólios, preços-base e condições de trabalho dos artistas disponíveis. Após escolher um artista, o cliente envia uma solicitação de comissão, informando as características desejadas para o trabalho. O artista recebe a solicitação e pode aceitar, recusar ou negociar o preço, prazo e condições do serviço. Quando ambas as partes concordam, a contratação é registrada na plataforma com as condições definidas. O artista realiza o trabalho e envia a entrega pelo sistema. O cliente pode solicitar revisões conforme o combinado e, após receber o trabalho, confirmar a conclusão da comissão. Ao final, o cliente poderá avaliar o artista e o serviço prestado. De fundo tambem haveria o **Administrador** que atua na moderação, resolução de conflitos e manutenção da plataforma, quando necessário.

* Qual seria o pior incidente possível para você como dono da plataforma? Quem seria prejudicado e por quê?
> **Resposta:** Acredito que o pior incidente possível seria de um acesso não autorizado a alguma conta, principalmente referente a contas de artista, levando-o a perder a conta com *comissions* já contratadas, as avaliações da conta ou ter alguém se passando pelo artista em questão e roubando projetos e clientes dele.

### 1. Spoofing (Falsificação de Identidade)

* Um artista poderá permitir que um assistente gerencie seu perfil? Se sim, precisamos distinguir quem realizou cada ação? 
> **Resposta:** Não, a plataforma é planejada para que cada artista gerencie seu próprio perfil.

* Se alguém criar um perfil usando o nome e os trabalhos de um artista conhecido, como identificaremos a fraude para resolver a situação?
> **Resposta:** A plataforma deverá permitir que artistas denunciem perfis que estejam utilizando indevidamente seu nome, identidade ou trabalhos artísticos. O sistema poderá disponibilizar um processo de verificação de identidade ou de autoria, mediante apresentação de evidências, como links para portfólios oficiais ou outras informações que comprovem a autoria, sendo eles analisados pelo administrador. Também há a possibilidade do uso de selo de verificação em contas.

* Se alguém fingir ser o suporte da plataforma e pedir arquivos ou informações, como o usuário poderá reconhecer um contato legítimo?
> **Resposta:** A plataforma deverá possuir um canal oficial de suporte, identificado dentro da própria aplicação. O suporte não deverá solicitar senhas, códigos de autenticação ou outras credenciais secretas dos usuários. Sempre que possível, as comunicações oficiais deverão ficar registradas no sistema, permitindo identificar o responsável pelo atendimento e reduzir o risco de falsificação de identidade.

### 2. Tampering (Adulteração)
* Se o artista alterar preço, prazo ou número de revisões depois do aceite, o pedido deve preservar as condições anteriores? Quando uma mudança é permitida?
> **Resposta:** Sim. Após o aceite de uma proposta, a contratação deverá preservar as condições acordadas, incluindo preço, prazo, quantidade de revisões e características do serviço. O artista poderá alterar os valores e condições dos serviços que ainda não foram contratados, mas essas alterações não deverão modificar automaticamente uma contratação já aceita. Caso seja necessário alterar uma contratação existente, a mudança deverá ser apresentada ao cliente como uma nova proposta ou alteração contratual, **exigindo a concordância das partes**. O sistema deverá manter o histórico das condições anteriores e das alterações realizadas, evitando que uma das partes modifique unilateralmente os termos acordados.

* O administrador poderá corrigir preços, propostas ou pedidos? Quais alterações exigem justificativa ou comunicação às partes?
> **Resposta:** O administrador poderá corrigir informações administrativas ou atuar na resolução de conflitos, mas não deverá alterar livremente os termos de uma contratação sem justificativa. Alterações em preços, propostas, prazos ou condições de pedidos deverão ser registradas em um histórico, contendo o responsável, a data, o motivo e os dados anteriores e posteriores à alteração. Quando a alteração afetar diretamente uma contratação, as partes envolvidas deverão ser comunicadas.


### 3. Repudiation (Repúdio)

* Quais ações precisam registrar autor, data e condições vigentes?
> **Resposta:** A plataforma deverá registrar as ações relevantes para a segurança e para a resolução de conflitos. Contendo, quando aplicável, o autor da ação, data e hora, identificação da contratação, condições vigentes e informações necessárias para reconstruir o histórico do evento. Sendo as ações:

> Cadastro, login e alterações de conta.

> Criação e alteração de perfis e portfólios.

> Cadastro e alteração de preços, prazos e condições de comissões.

> Envio, aceite, recusa e alteração de propostas.

> Mensagens e alterações relevantes na negociação.

> Criação, alteração, cancelamento e conclusão de contratações.

> Envio e recebimento de arquivos de entrega.

> Ações administrativas, como bloqueios e alterações realizadas por moderadores.

* Se o artista afirmar que entregou e o cliente disser que não recebeu, o que será considerado evidência de entrega?
> **Resposta:** A evidência de entrega deverá ser baseada no registro da própria plataforma, incluindo o arquivo enviado, o autor do envio, a data e hora e a identificação da contratação. O sistema deverá registrar o status da entrega e permitir que o cliente confirme o recebimento. Caso haja divergência, o histórico da contratação, os registros de envio e as mensagens relacionadas poderão ser utilizados pelo administrador para analisar o conflito. A plataforma deverá evitar que uma das partes consiga apagar ou alterar unilateralmente os registros necessários para essa análise.

* Se uma das partes apagar a conta ou uma mensagem, o que precisa continuar disponível para resolver um conflito?
> **Resposta:** A exclusão de uma conta não deverá apagar imediatamente os registros necessários para resolver conflitos relacionados a contratações já realizadas.

> Deverão ser preservados, conforme as regras de retenção da plataforma:

> Histórico das contratações.

> Propostas aceitas e condições acordadas.

> Registros de pagamentos, caso existam dentro do sistema.

> Evidências de entrega e arquivos necessários para análise.

> Registros de ações administrativas.

> Mensagens. De forma que mensagens não devem ser possíveis de serem apagadas, apenas por administradores.



### 4. Information Disclosure (Divulgação de Informações)

* O artista precisa ver nome completo, e-mail ou outros dados do cliente? O cliente precisa ver quais dados do artista?
> **Resposta:** O artista verá apenas nome e foto do usuário. O cliente deverá visualizar apenas as informações públicas do artista, como nome de exibição, descrição, portfólio, tipos de comissão, preços-base, prazos e condições de trabalho.

* Os administradores precisam acessar todas as conversas e arquivos, ou apenas em determinadas situações?
> **Resposta:** Os administradores não deverão ter acesso irrestrito às conversas e arquivos dos usuários. O acesso deverá ocorrer apenas quando necessário para atividades legítimas da plataforma, como:

> Resolução de denúncias e conflitos.

> Investigação de fraude ou abuso.

> Moderação de conteúdo.

> Atendimento de suporte autorizado.

> Cumprimento de obrigações legais.

> O acesso deverá ser limitado por permissões, registrado em logs e restrito às informações necessárias para a finalidade da análise. Sempre que possível, o usuário deverá ser informado quando seus dados forem acessados para fins de moderação ou resolução de conflitos.

* O que deve acontecer com arquivos e dados após a exclusão de uma conta?
> **Resposta:** A exclusão da conta deverá iniciar um processo de tratamento dos dados do usuário, considerando a finalidade dos dados e as regras de retenção da plataforma. Dados que não sejam mais necessários deverão ser excluídos ou anonimizados, conforme as regras de privacidade aplicáveis. Entretanto, informações relacionadas a contratações, disputas, registros de segurança ou obrigações legais poderão precisar ser preservadas durante um período definido.

* Quais dados serão públicos e quais serão privados? Isso muda quando uma solicitação vira contratação?
> **Resposta:** Antes da contratação, as informações públicas serão aquelas necessárias para o cliente conhecer o artista e seus serviços:

> Nome de exibição.

> Descrição do perfil.

> Portfólio.

> Categorias e tags.

> Tipos de comissão.

> Preços-base e prazos informados.

> Para o artista estará disponível apenas o nome do contratante.

> Dados privados, como e-mail, mensagens, informações cadastrais e propostas de negociação, deverão ser protegidos. Quando uma solicitação virar contratação, as partes poderão ter acesso às informações necessárias para executar o serviço, como condições acordadas, prazo, arquivos de referência e dados da comissão. A contratação não deverá tornar automaticamente públicos os dados privados do cliente ou do artista.

### 5. Denial of Service (Negação de Serviço)

* Se a plataforma ficar indisponível no dia de uma entrega, como ficam o prazo, o registro da entrega e uma eventual reclamação?
> **Resposta:** Se a plataforma ficar indisponível no dia da entrega, o sistema deverá preservar os registros existentes e permitir a recuperação das informações após o restabelecimento do serviço. O prazo da contratação deverá considerar a indisponibilidade quando ela impedir o envio ou recebimento da entrega. O sistema deverá registrar incidentes de indisponibilidade, quando possível, para auxiliar na análise de reclamações. Caso a entrega não possa ser realizada pela plataforma, deverá existir um procedimento para que o artista e o cliente possam comunicar o ocorrido e solicitar uma revisão do prazo ou da situação da contratação.

* Quanto tempo cada função pode ficar fora do ar antes de causar um problema grave: busca, login, mensagens e entrega?
> **Resposta:** Busca e filtros: a indisponibilidade impede a descoberta de artistas, mas não necessariamente interrompe contratações já existentes. Então poderia ficar algumas horas fora do ar antes de causar um problema grave.

> Login: impede o acesso às contas e às funcionalidades privadas. Então seria ideal ficar fora de ar o mínimo de tempo possível.

> Mensagens: pode interromper negociações e dificultar a comunicação entre cliente e artista, mas não necessariamente interrompe contratações já existentes. Então poderia ficar algumas horas fora do ar antes de causar um problema grave.

> Entrega: pode impedir a conclusão de uma comissão e causar conflitos relacionados a prazos. Então seria ideal ficar fora de ar o mínimo de tempo possível.

* Se alguém enviar centenas de solicitações a um artista, como isso afeta sua rotina? Ele precisa poder limitar, recusar ou pausar novos pedidos?
> **Resposta:** Sim. A plataforma deverá permitir que o artista gerencie sua disponibilidade para receber novas solicitações.

> O artista poderá:

> Pausar temporariamente o recebimento de novas comissões.

> Recusar solicitações.

> Definir limites de solicitações em andamento.

> Informar sua disponibilidade e prazo estimado.

> Além disso, o sistema deverá implementar mecanismos para reduzir o abuso, como limitação de requisições e prevenção de envio automatizado excessivo de solicitações. Essas medidas deverão proteger tanto a rotina do artista quanto a disponibilidade da plataforma.

* Qual perda de informações seria tolerável após uma falha? Perder uma imagem de portfólio e perder um aceite de proposta têm a mesma gravidade?
> **Resposta:** Os dados possuem diferentes níveis de importância para o funcionamento da plataforma. A perda de uma imagem de portfólio pode ser inconveniente, mas o artista poderá eventualmente reenviá-la. Já a perda de um aceite de proposta pode causar conflitos, prejuízos financeiros e dúvidas sobre a existência da contratação. Por isso, os dados relacionados a negociações, aceites, contratos, mensagens relevantes e entregas deverão possuir maior prioridade de proteção e recuperação.

### 6. Elevation of Privilege (Elevação de Privilégios)

* Quais ações são exclusivas de clientes, artistas, moderadores e administradores?
> **Resposta:**
> **Cliente:**

> Pesquisar artistas.

> Visualizar perfis e portfólios.

> Enviar solicitações.

> Negociar com artistas.

> Acompanhar suas contratações.

> Confirmar recebimento e avaliar serviços.

> **Artista:**

> Criar e gerenciar seu perfil.

> Cadastrar portfólio e comissões.

> Definir preços>base e condições.

> Receber e responder solicitações.

> Gerenciar suas contratações.

> Enviar entregas.

>**Administrador/ Moderador:**

> Analisar denúncias.

> Moderar conteúdos.

> Aplicar medidas de moderação autorizadas.

> Consultar informações necessárias para investigar violações.

> Gerenciar usuários e permissões.

> Gerenciar categorias.

> Administrar configurações da plataforma.

> Acessar funções administrativas autorizadas.

> Resolver conflitos e incidentes.

* Se um artista tentar confirmar a conclusão no lugar do cliente, em alguma situação isso seria permitido? Quem pode resolver pedidos sem resposta?
> **Resposta:** A conclusão de uma comissão deverá ser confirmada pelo cliente, após o recebimento e a análise da entrega. O artista poderá marcar o trabalho como enviado, mas não deverá confirmar unilateralmente que o cliente recebeu ou aprovou a comissão. Caso o cliente não responda, a plataforma poderá definir um prazo para análise e encerramento automático, desde que essa regra seja previamente informada às partes. Em situações de conflito ou ausência de resposta, o administrador poderá analisar o histórico da contratação e resolver o caso conforme as regras da plataforma. A possibilidade de conclusão automática deverá ser implementada com cuidado para evitar prejuízos ao cliente ou ao artista.

* Um usuário pode ser cliente e artista ao mesmo tempo? Como impedir que ele use os dois papéis para aprovar ações em benefício próprio?
> **Resposta:** Sim. Um usuário poderá exercer os dois papéis na plataforma, podendo contratar artistas e também oferecer seus próprios serviços. Entretanto, as permissões deverão ser aplicadas conforme a ação realizada e o contexto da contratação. O sistema deverá impedir que um usuário utilize privilégios administrativos ou de outra parte para aprovar ações em benefício próprio. Por exemplo, um artista não deverá conseguir aprovar unilateralmente uma contratação em nome do cliente, mesmo que possua um perfil de cliente.

* Ao bloquear uma conta ou retirar um funcionário da equipe, quais acessos devem parar imediatamente?
> **Resposta:** Ao bloquear uma conta, o sistema deverá impedir imediatamente o acesso às funcionalidades restritas, incluindo login, gerenciamento de perfil, envio de solicitações, negociação e acesso a informações privadas. As sessões ativas deverão ser invalidadas quando necessário, impedindo que o usuário continue utilizando a aplicação após o bloqueio. Caso um funcionário seja removido da equipe, suas permissões administrativas deverão ser revogadas imediatamente, incluindo acesso a dados de usuários, conversas, arquivos, moderação e configurações administrativas. O sistema deverá registrar a ação de bloqueio ou revogação, identificando quem realizou a alteração, quando ocorreu e qual foi o motivo, quando aplicável.
