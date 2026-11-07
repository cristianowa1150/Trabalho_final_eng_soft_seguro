# Tutorial de instalação e execução do OWASP ZAP no Fedora

Este tutorial descreve o procedimento usado para executar o OWASP ZAP contra a
aplicação Artifício em ambiente local autorizado. O procedimento usa a imagem
oficial do ZAP no Docker, evitando a necessidade de instalar o ZAP diretamente
no Fedora.

> Execute os testes somente contra aplicações próprias ou para as quais você
> tenha autorização explícita. O `zap-baseline.py` realiza análise passiva e
> rastreamento básico; ele não substitui um teste de intrusão completo.

## 1. Pré-requisitos

Abra um terminal no Fedora e instale o Java, o Git e o Docker:

```bash
sudo dnf install -y java-25-openjdk-devel git
sudo dnf install -y docker
```

Inicie o serviço do Docker e configure o usuário atual para executar comandos
Docker sem `sudo`:

```bash
sudo systemctl enable --now docker
sudo usermod -aG docker "$USER"
```

Para aplicar a alteração do grupo na sessão atual, execute:

```bash
newgrp docker
```

Se necessário, encerre a sessão e entre novamente no Fedora. Confirme que o
Docker está acessível:

```bash
docker ps
```

## 2. Obter e preparar a aplicação

Clone o repositório e entre no diretório da aplicação:

```bash
git clone https://github.com/cristianowa1150/Trabalho_final_eng_soft_seguro.git
cd Trabalho_final_eng_soft_seguro/codigo/artificio
```

Gere o JAR executável usando o Maven Wrapper incluído no projeto:

```bash
./mvnw clean package -DskipTests
```

O arquivo gerado será:

```text
target/artificio-1.0.0.jar
```

Inicie a aplicação em um terminal separado:

```bash
java -jar target/artificio-1.0.0.jar
```

Por padrão, a aplicação ficará disponível em
`http://127.0.0.1:8080`. Mantenha esse terminal aberto durante o teste.

Antes de executar o ZAP, confirme que a aplicação responde:

```bash
curl -I http://127.0.0.1:8080
```

## 3. Baixar a imagem do OWASP ZAP

Volte para a raiz do repositório e baixe a imagem oficial:

```bash
cd ../..
docker pull zaproxy/zap-stable
```

## 4. Executar o scan baseline

Crie os relatórios diretamente no diretório de evidências:

```bash
docker run --rm --network host \
  -v "$PWD/evidencias/etapa-5:/zap/wrk:rw" \
  zaproxy/zap-stable zap-baseline.py \
  -t http://127.0.0.1:8080 \
  -r relatorio-zap.html \
  -J relatorio-zap.json
```

O parâmetro `--network host` permite que o container acesse a aplicação local
na porta `8080`. O volume monta `evidencias/etapa-5` em `/zap/wrk`, que é o
diretório de trabalho do ZAP; por isso os arquivos de saída são criados no
repositório.

Ao terminar, verifique os relatórios:

```bash
ls -lh evidencias/etapa-5/relatorio-zap.html \
       evidencias/etapa-5/relatorio-zap.json
```

Abra o relatório HTML em um navegador:

```bash
xdg-open evidencias/etapa-5/relatorio-zap.html
```

## 5. Encerrar a aplicação

No terminal onde o JAR está rodando, pressione `Ctrl+C`. O container do ZAP é
removido automaticamente ao finalizar porque o comando usa `--rm`.

## Observações

- O scan deve ser executado com a aplicação local em funcionamento.
- Os relatórios produzidos neste procedimento devem ser mantidos em
  `evidencias/etapa-5/`.
- Para reproduzir os testes da etapa do projeto, consulte também o
  [relatório da Etapa 5](Etapa%205/etapa5.md).
