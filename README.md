# 🗳️ Sistema de Votação em Assembleias Cooperativas

Solução fullstack para cooperativas composta por:
- **Back-end em Java 17 / Spring Boot 3** estruturado no padrão clássico **MVC (Model-View-Controller / Service / Repository)**.
- **Front-end em React** com dashboard em tempo real, cadastro de pautas, abertura de sessões, terminal de votação e apuração de resultados com gerador de CPF válido/inválido para testes.
- **Persistência Confiável**: H2 em arquivo local (`./data/votacao_db.mv.db`) e PostgreSQL via Docker Compose, com migrações versionadas via **Flyway** (sem perda de votos após reiniciar a aplicação).

---

## 📌 Sumário
- [Arquitetura Java no Padrão MVC](#-arquitetura-java-no-padrão-mvc)
- [Front-end em React](#-front-end-em-react)
- [Padrões de Projeto (Design Patterns)](#-padrões-de-projeto-design-patterns)
- [Persistência Confiável (Sobrevive a Restarts)](#-persistência-confiável-sobrevive-a-restarts)
- [Tarefas Bônus Implementadas](#-tarefas-bônus-implementadas)
- [Documentação Swagger / OpenAPI](#-documentação-swagger--openapi)
- [Como Executar o Projeto](#-como-executar-o-projeto)
- [Testes Automatizados](#-testes-automatizados)

---

## 🏛️ Arquitetura Java no Padrão MVC

O backend Java segue a organização clássica do Spring MVC:

```
src/main/java/com/cooperativa/votacao/
├── config/                      # Configurações de CORS, OpenAPI, RestClient e Properties
│   ├── CorsConfig.java          # Habilita CORS para o Front-end React
│   ├── OpenApiConfig.java       # Configuração Swagger 3
│   ├── IntegracaoProperties.java
│   └── RestClientConfig.java
│
├── controller/                  # Controladores Spring MVC / REST Controllers
│   ├── PautaController.java     # /api/v1/pautas
│   ├── SessaoVotacaoController.java # /api/v1/sessoes
│   ├── VotoController.java      # /api/v1/pautas/{id}/votos
│   └── ResultadoController.java # /api/v1/pautas/{id}/resultado
│
├── service/                     # Camada de Negócio e Serviços
│   ├── PautaService.java
│   ├── SessaoVotacaoService.java
│   ├── VotoService.java
│   ├── ResultadoService.java
│   ├── UserInfoService.java
│   ├── strategy/                # Strategy Pattern para validações desacopladas
│   ├── client/                  # Adapter Pattern para o validador de CPF externo
│   └── event/                   # Observer Pattern para auditoria assíncrona
│
├── repository/                  # Camada de Acesso a Dados (Spring Data JPA)
│   ├── PautaRepository.java
│   ├── SessaoVotacaoRepository.java
│   ├── VotoRepository.java
│   └── dto/VotoContagemProjection.java # Projeção agregada de alta performance
│
├── model/                       # Modelos de Domínio e Contratos
│   ├── entity/                  # Pauta, SessaoVotacao, Voto
│   ├── enums/                   # OpcaoVoto, StatusSessao, ResultadoVotacao, StatusAssociadoVoto
│   ├── valueobject/             # Cpf (validação oficial e máscara)
│   └── dto/                     # Requests e Responses da API
│
└── exception/                   # Tratamento Centralizado de Erros
    ├── GlobalExceptionHandler.java
    ├── ApiErrorResponse.java
    └── (Exceções específicas de negócio)
```

---

## ⚛️ Front-end em React

O front-end em **React** está pronto e integrado ao Spring Boot:
- **Disponibilidade Imediata**: Ao iniciar a aplicação Java (`mvn spring-boot:run` ou `docker-compose up`), basta abrir **[http://localhost:8080/](http://localhost:8080/)** no navegador para utilizar a interface React!
- **Recursos da Interface**:
  1. **📊 Painel de Pautas**: Listagem em cards com status da sessão, cronômetro e consulta de resultado.
  2. **➕ Nova Pauta & ⚡ Abertura de Sessão**: Modais com validações e controle de tempo da votação em minutos.
  3. **🗳️ Terminal de Votação**: Interface rápida para associados votarem ('Sim'/'Não') com botões utilitários para gerar CPF Válido ou Inválido.
  4. **📊 Apuração em Tempo Real**: Consulta instantânea com contabilização de votos e deliberação final (Aprovada/Reprovada/Empate).

---

## 🎯 Padrões de Projeto (Design Patterns)

1. **Strategy Pattern (`ValidadorVotoStrategy`)**:
   - `ValidadorSessaoAbertaStrategy` (ordem 1)
   - `ValidadorVotoUnicoStrategy` (ordem 2)
   - `ValidadorCpfHabilitadoStrategy` (ordem 3)
2. **Adapter Pattern (`UserInfoClientAdapter`)**:
   - Isola o serviço externo de validação de CPF (`https://user-info.herokuapp.com/users/{cpf}`).
3. **Observer Pattern (`VotacaoEventListener`)**:
   - Publicação assíncrona de eventos de domínio (`SessaoAbertaEvent`, `VotoComputadoEvent`).
4. **Value Object (`Cpf`)**:
   - Encapsulamento de regras do algoritmo de CPF e imutabilidade.

---

## 💾 Persistência Confiável (Sobrevive a Restarts)

- **H2 Persistente em Arquivo (Padrão / Local)**:
  - Salva em disco em `./data/votacao_db.mv.db`. Ao reiniciar a aplicação, **todas as pautas e votos permanecem salvos**.
- **PostgreSQL (Docker Compose)**:
  - Disponível via `docker-compose up --build`.
- **Flyway Migrations**:
  - Script versionado em `src/main/resources/db/migration/V1__init_schema.sql` com constraints de unicidade (`UNIQUE (sessao_id, cpf_associado)`), protegendo contra duplicidade de votos mesmo em concorrência extrema.

---

## 🎁 Tarefas Bônus Implementadas

1. **Bônus 1 - Integração com Sistemas Externos**:
   - Validador de CPF em `https://user-info.herokuapp.com/users/{cpf}` com tratamento de 404 (inválido), `ABLE_TO_VOTE` / `UNABLE_TO_VOTE` e fallback resiliente configurável.
2. **Bônus 2 - Performance e Alta Concorrência**:
   - Contabilização agregada em banco de dados (`GROUP BY`) via SQL indexado no `VotoRepository`.
   - Suporta centenas de milhares de votos sem sobrecarregar a memória da JVM.
   - Constraint de banco contra votos simultâneos duplicados.
3. **Bônus 3 - Versionamento da API**:
   - Versionamento explícito por URI (`/api/v1/...`).

---

## 📖 Documentação Swagger / OpenAPI

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🚀 Como Executar o Projeto

### 1. Executando Localmente (Java MVC + React Embutido)
```bash
mvn spring-boot:run
```
Acesse no navegador:
- Front-end React: **http://localhost:8080/**
- Swagger OpenAPI: **http://localhost:8080/swagger-ui.html**
- H2 Console: **http://localhost:8080/h2-console**

### 2. Executando com Docker Compose (PostgreSQL)
```bash
docker-compose up --build
```

### 3. 📱 Gerando o APK Android para Celular via Docker Compose
Para compilar o APK instalável do aplicativo para celular e salvá-lo **diretamente na pasta raiz do projeto**:
```bash
docker compose --profile apk run --rm apk-builder
```
*(Ou dê um duplo clique no arquivo `gerar-apk.bat` no Windows).*

O arquivo **`cooperativa-votacao.apk`** será gerado automaticamente na raiz do projeto (`c:\Projetos\Tarefa\cooperativa-votacao.apk`). Basta copiar para o seu celular Android e instalar!

---

## 🧪 Testes Automatizados

Para rodar todos os testes unitários e de integração:
```bash
mvn test
```
