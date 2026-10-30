# Etapa 6 — Monitoramento e Detecção de Intrusões

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)  
**Integrantes:**
- Anna Letycia Fernandes Reis
- Cristiano Silveira Silva
- Eduardo Nascimento de Souza Rolim
- Luan Diniz Mazaro Rodovalho

---

## 27. Objetivo

O objetivo desta etapa é compreender como um sistema pode identificar comportamentos suspeitos depois que entra em operação.

Não será necessário instalar ou implementar um sistema de detecção de intrusões.

O entregável será um **roteiro ou uma descrição textual** armazenado em:

```text
roteiros/etapa-6-deteccao-de-intrusoes.md
```

---

## 28. Entregável mínimo

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

---

## 29. Critérios de avaliação da Etapa 6

Serão considerados:

* compreensão do conceito;
* relação com os riscos do projeto;
* escolha adequada dos eventos;
* clareza das regras;
* coerência das respostas;
* qualidade do roteiro;
* participação individual nos commits.

## Relatório de execução

As verificações realizadas com JUnit 5, Mockito, Spring Boot Test, MockMvc,
Spring Data JPA, H2 e JaCoCo estão documentadas no
[relatório de testes dos frameworks](../../evidencias/etapa-6-frameworks/relatorio-testes-frameworks.md).
O relatório também referencia as evidências do OWASP ZAP produzidas na Etapa 5
e separa ferramentas configuradas de ferramentas que ainda não foram
executadas.
