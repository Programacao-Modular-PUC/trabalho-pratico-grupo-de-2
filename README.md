# Sistema de Hospedagem — Maraú/BA

API REST em Spring Boot com protótipo web para gestão de residências, quartos, clientes e aluguéis.

## Documento Técnico - Sprint 4

Esta sprint evolui o sistema de hospedagem com os padrões **Strategy**, **Observer** e **Singleton**, aplicados ao fluxo de criação e cancelamento de aluguéis.

### Funcionalidades Escolhidas

- **Opção 1 - Sistema de Tarifação Flexível:** permite aplicar diferentes regras de diária, como tarifa regular, alta temporada e feriado.
- **Opção 3 - Central de Notificações:** notifica componentes do sistema quando uma reserva é criada ou cancelada.

### Solução Proposta

#### Strategy - Tarifação Flexível

O **Strategy** foi usado para separar as regras de tarifa do serviço de aluguel. O quarto calcula a diária base e, em seguida, `TarifaStrategyResolver` escolhe a estratégia adequada ao período da reserva.

Classes principais:

- `TarifaStrategy`: contrato das regras de tarifa.
- `TarifaRegularStrategy`: mantém a diária base.
- `TarifaAltaTemporadaStrategy`: aplica 20% de acréscimo em janeiro, julho e dezembro.
- `TarifaFeriadoStrategy`: aplica 30% de acréscimo quando o período inclui feriado nacional cadastrado.
- `TarifaStrategyResolver`: seleciona a estratégia compatível.
- `ParametrosDiaria`: contém hóspedes, berço, data inicial e data final.

Fluxo implementado:

1. `AluguelService.criar` valida datas, cliente, quarto, disponibilidade e capacidade.
2. O quarto calcula a diária base.
3. `TarifaStrategyResolver` aplica a tarifa flexível.
4. O valor total é calculado pela diária final multiplicada pela quantidade de diárias.

Assim, novas regras de tarifa podem ser adicionadas criando novas implementações de `TarifaStrategy`.

#### Observer - Central de Notificações

O **Observer** foi usado para desacoplar o serviço de aluguel das ações executadas após eventos. `AluguelService` publica eventos, e os observers registrados reagem sem alterar o fluxo principal.

Classes principais:

- `EventoAluguel`: dados do evento.
- `TipoEventoAluguel`: tipos `CRIADO` e `CANCELADO`.
- `AluguelObserver`: contrato dos observadores.
- `NotificacaoClienteObserver`: registra notificação interna ao cliente.
- `AuditoriaAluguelObserver`: registra auditoria do evento.
- `GerenciadorNotificacoes`: gerencia e dispara os observers.

Fluxo implementado:

1. Ao criar uma reserva, o sistema publica o evento `CRIADO`.
2. Ao cancelar uma reserva, o sistema publica o evento `CANCELADO`.
3. `GerenciadorNotificacoes` repassa o evento aos observers registrados.

Com isso, novos canais como e-mail, SMS ou WhatsApp podem ser adicionados como novos observers.

### Justificativa da Escolha dos Padrões

- **Strategy:** adequado porque a tarifação possui regras alternativas de cálculo. Cada regra fica isolada, testável e fácil de substituir.
- **Observer:** adequado porque vários componentes podem reagir ao mesmo evento de reserva sem que `AluguelService` conheça todos eles.

### Singleton Obrigatório

O **Singleton** foi implementado em `GerenciadorNotificacoes`, que representa a central global de notificações do sistema. Ele precisa ter uma única instância para manter uma lista consistente de observers; caso houvesse várias instâncias, observers poderiam ser registrados em uma central diferente daquela usada para publicar eventos.

A classe possui construtor privado, instância estática `INSTANCIA`, método `getInstancia()` e métodos para registrar, remover e notificar observers. Os observers `NotificacaoClienteObserver` e `AuditoriaAluguelObserver` se registram nessa instância ao iniciar a aplicação.

### Benefícios Obtidos

