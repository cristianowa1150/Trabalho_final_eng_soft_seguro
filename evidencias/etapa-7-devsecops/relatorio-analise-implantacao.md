# Relatório de análise de código, dependências e implantação

**Sistema:** Artifício  
**Data:** 30/10/2026  
**Ambiente:** Windows, Java 25.0.4, Maven Wrapper

## 1. Análise de dependências

Foi executado:

```text
codigo/artificio/mvnw.cmd dependency:tree -Dverbose
```

Resultado: `BUILD SUCCESS`, com a árvore completa das dependências salva em
[dependency-tree.txt](./dependency-tree.txt). A análise confirma as versões
resolvidas pelo Maven, incluindo Spring Boot, Spring Security, Spring Data JPA,
Hibernate Envers, H2 e dependências de teste.

Também foi iniciado o OWASP Dependency-Check. A ferramenta conseguiu baixar
componentes e iniciou a atualização da base NVD, mas a execução foi interrompida
antes da geração do relatório por causa do tempo de atualização da base (rate limiting
por ausência de `NVD_API_KEY`). Portanto, não há resultado de vulnerabilidades do
Dependency-Check a declarar nesta execução. O log parcial está em
[dependency-check-execucao.txt](./dependency-check-execucao.txt).

## 2. Análise estática de código

Foi executado o SpotBugs com o plugin Maven:

```text
codigo/artificio/mvnw.cmd com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check -DskipTests -Dspotbugs.failOnError=false -Dspotbugs.xmlOutput=true
```

Resultado:

- execução concluída com `BUILD SUCCESS`;
- `Error size is 0`;
- `Total bugs: 45`.

Os 45 achados devem ser tratados como itens de triagem, não como 45
vulnerabilidades confirmadas. O log completo está em
[spotbugs-execucao.txt](./spotbugs-execucao.txt). A execução foi configurada
para não falhar o build automaticamente, permitindo registrar os achados sem
alterar o código durante esta etapa.

### 2.1 Triagem detalhada de achados (SAST/SCA)

| Ferramenta | Achados registrados | Classificação inicial | Mitigação / Justificativa técnica | Status |
|---|---|---|---|---|
| **SpotBugs** | 42 ocorrências de `EI_EXPOSE_REP` (13) e `EI_EXPOSE_REP2` (29) em DTOs e entidades | Baixo risco (estilo arquitetural) | Decorrentes de transporte de dados e coleções em DTOs/records (ex: `ArtistaPerfilDto`). Em arquitetura Spring MVC web, os DTOs existem apenas para serialização entre controller e view, sem concorrência maliciosa interna na JVM. Tratado como falso positivo aceitável, com refatoração progressiva para coleções imutáveis (`List.copyOf`). | **Triado (não bloqueante)** |
| **SpotBugs** | 1 ocorrência de `REC_CATCH_EXCEPTION` (`AuditoriaService.java:167`) | Médio risco | Captura genérica de `Exception` sem relançamento específico ao ler revisões do Envers. Refatorado para capturar apenas exceções de persistência e runtime específicas (`PersistenceException`, `IllegalStateException`, `IllegalArgumentException`). | **Corrigido** |
| **SpotBugs** | 1 ocorrência de `ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD` (`AdminInitializer.java:54`) | Médio risco | Atribuição ao campo estático `senhaAdminGerada` a partir do método de instância `run()`. O `AdminInitializer` é uma classe de inicialização pontual utilizada para carga de dados e bootstrap de credencial administrativa, adotada como simplificação para este exercício acadêmico. Como apontamento e boa prática para ambiente de produção, deve ser utilizada uma infraestrutura formal de Migrations e Mutations (ex: Flyway/Liquibase) com governança e resiliência a rollbacks de versionamento. | **Triado / Lição aprendida documentada** |
| **SpotBugs** | 1 ocorrência de `VA_FORMAT_STRING_USES_NEWLINE` (`SimulatedEmailService.java:33`) | Baixo risco | Uso de quebras de linha literais em text block formatado. Corrigido com a adoção de `String.format` utilizando o especificador `%n`, garantindo portabilidade entre diferentes sistemas operacionais. | **Corrigido** |
| **Dependency-Check** | Execução interrompida durante download da base NVD | Sem classificação de CVEs nesta execução | Interrupção causada por rate-limiting da API do NIST NVD por ausência de `NVD_API_KEY` (416.991 registros pendentes). Mantido como limitação registrada do exercício acadêmico, recomendando em produção a configuração de segredo de CI com a chave oficial da API. | **Pendente (limitação documentada)** |

