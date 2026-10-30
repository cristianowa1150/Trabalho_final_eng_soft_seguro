# Etapa 7 — DevSecOps e Roteiro da Apresentação Final

**Sistema:** Artifício — Catálogo e Gestão de Comissões Artísticas  
**Disciplina:** Engenharia de Software Seguro (PPGCO/UFU)

## 30. Objetivo

Este roteiro integra as etapas do trabalho em um fluxo DevSecOps. A proposta não
é implementar um pipeline real, mas mostrar em que momento cada controle de
segurança seria executado, qual evidência seria produzida e quando o fluxo
deveria parar.

## 31. Pipeline DevSecOps proposto

| Momento | Atividade de segurança | Evidência produzida no projeto | Condição para continuar |
|---|---|---|---|
| Planejamento | Identificar ameaças com STRIDE, casos de abuso e riscos | [Documentação da Etapa 1](../docs/Etapa%201/) e análise/priorização de riscos da Etapa 2 | Riscos prioritários identificados, responsáveis definidos e tratamento planejado |
| Requisitos e arquitetura | Derivar requisitos de segurança, mapear vulnerabilidades e decidir controles | [Arquitetura segura](../diagramas/arquitetura_segura.png), requisitos e decisões da Etapa 3 | Requisitos de autenticação, autorização, validação, auditoria e implantação aprovados |
| Implementação | Aplicar autorização por posse e papel, validação estrita, CSRF, hashing e tratamento seguro de erros | Código em [`codigo/artificio/`](../codigo/artificio/) e testes de segurança da Etapa 4 | Revisão de código concluída e nenhum segredo versionado |
| Testes automatizados | Executar testes unitários, integração, casos de abuso e testes de segurança | [Relatório de testes](../docs/relatorio_testes_casos_de_abuso.md) e relatório JaCoCo | Testes obrigatórios aprovados e falhas analisadas |
| Análise de código e dependências | Revisar configurações, dependências, resultados de análise estática e exposição de segredos | `pom.xml`, revisão do código e registros de análise | Nenhuma vulnerabilidade crítica sem tratamento e nenhuma credencial exposta |
| Teste dinâmico/pentest autorizado | Executar ZAP baseline e verificações HTTP somente contra a aplicação local | [Relatório ZAP e evidências da Etapa 5](../evidencias/etapa-5/) | Todos os achados críticos/altos analisados, com correção ou aceite formal do risco |
| Implantação | Publicar somente artefato aprovado, com configuração de produção e segredos fora do repositório | JAR produzido, perfil `prod` e instruções de execução em [`codigo/artificio/README.md`](../codigo/artificio/README.md) | Perfil de produção ativo, console H2 bloqueado e configuração revisada |
| Monitoramento e resposta | Observar autenticação, autorização, uploads, erros e alertas de abuso; triar e conter incidentes | [Roteiro da Etapa 6](./etapa-6-deteccao-de-intrusoes.md) | Alertas encaminhados, incidentes registrados e ações de resposta definidas |

### Condições que impedem a continuidade

O pipeline deve parar e abrir uma pendência quando ocorrer qualquer uma destas
condições:

1. teste automatizado de segurança reprovado ou falha no controle de acesso;
2. vulnerabilidade crítica ou alta encontrada pelo ZAP/análise de código sem
   correção, mitigação documentada ou aceite formal;
3. segredo, senha, token ou chave encontrado no código, HTML, URL ou log;
4. dependência conhecida como vulnerável sem atualização ou justificativa;
5. console H2, facilitador de login ou outra configuração de desenvolvimento
   habilitada no perfil de produção;
6. evidência de falha de monitoramento ou de resposta para um alerta relevante.

## 32. Roteiro da apresentação final (10 a 15 minutos)

| Tempo | Participante sugerido | Conteúdo |
|---|---|---|
| 1 min | Integrante 1 | Apresentar o sistema Artifício, seu público e o objetivo do trabalho |
| 2 min | Integrante 1 | Explicar ameaças STRIDE, casos de abuso e os riscos prioritários |
| 2 min | Integrante 2 | Mostrar requisitos de segurança, arquitetura segura e decisões de projeto |
| 2 min | Integrante 2 | Demonstrar implementação segura e testes automatizados |
| 2 min | Integrante 3 | Apresentar a verificação da Etapa 5, o ZAP, os achados e as limitações |
| 2 min | Integrante 3 | Explicar as três regras de detecção e a resposta a alertas da Etapa 6 |
| 2 min | Integrante 4 | Mostrar o pipeline DevSecOps, os gates de segurança e a implantação |
| 1–2 min | Todos | Relatar aprendizados, limitações, próximos passos e responder perguntas |

Os nomes dos participantes e a divisão final devem ser confirmados pelo grupo
antes da apresentação. Todos devem participar e os commits individuais devem
permanecer associados às respectivas contas.

### Checklist visual para a apresentação

- sistema escolhido e evolução entre as etapas;
- ameaças, casos de abuso e riscos prioritários;
- arquitetura e requisitos de segurança;
- práticas de código seguro e testes;
- resultados da verificação dinâmica;
- regras de detecção e resposta;
- pipeline, condições de parada e aprendizados.

Não é necessário demonstrar todas as tabelas. A apresentação deve destacar as
decisões, as evidências e a evolução do software. Um vídeo pode ser gravado como
material adicional, mas não substitui a apresentação presencial prevista no
enunciado.

## 33. Critérios de avaliação da Etapa 7

O roteiro cobre a integração entre as etapas, os oito momentos do pipeline,
condições objetivas de parada e uma apresentação final planejada para a duração
solicitada. A execução da apresentação e a eventual gravação ainda dependem da
organização do grupo.
