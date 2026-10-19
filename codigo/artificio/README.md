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
# Executa todos os testes automatizados
./mvnw test
```

Para rodar testes de uma classe ou pacote específico:
```bash
# Testes de autenticação e controle de acesso
./mvnw test -Dtest=AuthControllerTest

# Testes de validação de isolamento do perfil de produção
./mvnw test -Dtest=GlobalModelAdviceProdTest
```

---

## 🚀 3. Execução da Aplicação

### 3.1 Ambiente de Desenvolvimento / Avaliação (Padrão)

Neste modo, o banco de dados H2 é inicializado e populado automaticamente com massa de dados de teste (artistas, portfólio, modalidades e pedidos), e os facilitadores de login rápido e console H2 ficam ativos.

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

| Papel / Perfil | E-mail | Senha | Funcionalidades Disponíveis |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@artificio.local` | `senha123` | Painel de moderação (`/admin/usuarios`), auditoria de contas e promoção de clientes a artistas. |
| **Artista (Aquarela)** | `beatriz@artificio.local` | `senha123` | Painel do artista, upload de obras no portfólio, gestão de modalidades de preço e aceite de pedidos. |
| **Artista (Pixel Art)** | `carlos@artificio.local` | `senha123` | Vitrine pública, portfólio, envio de links de entrega final de encomendas. |
| **Cliente** | `lucas@artificio.local` | `senha123` | Solicitação de encomendas, briefing, simulação de pagamento PIX e avaliação com estrelas. |

> 💡 **Dica de usabilidade:** No modo de desenvolvimento, há um botão flutuante **`🔑 Contas de Teste`** no canto inferior da tela e botões de **`Preencher`** na tela de login (`/login`) para carregar as credenciais com 1 clique.

---

## 🧹 5. Rotinas de Limpeza

```bash
# Limpar artefatos de compilação da pasta target/
./mvnw clean

# Forçar purga e re-download de dependências do repositório local do Maven
./mvnw dependency:purge-local-repository
```
