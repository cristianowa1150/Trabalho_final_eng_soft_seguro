# Relatório de testes dos frameworks e ferramentas

**Sistema:** Artifício  
**Data da execução:** 30/10/2026  
**Ambiente:** Windows, Java 25.0.4, Maven Wrapper, Spring Boot 4.1.2

## Escopo

Foram executadas duas verificações representativas por framework ou camada de
teste já configurada no projeto. Os mesmos testes podem exercer mais de uma
camada: por exemplo, `AuthControllerTest` usa Spring Boot Test e MockMvc.
Isso não significa que uma ferramenta ausente foi instalada ou executada.

| Framework/ferramenta | Dois testes registrados | Resultado |
|---|---|---|
| JUnit 5 | `ComissaoServiceTest.deveCriarSolicitacaoComSucesso`; `ComissaoServiceTest.deveRejeitarSolicitacaoParaSiProprio` | 2/2 aprovados |
| Mockito | Os mesmos dois testes unitários, com `@Mock`, `when` e `verify` | 2/2 aprovados |
| Spring Boot Test | `AuthControllerTest.deveRenderizarTelaDeLogin`; `AuthControllerTest.deveRejeitarCadastroInvalido` | 2/2 aprovados |
| MockMvc + Spring Security Test | Os mesmos dois testes HTTP, incluindo contexto web e CSRF no cadastro | 2/2 aprovados |
| Spring Data JPA + H2 | `ArtificioJpaTests.testCicloPersistenciaEntidades`; `ArtificioJpaTests.testSegregacaoSchemaAuditoriaEnvers` | 2/2 aprovados |
| JaCoCo | Instrumentação durante a suíte e geração de `jacoco.csv`/relatório HTML | Gerado com sucesso |
| OWASP ZAP | Verificação do console H2 no perfil padrão (A01); verificação do console H2 no perfil `prod` (A01) | Evidências já versionadas na Etapa 5 |

## Execução Maven

Comando usado:

```text
codigo/artificio/mvnw.cmd -Dtest=ComissaoServiceTest,AuthControllerTest,ArtificioJpaTests test jacoco:report
```

Resultado da execução:

```text
ArtificioJpaTests: Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
ComissaoServiceTest: Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
AuthControllerTest: Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
Total: Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
EXIT_CODE=0
```

O log integral está em
[execucao-testes-frameworks.txt](./execucao-testes-frameworks.txt). Os testes
selecionados e os resultados XML do Surefire foram gerados pelo Maven durante a
mesma execução.

## Cobertura

O relatório HTML e o CSV do JaCoCo estão em
[jacoco/](./jacoco/). O JaCoCo mede cobertura; ele não substitui os testes e
não é um scanner de vulnerabilidades.

## OWASP ZAP

O ZAP não foi reinstalado nem executado novamente nesta rodada. As duas
verificações registradas acima são as evidências autorizadas e já versionadas da
Etapa 5:

- [console H2 no perfil padrão](../etapa-5/A01-h2-console.html);
- [console H2 no perfil prod](../etapa-5/A01-prod-h2-console.html);
- [relatório completo do ZAP](../etapa-5/relatorio-zap.html).

## Ferramentas não configuradas

SpotBugs, OWASP Dependency-Check, Semgrep, Gitleaks, Snyk, SonarQube, Burp
Suite, Wazuh, Suricata e Falco não possuem configuração ou evidência anterior
no repositório. Portanto, não foram apresentados como resultados executados.
Eles podem ser adicionados em uma etapa posterior, preferencialmente no
pipeline DevSecOps, com seus próprios comandos, versões e arquivos de saída.

## Conclusão

Os resultados comprovam testes unitários, de integração web, segurança HTTP,
persistência/auditoria e cobertura. O relatório mantém a distinção entre
frameworks de teste, medição de cobertura e DAST, preservando a coerência com
as evidências da Etapa 5.