- **Strategy:** facilita a criação de novas regras de tarifa sem alterar o fluxo principal de aluguel; reduz condicionais em `AluguelService`; deixa cada cálculo de tarifa isolado e testável.
- **Observer:** permite adicionar novos canais ou ações de notificação sem modificar a criação/cancelamento de reservas; reduz o acoplamento entre eventos de aluguel e ações secundárias; facilita expansão para e-mail, SMS ou WhatsApp.
- **Singleton:** garante uma única central de notificações compartilhada pelo sistema; evita registros duplicados ou inconsistentes de observers; centraliza o disparo de eventos em um ponto controlado.
- **Arquitetura geral:** melhora extensibilidade, manutenção, organização das responsabilidades e cobertura de testes.

### Demonstração das Funcionalidades

Para demonstrar a tarifação flexível, crie reservas em períodos diferentes:

- `2026-06-10`: tarifa regular.
- `2026-07-10`: alta temporada, com 20% de acréscimo.
- `2026-09-07`: feriado, com 30% de acréscimo.

Para demonstrar a central de notificações, crie ou cancele uma reserva e observe no terminal as mensagens geradas pelos observers.

Para demonstrar o Singleton, execute os testes automatizados; `GerenciadorNotificacoesTest` valida que `getInstancia()` sempre retorna o mesmo objeto.

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

Por padrão, a API e o protótipo web rodam em **[http://localhost:8080](http://localhost:8080)**. Para alterar, edite `server.port` no `application.properties`.

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


| Recurso                     | URL                                                                              |
| --------------------------- | -------------------------------------------------------------------------------- |
| Protótipo web (recomendado) | [http://localhost:8080/index.html](http://localhost:8080/index.html)             |
| Residências                 | [http://localhost:8080/residencias.html](http://localhost:8080/residencias.html) |
| Reserva / Aluguel           | [http://localhost:8080/reserva.html](http://localhost:8080/reserva.html)         |
| Recibo                      | [http://localhost:8080/recibo.html](http://localhost:8080/recibo.html)           |


O protótipo é empacotado junto com o backend para evitar problemas de CORS no navegador.

### Endpoints da API


| Recurso                | Base                               |
| ---------------------- | ---------------------------------- |
| Clientes               | `GET/POST/PUT/DELETE /clientes`    |
| Residências            | `GET/POST/PUT/DELETE /residencias` |
| Quartos                | `GET/POST/DELETE /quartos`         |
| Aluguéis               | `GET/POST/DELETE /alugueis`        |
| Cancelar aluguel       | `POST /alugueis/{id}/cancelamento` |
| Aluguéis de um cliente | `GET /clientes/{id}/alugueis`      |


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
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Gerar o relatório de testes

O relatório é gerado automaticamente ao rodar `mvn test`, pois o `pom.xml` já inclui o plugin `maven-surefire-report-plugin`.

Após a execução, os arquivos ficam em:


| Tipo                       | Caminho                                    |
| -------------------------- | ------------------------------------------ |
| Relatório HTML (principal) | `backend/target/site/surefire-report.html` |
| Relatórios XML             | `backend/target/surefire-reports/*.xml`    |
| Resumo em texto            | `backend/target/surefire-reports/*.txt`    |


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
- `TarifaStrategyResolverTest` — escolha e aplicação das estratégias de tarifa
- `GerenciadorNotificacoesTest` — validação do Singleton de notificações

## Solução de problemas


| Problema                     | Possível causa / solução                                                                        |
| ---------------------------- | ----------------------------------------------------------------------------------------------- |
| Erro de conexão com MySQL    | Verifique se o MySQL está rodando e se usuário/senha em `application.properties` estão corretos |
| Porta 8080 em uso            | Altere `server.port` ou encerre o processo que ocupa a porta                                    |
| `mvn` não reconhecido        | Use `.\mvnw.cmd` (Windows) ou `./mvnw` (Linux/macOS) em vez de `mvn`                            |
| Página web não carrega dados | Confirme que o backend está ativo em [http://localhost:8080](http://localhost:8080)             |
| Testes falham                | Rode `.\mvnw.cmd test` e verifique o erro no terminal ou em `target/surefire-reports/`          |


## Tecnologias

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL
- JUnit 5 + Mockito
- Maven

