.PHONY: help clean test tests deps run

help: ## Exibe os comandos disponíveis
	@echo "Comandos disponíveis no Artifício:"
	@echo "  make run         - Inicia a aplicação localmente (Spring Boot)"
	@echo "  make test        - Executa todos os testes automatizados (276 testes)"
	@echo "  make test-base   - Executa os testes fundamentais de domínio e persistência"
	@echo "  make test-abuso  - Executa a suíte formal de casos de abuso (CA01 a CA27)"
	@echo "  make test-gemini - Executa a suíte de resiliência e estresse do Gemini"
	@echo "  make clean       - Limpa artefatos de build (pasta target/)"
	@echo "  make deps        - Baixa e resolve todas as dependências do projeto"

clean: ## Limpa artefatos gerados pelo build
	./mvnw clean

test: ## Executa todos os testes automatizados
	./mvnw test

test-base: ## Executa os testes fundamentais de domínio e persistência
	./mvnw test -Dtest="!CasosAbusoTests*,!Gemini*"

test-abuso: ## Executa a suíte formal de casos de abuso (CA01 a CA27)
	./mvnw test -Dtest=CasosAbusoTests

test-gemini: ## Executa a suíte de resiliência e estresse do Gemini
	./mvnw test -Dtest="Gemini*Tests"

deps: ## Resolve e baixa dependências e plugins
	./mvnw dependency:resolve dependency:resolve-plugins

run: ## Inicia o servidor de desenvolvimento local
	./mvnw spring-boot:run
