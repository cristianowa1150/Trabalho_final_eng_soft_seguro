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

Após a verificação, o processo local foi encerrado. A implantação comprova a
execução do artefato atual em perfil `prod`; não representa publicação em um
servidor externo ou pipeline CI/CD.

## 4. Conclusão

O item de **implantação** foi coberto com execução real do JAR reconstruído,
perfil `prod` ativo e validações HTTP dos controles principais. O item de
**análise de código e dependências** foi coberto pela árvore Maven e pelo
SpotBugs, com 45 achados para triagem. O Dependency-Check foi apenas iniciado e
permanece pendente de uma execução completa com sua base NVD atualizada.