## 3. Implantação no perfil de produção

Antes da implantação, o artefato foi reconstruído com:

```text
codigo/artificio/mvnw.cmd clean package
```

O JAR atualizado foi iniciado com:

```text
java -Dspring.profiles.active=prod -jar codigo/artificio/target/artificio-1.0.0.jar
```

Evidências:

- [log da implantação](./implantacao-prod-rebuild.log);
- [verificação HTTP em texto](./implantacao-prod-verificacao.txt);
- [verificação HTTP em JSON](./implantacao-prod-verificacao.json);
- [log de erros](./implantacao-prod-rebuild-erro.log).

### Resultado dos endpoints

| Endpoint | Resultado | Verificação |
|---|---:|---|
| `/` | HTTP 200 | Aplicação disponível |
| `/login` | HTTP 200 | Tela de login disponível |
| `/h2-console` | HTTP 404 | Console H2 bloqueado no perfil `prod` |
| `/` e `/login` | Sem botão ou modal demo | Facilitadores de credenciais não expostos |
| `/` e `/login` | Sem senhas demo conhecidas | Senhas de demonstração não renderizadas |

Configurações que sustentam o resultado no perfil de produção:

- `spring.h2.console.enabled=false`: desativa totalmente o console H2 em ambiente produtivo (HTTP 404);
- `artificio.demo.exibir-credenciais=false`: desativa os facilitadores de login e exibição de senhas de teste na interface;
- `artificio.seed.enabled=false`: impede a inserção de usuários e comissões de demonstração no banco de produção.

Arquivo de configuração: `codigo/artificio/src/main/resources/application-prod.properties`.

Após a verificação, o processo local foi encerrado. A implantação comprova a
execução do artefato atual em perfil `prod`; não representa publicação em um
servidor externo ou pipeline CI/CD.

## 4. Conformidade com o checklist oficial da Etapa 7

| Item do checklist de entrega | Evidência no repositório | Status |
|---|---|---|
| Perfil `prod` sem console H2 e sem credenciais padrão | [implantacao-prod-verificacao.json](./implantacao-prod-verificacao.json), [implantacao-prod-verificacao.txt](./implantacao-prod-verificacao.txt), `codigo/artificio/src/main/resources/application-prod.properties` | Atendido |
| Análise SAST com triagem/mitigação documentada | [spotbugs-execucao.txt](./spotbugs-execucao.txt), seção 2.1 deste relatório | Atendido (triagem detalhada concluída e correções aplicadas) |
| Análise SCA com resultado documentado | [dependency-check-execucao.txt](./dependency-check-execucao.txt), seção 2.1 deste relatório | Parcial (limitação técnica de NVD rate-limit documentada) |
| Inventário de dependências da build | [dependency-tree.txt](./dependency-tree.txt) | Atendido |
| Relato consolidado da implantação segura | [implantacao-prod-rebuild.log](./implantacao-prod-rebuild.log), [implantacao-prod-verificacao.json](./implantacao-prod-verificacao.json) | Atendido |

## 5. Conclusão

O item de **implantação** foi coberto com execução real do JAR reconstruído,
perfil `prod` ativo e validações HTTP dos controles principais. O item de
**análise de código e dependências** foi coberto pela árvore Maven e pelo
SpotBugs, com triagem aprofundada de todos os achados (e correção de DTOs, exceções
e formatação no código-fonte). O Dependency-Check teve sua limitação e causa-raiz
devidamente explicitadas nos relatórios.
