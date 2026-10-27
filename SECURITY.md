# Política de Segurança

Esta política descreve como reportar vulnerabilidades de segurança no
repositório **Trabalho Final de Engenharia de Software Seguro** e como os
relatos serão tratados.

## Versões suportadas

O projeto é acadêmico e não possui uma política de manutenção de versões
anteriores. Correções de segurança são avaliadas para a versão em
desenvolvimento e para a versão estável mais recente.

| Versão | Suporte de segurança |
| ------ | -------------------- |
| `main` / versão em desenvolvimento | :white_check_mark: |
| `1.0.x` | :white_check_mark: |
| Outras versões e branches | :x: |

O suporte está condicionado à possibilidade de reproduzir o problema em uma
versão ainda mantida. Como o projeto usa dependências de terceiros, uma
correção também pode exigir a atualização de uma dependência ou da
configuração de implantação.

## Como reportar uma vulnerabilidade

**Não abra uma issue pública para uma vulnerabilidade ainda não corrigida.**
O relato deve ser enviado de forma privada por um dos canais abaixo:

1. Use o recurso **Report a vulnerability** na aba **Security** deste
   repositório, quando ele estiver disponível; ou
2. Envie um e-mail para
   [cristianowa1150@hotmail.com](mailto:cristianowa1150@hotmail.com), com o
   assunto `[Segurança] Relato de vulnerabilidade`.

Se o recurso de reporte privado do GitHub estiver habilitado, ele é o canal
preferencial, pois permite discutir o problema sem expor detalhes antes da
correção.

### Informações a incluir

Para facilitar a análise, inclua:

- descrição clara do problema e do impacto;
- versão, branch, commit ou ambiente afetado;
- passos mínimos para reproduzir o comportamento;
- exemplo de requisição, entrada ou código de prova de conceito, quando
  aplicável;
- pré-requisitos e nível de acesso necessários para explorar o problema;
- evidências como logs, mensagens de erro ou capturas de tela, removendo
  credenciais, tokens, dados pessoais e outros segredos;
- sugestão de correção, se houver.

Relatos incompletos ainda serão considerados, mas informações suficientes para
reprodução ajudam a reduzir o tempo de resposta.

## O que esperar após o reporte

Os responsáveis pelo projeto buscarão:

- confirmar o recebimento em até **7 dias corridos**;
- avaliar a validade, o impacto e a abrangência do problema;
- manter o contato com o pesquisador durante a investigação;
- informar, quando possível, se o relato foi aceito, rejeitado ou necessita de
  mais informações;
- corrigir o problema ou documentar a decisão de não corrigi-lo;
- publicar a correção e os créditos ao pesquisador somente com autorização,
  respeitando a divulgação responsável.

Os prazos podem ser maiores por se tratar de um projeto acadêmico mantido de
forma voluntária. Caso não haja resposta após 7 dias, reencaminhe o relato
privado ou tente o outro canal indicado acima.

## Escopo

Fazem parte do escopo:

- o código-fonte da aplicação em [`codigo/artificio`](codigo/artificio);
- configurações e recursos versionados que alterem a segurança da aplicação;
- vulnerabilidades que possam comprometer autenticação, autorização,
  confidencialidade, integridade ou disponibilidade da aplicação;
- exposição acidental de credenciais, tokens ou outros segredos no repositório.

Ambientes de demonstração, dados fictícios, exemplos de login e o console H2
disponível apenas no perfil de desenvolvimento não devem ser tratados como
serviços de produção. Ainda assim, falhas que permitam acesso indevido fora do
escopo esperado ou que permaneçam habilitadas no perfil de produção devem ser
reportadas.

## Atividades permitidas e não permitidas

Pesquisas de segurança devem ser realizadas de forma responsável e apenas em
uma cópia local ou em um ambiente para o qual você tenha autorização. Não:

- acesse, altere, exclua ou exfiltre dados de terceiros;
- interrompa ou degrade serviços, execute ataques de negação de serviço ou
  faça testes de carga abusivos;
- use engenharia social, phishing ou acesso físico para testar o projeto;
- publique a vulnerabilidade, o código de exploração ou dados obtidos antes de
  uma correção e de um acordo de divulgação;
- inclua segredos reais em issues, pull requests, commits ou no próprio relato.

Se um teste expuser dados pessoais ou credenciais, interrompa-o, preserve
apenas o mínimo necessário para comprovar o problema e informe isso
imediatamente no relato.

## Divulgação responsável

Solicitamos um prazo razoável para investigação e correção antes de qualquer
divulgação pública. A data de divulgação deve ser combinada com os
responsáveis pelo projeto. Quando apropriado, a publicação de uma correção
poderá incluir um resumo do impacto, a versão corrigida e o crédito ao
pesquisador, sem divulgar detalhes que facilitem a exploração de usuários
desatualizados.

## Reconhecimento

Pesquisadores que fizerem um relato válido e seguirem esta política poderão
ser reconhecidos na documentação ou nas notas da correção, desde que
autorizem explicitamente o uso do nome ou identificador informado.

Este projeto não oferece programa de recompensa financeira por
vulnerabilidades.
