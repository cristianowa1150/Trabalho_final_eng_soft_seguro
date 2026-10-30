# Etapa 7 — Relatório de integração DevSecOps

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)

## Objetivo

Este relatório registra como os testes e as ferramentas de segurança se
integram ao pipeline DevSecOps proposto para o Artifício. O pipeline descrito
é conceitual; os resultados abaixo correspondem somente às ferramentas
efetivamente configuradas e executadas no repositório.

## Evidências de teste

O relatório detalhado, com dois testes representativos por framework/camada,
está em [relatorio-testes-frameworks.md](../../evidencias/etapa-6-frameworks/relatorio-testes-frameworks.md).
Ele inclui:

- JUnit 5 e Mockito para testes unitários;
- Spring Boot Test, MockMvc e Spring Security Test para testes web;
- Spring Data JPA e H2 para persistência e segregação da auditoria;
- JaCoCo para geração de cobertura;
- OWASP ZAP com as evidências dinâmicas da Etapa 5.

## Pipeline e critérios de bloqueio

1. **Planejamento:** STRIDE, casos de abuso e riscos.
2. **Requisitos e arquitetura:** requisitos de segurança e decisões de controle.
3. **Implementação:** código seguro, autorização, validação, CSRF e hashing.
4. **Testes:** JUnit, Mockito, Spring Boot Test, MockMvc e JPA.
5. **Cobertura:** JaCoCo para acompanhar as áreas exercitadas.
6. **DAST:** OWASP ZAP contra a aplicação própria em ambiente autorizado.
7. **Implantação:** revisão do perfil `prod` e bloqueio de facilitadores de
   desenvolvimento.
8. **Operação:** logs, regras de detecção e resposta a incidentes.

O pipeline deve parar quando houver teste reprovado, falha de autorização,
segredo exposto, vulnerabilidade crítica/alta sem tratamento, dependência
vulnerável sem decisão, configuração de desenvolvimento em produção ou falha
de monitoramento.

## Coerência documental

- [Etapa 6](../Etapa%206/etapa6.md) explica detecção, eventos, regras e resposta,
  e aponta para os resultados de execução.
- Este documento explica a integração das ferramentas no pipeline e aponta para
  o relatório de evidências, sem duplicar os logs.
- [Roteiro da Etapa 7](../../roteiros/etapa-7-devsecops-e-video-final.md)
  continua sendo o material de apresentação e contém a divisão de tempo.
- [Evidências da Etapa 5](../../evidencias/etapa-5/) continuam sendo a fonte do
  resultado do ZAP.

Assim, os documentos não confundem roteiro, relatório e evidência bruta: o
roteiro orienta a apresentação, este arquivo explica a integração e a pasta
`evidencias` preserva os resultados executados.

## Análise de código, dependências e implantação

As atividades foram executadas e estão consolidadas no
[relatório de análise e implantação](../../evidencias/etapa-7-devsecops/relatorio-analise-implantacao.md).
A árvore Maven foi gerada, o SpotBugs encontrou 45 itens para triagem e o JAR
foi reconstruído e executado com o perfil `prod`. A verificação confirmou que a
aplicação responde em `/` e `/login`, não expõe os facilitadores de credenciais
e bloqueia `/h2-console` com HTTP 404.

O OWASP Dependency-Check foi iniciado, mas a atualização da base NVD foi
interrompida antes da geração do relatório. Essa limitação está registrada nas
evidências e não é apresentada como uma análise concluída.
