# Artifício - Guia de Execução e Procedimentos do Ambiente

Este documento descreve os procedimentos de ambiente, pré-requisitos, comandos de compilação, execução, testes e rotinas de limpeza para o projeto **Artifício** (Spring Boot 4.x / Java 26).

---

## 1. Pré-requisitos de Ambiente

- **Sistema Operacional:** Linux, macOS ou Windows
- **JDK:** OpenJDK Eclipse Temurin (Adoptium) - Java 25 ou superior (JDK 26 recomendado)
- **Maven:** Utilizar o Maven Wrapper (`./mvnw`) fornecido na raiz do projeto.

---

## 2. Inicialização do Projeto (Spring Initializr)

O projeto foi gerado originalmente com as seguintes definições:

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 4.1.1
- **Packaging:** Jar
- **Java Version:** 25/26
- **Group:** `com.github.trabalhoengsoftseguro`
- **Artifact:** `artificio`
- **Dependências Iniciais:**
  - `spring-boot-starter-webmvc` (Spring Web)
  - `spring-boot-starter-thymeleaf` (Template Engine)
  - `spring-boot-starter-security` (Spring Security)
  - `spring-boot-starter-validation` (Bean Validation)
  - `spring-boot-starter-data-jpa` (Spring Data JPA)
  - `h2` (Banco de Dados H2 Runtime)
  - `spring-boot-h2console` (Console Web H2)

---

## 3. Comandos de Download e Compilação

Todos os comandos devem ser executados a partir do diretório do projeto (`codigo/artificio`):

```bash
cd codigo/artificio
```

### 3.1 Baixar Dependências
```bash
# Baixar e resolver todas as dependências do pom.xml
./mvnw dependency:resolve

# Baixar dependências e plugins para suporte offline
./mvnw dependency:go-offline
```

### 3.2 Forçar Atualização de Dependências (-U)
Para forçar a checagem de versões e atualizações remotas:
```bash
./mvnw clean compile -U
```

---

## 4. Execução da Aplicação

### 4.1 Modo Desenvolvimento (Direto pelo Maven)
```bash
./mvnw spring-boot:run
```
A aplicação estará disponível em `http://localhost:8080`.

### 4.2 Gerar Pacote JAR e Executar
```bash
# Compilar e empacotar (pulando testes)
./mvnw clean package -DskipTests

# Executar o arquivo JAR gerado
java -jar target/artificio-0.0.1-SNAPSHOT.jar
```

---

## 5. Execução de Testes Automatizados

```bash
# Rodar todos os testes unitários e de integração
./mvnw test

# Rodar testes gerando relatório detalhado
./mvnw test -Dtest=*Test
```

---

## 6. Procedimentos de Limpeza

### 6.1 Limpeza do Projeto Local (`target/`)
Remove os binários compilados e a pasta `target/`:
```bash
./mvnw clean
```

### 6.2 Limpeza Forçada do Cache Local do Maven (`~/.m2`)
Se houver corrupção de downloads ou necessidade de limpar artefatos baixados:

- **Limpar apenas os artefatos do projeto Artifício no repositório local:**
  ```bash
  rm -rf ~/.m2/repository/com/github/trabalhoengsoftseguro
  ```

- **Limpeza completa do repositório local Maven (atenção: exigirá novo download de todas as dependências):**
  ```bash
  rm -rf ~/.m2/repository/*
  ```

- **Forçar purga e re-download de dependências pelo próprio Maven:**
  ```bash
  ./mvnw dependency:purge-local-repository
  ```
