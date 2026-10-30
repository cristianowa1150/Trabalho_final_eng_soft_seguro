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
antes da geração do relatório por causa do tempo de atualização da base. Portanto,
não há resultado de vulnerabilidades do Dependency-Check a declarar nesta
execução. O log parcial está em
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

### 2.1 Triagem inicial de achados (SAST/SCA)

| Ferramenta | Achado registrado | Classificação inicial | Mitigação/justificativa | Status |
|---|---|---|---|---|
| SpotBugs | `EI_EXPOSE_REP`/`EI_EXPOSE_REP2` em DTOs/entidades e injeção por construtor (45 itens no total) | Predominantemente baixo risco arquitetural, exige revisão caso a caso | Manter build sem bloqueio automático nesta execução e abrir triagem técnica por classe antes de tratar como vulnerabilidade real | **Pendente de triagem detalhada** |
| Dependency-Check | Execução interrompida durante atualização NVD, sem relatório final | Sem classificação possível nesta execução | Reexecutar com atualização completa da base NVD e gerar relatório para decisão de mitigação | **Pendente** |

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

- `codigo/artificio/src/main/resources/application-prod.properties` define
  `spring.h2.console.enabled=false`;
- `codigo/artificio/src/main/resources/application-prod.properties` define
  `artificio.demo.exibir-credenciais=false`.

Após a verificação, o processo local foi encerrado. A implantação comprova a
execução do artefato atual em perfil `prod`; não representa publicação em um
servidor externo ou pipeline CI/CD.

## 4. Conformidade com o checklist oficial da Etapa 7

| Item do checklist de entrega | Evidência no repositório | Status |
|---|---|---|
| Perfil `prod` sem console H2 e sem credenciais padrão | [implantacao-prod-verificacao.json](./implantacao-prod-verificacao.json), [implantacao-prod-verificacao.txt](./implantacao-prod-verificacao.txt), `codigo/artificio/src/main/resources/application-prod.properties` | Atendido |
| Análise SAST com triagem/mitigação documentada | [spotbugs-execucao.txt](./spotbugs-execucao.txt), seção 2.1 deste relatório | Parcial (triagem detalhada pendente) |
| Análise SCA com resultado documentado | [dependency-check-execucao.txt](./dependency-check-execucao.txt), seção 2.1 deste relatório | Parcial (execução completa pendente) |
| Inventário de dependências da build | [dependency-tree.txt](./dependency-tree.txt) | Atendido |
| Relato consolidado da implantação segura | [implantacao-prod-rebuild.log](./implantacao-prod-rebuild.log), [implantacao-prod-verificacao.json](./implantacao-prod-verificacao.json) | Atendido |

## 5. Conclusão

O item de **implantação** foi coberto com execução real do JAR reconstruído,
perfil `prod` ativo e validações HTTP dos controles principais. O item de
**análise de código e dependências** foi coberto pela árvore Maven e pelo
SpotBugs, com 45 achados para triagem. O Dependency-Check foi apenas iniciado e
permanece pendente de uma execução completa com sua base NVD atualizada.
