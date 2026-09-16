# Análise de Ameaças (STRIDE) - Cristiano Silveira Silva

## Perguntas para Validação com o Luan Diniz Mazaro Rodovalho


### 1. Spoofing (Falsificação de Identidade)

* Como vamos validar a identidade real dos artistas para evitar que golpistas criem perfis falsos usando a arte de terceiros (impactando os requisitos RF01 e RF06)? 
> **Resposta:** Inicialmente, o cadastro de artistas poderá ser realizado por qualquer usuário que queira oferecer serviços na plataforma. Para reduzir a criação de perfis falsos, o sistema deverá permitir denúncias de uso indevido de identidade e de trabalhos artísticos. Futuramente, poderá ser implementado um mecanismo de verificação de identidade ou de autoria, com apresentação de evidências, como links para portfólios oficiais. O administrador deverá analisar as denúncias e poderá remover conteúdos indevidos ou bloquear contas que violem as regras da plataforma. Além disso podemos aderir o uso de selos de verificação em contas.

* Haverá algum mecanismo de recuperação de conta caso um usuário legítimo perca o acesso, garantindo que um atacante não roube a conta por meio do "Esqueci minha senha"?
> **Resposta:** Sim. O sistema deverá possuir uma funcionalidade de recuperação de senha por meio de um endereço de e-mail previamente cadastrado. O processo deverá utilizar um token de recuperação aleatório, de uso único e com tempo de expiração limitado. O token não deverá ser enviado em texto visível nos registros da aplicação. Após a redefinição da senha, as sessões ativas da conta deverão ser invalidadas quando necessário, reduzindo o risco de acesso contínuo por um atacante. Também deverão ser implementados mecanismos de proteção contra tentativas excessivas de recuperação de senha, como limitação de requisições e mensagens que não revelem se um e-mail está cadastrado.

* A autenticação segura (RNF01) exigirá múltiplos fatores (MFA) para contas administrativas (RF38) ou para artistas que movimentam valores altos?
> **Resposta:** O MFA deverá ser obrigatório para contas administrativas, devido ao alto nível de privilégio e ao acesso a informações sensíveis dos usuários. Para artistas, o MFA poderá ser disponibilizado como uma opção de segurança adicional.

### 2. Tampering (Adulteração)
* Quais controles teremos para impedir que um cliente mal-intencionado altere o "preço-base" (RF11) ou os prazos durante o envio de uma solicitação de comissão?
> **Resposta:** O preço-base e o prazo serão definidos pelo artista no cadastro da comissão. O cliente poderá visualizar essas informações e enviar uma solicitação ou proposta de negociação. O sistema deverá validar os valores e condições no backend, não confiando nos dados enviados pelo navegador. O cliente não poderá alterar diretamente o preço-base cadastrado pelo artista. Quando uma solicitação for enviada, o sistema deverá registrar os valores utilizados naquele momento. Caso haja negociação, as novas condições deverão ser registradas como uma proposta, com aceite das partes. Após a contratação, as condições acordadas deverão ser preservadas, e alterações deverão exigir autorização conforme as regras da plataforma.

* Como vamos proteger o sistema de avaliações (RF33 a RF35) para que um artista não consiga apagar reviews negativos ou inserir avaliações falsas em seu próprio perfil?
> **Resposta:** As avaliações deverão ser vinculadas a uma contratação concluída, permitindo que somente clientes que realmente contrataram o serviço possam avaliar. O artista não poderá criar avaliações em seu próprio perfil nem apagar avaliações diretamente. A remoção de avaliações deverá ser restrita ao administrador, mediante justificativa, como conteúdo ofensivo, fraude ou violação das regras da plataforma. O sistema deverá registrar o autor, a contratação relacionada e a data da avaliação. Também poderá limitar a quantidade de avaliações por contratação para evitar duplicidade ou manipulação.

* Os arquivos de imagens do portfólio do artista (RF08) terão algum bloqueio para evitar que sejam sobrescritos por scripts maliciosos no servidor?
> **Resposta:** Sim. Os arquivos enviados para o portfólio deverão ser tratados como conteúdo não confiável. O sistema deverá validar o tipo, o tamanho e o formato dos arquivos, permitindo somente extensões de imagem autorizadas, como PNG, JPEG e WebP. Os arquivos deverão ser armazenados em local separado do código executável da aplicação, evitando que um arquivo malicioso seja interpretado como um script no servidor. Os nomes dos arquivos deverão ser gerados pelo sistema, evitando conflitos e sobrescritas indevidas. O acesso aos arquivos deverá respeitar as permissões do usuário.

### 3. Repudiation (Repúdio)

* O sistema de logs (RNF15) irá registrar com exatidão o momento e o IP de quando um artista(usuário)aceita uma comissão ou de quando um cliente aprova o serviço? 
> **Resposta:** [Insira a resposta aqui]

* Se houver uma disputa ou denúncia (RF42), teremos um histórico imutável das mensagens e negociações feitas dentro da plataforma para o administrador analisar?
> **Resposta:** [Insira a resposta aqui]

* De acordo com a lei 13709 LGPD caso algum usuário solicite seus dados, ou sua exclusão o controlador dará esta opção no sistema? 
> **Resposta:** [Insira a resposta aqui]


### 4. Information Disclosure (Divulgação de Informações)
* Além de utilizar HTTPS (RNF04) e criptografar senhas (RNF02), os dados sensíveis dos clientes e artistas (como informações de pagamento, se houver no futuro, e dados cadastrais do RF03) estarão criptografados no banco de dados, e o banco estará no mesmo servidor da aplicação?
> **Resposta:** [Insira a resposta aqui]

* Como garantimos que a funcionalidade de busca e filtros (RF15 a RF19) não permita, via técnicas de injeção (ex: SQLi ou GraphQL abuse), a extração em massa da base de usuários?
> **Resposta:** [Insira a resposta aqui]
### 5. Denial of Service (Negação de Serviço)
* O upload de imagens para os portfólios (RF08) terá limite de tamanho e taxa de requisições por minuto (Rate Limiting) para evitar que a plataforma fique sem espaço ou caia?
> **Resposta:** [Insira a resposta aqui]

* As operações de busca e filtragem (RNF06) possuem paginação e limites rígidos? O que impede um robô de fazer buscas complexas repetidas vezes até esgotar os recursos do servidor, violando a disponibilidade (RNF17)?
> **Resposta:** [Insira a resposta aqui]


### 6. Elevation of Privilege (Elevação de Privilégios)
* O sistema que define os tipos de perfil (RF05) tem validações rigorosas no backend para impedir que um usuário recém-criado altere seu próprio status no banco para "Administrador" (violando o acesso à área do RF38)?
> **Resposta:** [Insira a resposta aqui]

* Como vamos isolar a área administrativa das funções normais de cliente/artista para garantir que endpoints de moderação (RF40 e RF43) jamais sejam executados sem um token de administrador válido?
> **Resposta:** [Insira a resposta aqui]

* Vamos implementar alguma stored procedure no banco para garantir consistencia?
> **Resposta:** [Insira a resposta aqui]
