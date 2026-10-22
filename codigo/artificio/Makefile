.PHONY: help clean test tests deps run

help: ## Exibe os comandos disponíveis
	@echo "Comandos disponíveis no Artifício:"
	@echo "  make run    - Inicia a aplicação localmente (Spring Boot)"
	@echo "  make test   - Executa a suíte de testes automatizados"
	@echo "  make clean  - Limpa artefatos de build (pasta target/)"
	@echo "  make deps   - Baixa e resolve todas as dependências do projeto"

clean: ## Limpa artefatos gerados pelo build
	./mvnw clean

test: ## Executa os testes automatizados
	./mvnw test

deps: ## Resolve e baixa dependências e plugins
	./mvnw dependency:resolve dependency:resolve-plugins

run: ## Inicia o servidor de desenvolvimento local
	./mvnw spring-boot:run
