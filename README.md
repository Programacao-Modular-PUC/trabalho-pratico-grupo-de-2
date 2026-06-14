# Sistema de Hospedagem — Maraú/BA

API REST em Spring Boot com protótipo web para gestão de residências, quartos, clientes e aluguéis.

## Pré-requisitos

- **Java 17** ou superior
- **MySQL** em execução (porta padrão `3306`)
- Conexão com a internet na primeira execução (o Maven Wrapper baixa as dependências automaticamente)

Não é necessário instalar o Maven separadamente: o projeto inclui o wrapper (`mvnw` / `mvnw.cmd`).

## Estrutura do repositório

```
trabalho-pratico-grupo-de-2/
├── backend/          # API Spring Boot + testes JUnit
├── web-prototipo/    # Interface HTML/JS (servida pelo backend)
└── README.md
```

## Configuração

### 1. Banco de dados MySQL

O sistema usa o banco `hospedagem_pm`. Ele é criado automaticamente na primeira execução, desde que o usuário MySQL tenha permissão para isso.

As configurações ficam em `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospedagem_pm?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
spring.datasource.username=root
spring.datasource.password=root1234
server.port=8080
```

Ajuste `username` e `password` conforme o seu ambiente local.

### 2. Porta da aplicação

Por padrão, a API e o protótipo web rodam em **http://localhost:8080**. Para alterar, edite `server.port` no `application.properties`.

## Como rodar o sistema

Abra um terminal na pasta `backend` e execute:

**Windows (PowerShell ou CMD):**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
cd backend
./mvnw spring-boot:run
```

Aguarde a mensagem indicando que a aplicação subiu. Em seguida:

| Recurso | URL |
|---------|-----|
| Protótipo web (recomendado) | http://localhost:8080/index.html |
| Residências | http://localhost:8080/residencias.html |
| Reserva / Aluguel | http://localhost:8080/reserva.html |
| Recibo | http://localhost:8080/recibo.html |

O protótipo é empacotado junto com o backend para evitar problemas de CORS no navegador.

### Endpoints da API

| Recurso | Base |
|---------|------|
| Clientes | `GET/POST/PUT/DELETE /clientes` |
| Residências | `GET/POST/PUT/DELETE /residencias` |
| Quartos | `GET/POST/DELETE /quartos` |
| Aluguéis | `GET/POST/DELETE /alugueis` |
| Cancelar aluguel | `POST /alugueis/{id}/cancelamento` |
| Aluguéis de um cliente | `GET /clientes/{id}/alugueis` |

Exemplo de verificação rápida:

```powershell
curl http://localhost:8080/clientes
```

## Testes e relatório JUnit

O projeto possui testes automatizados com **JUnit 5** (via `spring-boot-starter-test`). As classes de teste ficam em `backend/src/test/java/`.

### Executar os testes

Na pasta `backend`:

**Windows:**

```powershell
cd backend
.\mvnw.cmd test
```

**Linux / macOS:**

```bash
cd backend
./mvnw test
```

Se tudo estiver correto, o terminal exibirá um resumo semelhante a:

```
Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Gerar o relatório de testes

O relatório é gerado automaticamente ao rodar `mvn test`, pois o `pom.xml` já inclui o plugin `maven-surefire-report-plugin`.

Após a execução, os arquivos ficam em:

| Tipo | Caminho |
|------|---------|
| Relatório HTML (principal) | `backend/target/site/surefire-report.html` |
| Relatórios XML | `backend/target/surefire-reports/*.xml` |
| Resumo em texto | `backend/target/surefire-reports/*.txt` |

Abra o relatório HTML no navegador:

**Windows:**

```powershell
start backend\target\site\surefire-report.html
```

**Linux:**

```bash
xdg-open backend/target/site/surefire-report.html
```

**macOS:**

```bash
open backend/target/site/surefire-report.html
```

> **Observação:** a pasta `backend/target/` é gerada automaticamente pelo Maven e não deve ser versionada no Git. Para entregar o relatório, copie o arquivo `surefire-report.html` para fora de `target/` (por exemplo, `docs/relatorio-testes-junit.html`).

### Classes de teste incluídas

- `QuartoCalculoDiariaTest` — cálculo de diárias por tipo de quarto
- `QuartoValidacaoTest` — validação de capacidade e berço
- `AluguelServiceTest` — criação, cancelamento e disponibilidade de aluguéis
- `AluguelServiceValidacaoDatasTest` — validação de datas do aluguel

## Solução de problemas

| Problema | Possível causa / solução |
|----------|--------------------------|
| Erro de conexão com MySQL | Verifique se o MySQL está rodando e se usuário/senha em `application.properties` estão corretos |
| Porta 8080 em uso | Altere `server.port` ou encerre o processo que ocupa a porta |
| `mvn` não reconhecido | Use `.\mvnw.cmd` (Windows) ou `./mvnw` (Linux/macOS) em vez de `mvn` |
| Página web não carrega dados | Confirme que o backend está ativo em http://localhost:8080 |
| Testes falham | Rode `.\mvnw.cmd test` e verifique o erro no terminal ou em `target/surefire-reports/` |

## Tecnologias

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL
- JUnit 5 + Mockito
- Maven
