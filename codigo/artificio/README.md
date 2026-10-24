# Artifício - Aplicação Spring Boot

Aplicação web desenvolvida para o trabalho final da disciplina de **Engenharia de Software Seguro** (PPGCO/UFU). O sistema implementa uma plataforma para catálogo de artistas, portfólios e gestão do ciclo completo de encomendas de arte (comissões).

---

## 🛠️ Tecnologias e Pré-requisitos

- **Linguagem:** Java 25 / 26 (OpenJDK Eclipse Temurin recomendado)
- **Framework:** Spring Boot 4.1.1
- **Módulos:** Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation, Thymeleaf
- **Banco de Dados:** H2 Database (em memória, autocontido para desenvolvimento e avaliação)
- **Build Tool:** Maven Wrapper (`./mvnw` já incluído no repositório)

> **Nota:** Todos os comandos abaixo devem ser executados a partir do diretório raiz da aplicação (`codigo/artificio`):
> ```bash
> cd codigo/artificio
> ```

---

## 📦 1. Compilação e Empacotamento

### Baixar Dependências
```bash
./mvnw dependency:resolve
```

### Compilar o Código-Fonte
```bash
./mvnw clean compile
```

### Gerar Pacote JAR
```bash
# Compilar, rodar testes e gerar o arquivo JAR executável
./mvnw clean package

# (Opcional) Gerar o JAR sem executar os testes:
./mvnw clean package -DskipTests
```
O artefato gerado estará em `target/artificio-0.0.1-SNAPSHOT.jar`.

---

## 🧪 2. Execução dos Testes Automatizados

A aplicação conta com uma suíte de testes unitários (Mockito) e testes de integração de controllers (MockMvc e Spring Security Test):

```bash
# Executa todos os testes automatizados (276 testes)
make test
# ou: ./mvnw test
```

Para rodar suítes específicas via `Makefile` ou Maven:
```bash
# Testes fundamentais de domínio e persistência (107 testes)
make test-base
# ou: ./mvnw test -Dtest="!CasosAbusoTests*,!Gemini*"

# Suíte formal de Casos de Abuso (CA01 a CA27 - 42 testes)
make test-abuso
# ou: ./mvnw test -Dtest=CasosAbusoTests

# Suíte modular de resiliência e estresse do Gemini (127 testes divididos em 6 classes)
make test-gemini
# ou: ./mvnw test -Dtest="Gemini*Tests"
```

A suíte Gemini está modularizada em 6 classes especializadas no pacote `com.github.trabalhoengsoftseguro.artificio.gemini`:
- `GeminiAutenticacaoControleAcessoTests`: RBAC, prevenção de escalada vertical e CSRF (23 testes)
- `GeminiComissoesMaquinaEstadosTests`: Integridade do fluxo de estados de comissões (23 testes)
- `GeminiIdorIntegridadeDadosTests`: Proteção anti-IDOR/BOLA e isolamento de posse (18 testes)
- `GeminiUploadSegurancaMidiaTests`: Magic bytes, upload malicioso e proteção SVG (16 testes)
- `GeminiValidacaoResilienciaHttpTests`: Entradas extremas, resiliência HTTP e GlobalExceptionHandler (32 testes)
- `GeminiWebAuditoriaTests`: Integridade da trilha forense do Envers e rotas administrativas (15 testes)

> 📋 Para a listagem completa, detalhamento dos 276 testes e mapeamento de cada caso de abuso, consulte o [Relatório Geral de Testes e Casos de Abuso](../../docs/relatorio_testes_casos_de_abuso.md).

---

## 🚀 3. Execução da Aplicação

### 3.1 Ambiente de Desenvolvimento / Avaliação (Padrão)

Neste modo, o banco de dados H2 é inicializado e populado automaticamente com massa de dados de teste (artistas, portfólio, modalidades e pedidos), e os facilitadores de login rápido e console H2 ficam ativos.

As tabelas de negócio residem no schema padrão `PUBLIC`, enquanto todas as tabelas de auditoria forense do Hibernate Envers (`REVINFO` e `*_AUD`) são segregadas automaticamente no schema isolado `AUDITORIA`.

**Opção A — Executar diretamente pelo Maven:**
```bash
./mvnw spring-boot:run
```

**Opção B — Executar a partir do JAR compilado:**
```bash
java -jar target/artificio-0.0.1-SNAPSHOT.jar
```

- **URL da Aplicação:** [http://localhost:8080](http://localhost:8080)
- **Console H2 Web:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:artificiodb`
  - Usuário: `sa`
  - Senha: *(em branco)*
  - Schemas disponíveis: `PUBLIC` (negócio) e `AUDITORIA` (trilha forense e tabelas `*_AUD`)

---

### 3.2 Ambiente de Produção (`prod`)

No perfil de produção, os facilitadores de avaliação são desativados automaticamente por questões de segurança (nenhum modal de teste ou botão de preenchimento de login é renderizado no HTML) e o console H2 é bloqueado.

**Opção A — Executar via Maven com Perfil `prod`:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**Opção B — Executar o JAR com propriedade de sistema:**
```bash
java -jar -Dspring.profiles.active=prod target/artificio-0.0.1-SNAPSHOT.jar
```

**Opção C — Executar via variável de ambiente:**
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar target/artificio-0.0.1-SNAPSHOT.jar
```

---

## 🔑 4. Contas Pré-configuradas para Testes (Modo Dev)

Durante a inicialização em modo de desenvolvimento, o sistema cria as seguintes contas para permitir a avaliação de todos os papéis de acesso:

| Papel / Perfil | Nome | E-mail | Senha | Funcionalidades / Escopo |
| :--- | :--- | :--- | :--- | :--- |
| **Administrador** | Administrador da Plataforma | `admin@artificio.local` | `Admin#2026` | Supervisão (`/admin/usuarios`), auditoria de contas e promoção de clientes a artistas. |
| **Artista (Aquarela)** | Anna Letycia Fernandes Reis | `anna@artificio.local` | `Anna@Arte2026` | Painel do artista, portfólio botânico/aquarela, gestão de modalidades e aceite de pedidos. |
| **Artista (Pixel Art)** | Cristiano Silveira Silva | `cristiano@artificio.local` | `Cris#Pixel2026` | Vitrine de pixel art, retrogaming, sprites e envio de links de entrega final. |
| **Artista (Vetor)** | Marina Duarte *(Artista Extra)* | `marina@artificio.local` | `Marina*Vetor2026` | Portfólio vetorial, design de personagens e capas. |
| **Cliente** | Eduardo Rolim | `eduardo@artificio.local` | `Eduardo&Cliente2026` | Solicitação de encomendas, briefing, simulação de pagamento PIX e avaliação com estrelas. |
| **Cliente** | Luan Diniz | `luan@artificio.local` | `Luan$Cliente2026` | Contratação de comissões, acompanhamento de pedidos e aprovação de entregas. |

> 💡 **Dica de usabilidade:** No modo de desenvolvimento, há um botão flutuante **`🔑 Contas de Teste`** no canto inferior da tela e botões de **`Preencher`** na tela de login (`/login`) para carregar instantaneamente usuário e senha sem precisar digitar.


---

## 🧹 5. Rotinas de Limpeza

```bash
# Limpar artefatos de compilação da pasta target/
./mvnw clean

# Forçar purga e re-download de dependências do repositório local do Maven
./mvnw dependency:purge-local-repository
```
