## 18.3 Diagrama da arquitetura segura

![Arquitetura Segura](../../diagramas/arquitetura_segura)

- Serviço de Autenticação: identifica o usuário e mantém o controle da sessão.
- Rate Limiting: limita tentativas excessivas de autenticação e outras operações suscetíveis a automação.
- Autorização (RBAC + Ownership): verifica tanto o papel do usuário quanto sua permissão sobre o recurso específico.
- Banco de Dados: armazena contas, perfis, obras, contratações, mensagens e demais informações. (Decidir qual usar - firebase?)
- Armazenamento de Arquivos: mantém obras, referências e arquivos relacionados às comissões.
- Logs e Monitoramento: registram operações relevantes para detecção e investigação de incidentes.
- Serviços Externos: representam serviços como autenticação externa, caso sejam utilizados. (Temos de decidir quais usar, provavelmente google)

## 18.4 Decisões de arquitetura

### Decisão 1 — Autorização centralizada no backend

| **Decisão** | **Risco tratado** | **Justificativa** | **Componente afetado** | **Resultado esperado** |
| :--- | :--- | :--- | :--- | :--- |
| Validar permissões no backend em todas as operações protegidas, utilizando o papel do usuário e as regras de acesso ao recurso. | **R28, R30** | A interface pode ser manipulada pelo usuário e não deve ser considerada uma barreira de segurança. A validação no servidor impede que um usuário acesse diretamente endpoints administrativos ou recursos pertencentes a outros usuários. | Backend/API e banco de dados | Impedir acesso não autorizado a funções administrativas e aos recursos de outros usuários. |

A autorização será realizada no backend antes da execução das operações protegidas. Para isso, serão considerados tanto o **papel do usuário** quanto as regras de **propriedade ou autorização sobre o recurso**.

```text
Usuário → API → Autorização
                  ├── Possui o papel necessário?
                  └── Possui autorização sobre o recurso?
                         ↓
                  Permitir / Recusar
```

### Decisão 2 — Controle de acesso aos arquivos baseado em autorização

| **Decisão** | **Risco tratado** | **Justificativa** | **Componente afetado** | **Resultado esperado** |
| :--- | :--- | :--- | :--- | :--- |
| O acesso a obras, referências e arquivos de comissões deverá passar pelo backend, que verificará a propriedade ou autorização do usuário antes de liberar o arquivo. | **R30** | Links ou identificadores de arquivos não devem representar autorização por si mesmos. A validação no backend reduz o risco de um usuário descobrir o identificador de uma obra ou arquivo e acessá-lo diretamente. | Backend/API e armazenamento de arquivos | Impedir acesso direto a arquivos privados de outros usuários. |

O armazenamento de arquivos deverá diferenciar os arquivos públicos dos arquivos privados relacionados às comissões. As obras publicadas no portfólio podem ser disponibilizadas publicamente, enquanto arquivos de referência e entregas de uma comissão devem possuir acesso restrito aos usuários envolvidos na contratação.

A solicitação de um arquivo privado deverá seguir o fluxo:

```text
Usuário → Backend/API → Autorização
                           ↓
                  Verificar propriedade
                  ou permissão de acesso
                           ↓
                    Permitir / Recusar
                           ↓
                  Armazenamento de arquivos
```

### Decisão 3 — Rate limiting no serviço de autenticação

| **Decisão** | **Risco tratado** | **Justificativa** | **Componente afetado** | **Resultado esperado** |
| :--- | :--- | :--- | :--- | :--- |
| Aplicar rate limiting às operações de login e recuperação de conta, considerando critérios como conta e origem da requisição, com registro das tentativas excedentes. | **R21** | Operações de autenticação podem ser exploradas por bots para gerar grande quantidade de requisições ou tentar comprometer contas. O controle reduz a capacidade de automação e limita o impacto sobre o serviço. | Serviço de autenticação, API e monitoramento | Reduzir ataques automatizados e evitar que tentativas excessivas comprometam a disponibilidade do serviço. |

O serviço de autenticação deverá limitar a quantidade de requisições realizadas em um determinado intervalo de tempo. Quando o limite for excedido, novas requisições poderão ser temporariamente bloqueadas ou retardadas.

O fluxo simplificado será:

Usuário → Serviço de Autenticação → Rate Limiting

Dentro do limite:
- Processar autenticação;
- Registrar a operação nos logs.

Limite excedido:
- Bloquear ou retardar a requisição;
- Registrar a tentativa nos logs;
- Permitir o monitoramento de possíveis comportamentos automatizados.

Além de limitar as requisições, as tentativas excedentes deverão ser registradas para permitir o monitoramento de possíveis comportamentos automatizados ou ataques.

Essa decisão trata diretamente o risco **R21 – Sobrecarga por login ou recuperação automatizada**, reduzindo tanto a possibilidade de abuso do serviço quanto o impacto de grandes quantidades de requisições sobre o serviço de autenticação.
