# Etapa 6 — Monitoramento e Detecção de Intrusões

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)

## 27. Objetivo

Este roteiro descreve como o sistema Artifício poderia identificar comportamentos
suspeitos depois de entrar em operação. A proposta é conceitual: não é necessário
instalar ou implementar um IDS nesta etapa.

## 28. Conceitos

**Detecção de intrusões** é a observação de eventos do sistema para identificar
comportamentos que podem indicar abuso, tentativa de ataque ou comprometimento de
uma conta. A detecção não prova sozinha que houve um incidente; ela gera um alerta
para análise e resposta.

**Prevenir** significa reduzir a probabilidade de o abuso ocorrer ou impedir sua
execução, por exemplo, com autenticação, autorização por papel, validação de
entradas, proteção CSRF e isolamento por posse. **Detectar** significa reconhecer
um comportamento suspeito durante ou depois da tentativa, registrar evidências e
acionar uma resposta. Os controles preventivos do projeto continuam necessários:
as regras abaixo são uma camada complementar, não uma substituição para eles.

## Eventos que deveriam ser registrados

Os registros devem conter data e hora, resultado, identificador técnico da
requisição, rota e, quando aplicável, um identificador de usuário ou recurso.
Não devem conter senhas, tokens, conteúdo de sessão ou outros segredos.

- autenticações bem-sucedidas e malsucedidas, logout e bloqueio temporário;
- alterações de senha, criação/alteração de usuários e mudanças de papel;
- acesso negado por autorização, incluindo tentativas de acessar recurso de outro
  usuário;
- criação, alteração, cancelamento e mudança de estado de uma comissão;
- uploads rejeitados por tamanho, MIME, magic bytes ou conteúdo SVG;
- erros de validação, exceções HTTP e respostas 4xx/5xx;
- consultas ou tentativas de acesso a rotas administrativas e ao console H2;
- alertas gerados, responsável pela análise, ação tomada e encerramento do alerta.

Os registros de auditoria devem ser protegidos contra alteração indevida, ter
retenção definida e ser revisados somente por pessoas autorizadas.

## Regras simples de detecção

| Risco observado | Fonte de dados | Condição de alerta | Resposta inicial |
|---|---|---|---|
| Uso indevido ou tomada de conta | Logs de autenticação | Cinco falhas de login para a mesma conta ou origem em até 10 minutos, ou tentativas para várias contas a partir da mesma origem | Registrar o incidente, alertar a equipe, limitar temporariamente novas tentativas e validar a situação da conta sem expor a senha |
| Escalada de privilégio ou acesso indevido a dados | Logs de autorização, requisições e auditoria de recursos | Três respostas `403` para recursos de outro usuário ou para rotas administrativas em até 5 minutos, especialmente após login recente | Preservar os eventos correlatos, alertar a equipe, revisar a conta e revogar sessões/tokens se houver indício de comprometimento |
| Exploração de entrada ou upload malicioso | Logs de validação, upload e erros HTTP | Dez ou mais rejeições de upload/validação, ou cinco erros `4xx/5xx` semelhantes para a mesma origem em até 5 minutos | Aplicar limitação temporária, bloquear o arquivo e a origem conforme a política, analisar os registros e verificar se algum conteúdo foi persistido |

## O que acontece depois de um alerta

1. **Registrar e correlacionar:** preservar o alerta, os eventos relacionados e o
   horário, sem incluir segredos.
2. **Triar:** confirmar se o comportamento é um falso positivo ou um incidente,
   considerando usuário, origem, rota e sequência de eventos.
3. **Conter:** limitar tentativas, revogar sessões ou bloquear temporariamente a
   origem, de forma proporcional ao risco e sem apagar evidências.
4. **Erradicar e recuperar:** corrigir a causa, remover artefatos maliciosos,
   redefinir credenciais comprometidas e restaurar o serviço com segurança.
5. **Aprender:** documentar a decisão, ajustar as regras e criar um teste ou
   requisito preventivo para evitar a recorrência.

## Relação com as etapas anteriores

As regras se relacionam aos casos de abuso, aos controles de autorização e
validação implementados na aplicação e aos achados documentados na
[verificação de segurança da Etapa 5](../docs/Etapa%205/etapa5.md). Em particular,
o acesso ao console H2, a exposição de credenciais e a impressão de senha em log
devem gerar eventos e revisão operacional até que os controles corretivos estejam
aplicados.

## 29. Critérios de avaliação da Etapa 6

O roteiro cobre o conceito de detecção, a diferença entre prevenção e detecção,
os eventos relevantes, três regras com os quatro campos solicitados e o fluxo de
resposta após um alerta. A participação individual deve ser comprovada pelos
commits dos integrantes.
