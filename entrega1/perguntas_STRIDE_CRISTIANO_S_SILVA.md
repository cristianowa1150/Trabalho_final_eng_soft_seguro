# Análise de Ameaças (STRIDE) - Cristiano Silveira Silva

## Perguntas para Validação com o Luan Diniz Mazaro Rodovalho


### 1. Spoofing (Falsificação de Identidade)

* Como vamos validar a identidade real dos artistas para evitar que golpistas criem perfis falsos usando a arte de terceiros (impactando os requisitos RF01 e RF06)? 
> **Resposta:** [Insira a resposta aqui]

* Haverá algum mecanismo de recuperação de conta caso um usuário legítimo perca o acesso, garantindo que um atacante não roube a conta por meio do "Esqueci minha senha"?
> **Resposta:** [Insira a resposta aqui]

* A autenticação segura (RNF01) exigirá múltiplos fatores (MFA) para contas administrativas (RF38) ou para artistas que movimentam valores altos?
> **Resposta:** [Insira a resposta aqui]

### 2. Tampering (Adulteração)
* Quais controles teremos para impedir que um cliente mal-intencionado altere o "preço-base" (RF11) ou os prazos durante o envio de uma solicitação de comissão?
> **Resposta:** [Insira a resposta aqui]

* Como vamos proteger o sistema de avaliações (RF33 a RF35) para que um artista não consiga apagar reviews negativos ou inserir avaliações falsas em seu próprio perfil?
> **Resposta:** [Insira a resposta aqui]

* Os arquivos de imagens do portfólio do artista (RF08) terão algum bloqueio para evitar que sejam sobrescritos por scripts maliciosos no servidor?
> **Resposta:** [Insira a resposta aqui]

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