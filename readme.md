# 🚀 RenderAPI — API REST Java com CI/CD, Testes e Deploy Automatizado

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=flat-square)
![Maven](https://img.shields.io/badge/Build-Maven-blue?style=flat-square)
![SonarCloud](https://img.shields.io/badge/Quality%20Gate-SonarCloud-yellow?style=flat-square)
![GitHub Actions](https://img.shields.io/badge/CI/CD-GitHub%20Actions-blueviolet?style=flat-square)
![Render](https://img.shields.io/badge/Deploy-Render.com-lightgrey?style=flat-square)

---

## 📘 Visão Geral

O **RenderAPI** é uma aplicação **Java Spring Boot 3** com um CRUD completo de produtos, instrumentada com **testes unitários, integração e funcionais**, **análise de qualidade via SonarCloud**, **pipeline CI/CD GitHub Actions** e **deploy automático no Render.com**.  

O projeto demonstra **boas práticas de Engenharia de Plataforma**, incluindo:
- Cobertura de código com **JaCoCo**
- Qualidade de código e análise estática com **SonarCloud**
- Testes **Cypress** para validação ponta a ponta
- **GitHub Actions** com gates automatizados e publicação controlada
- Deploy contínuo e versionado no **Render.com**

---

## 🧩 Stack Técnica

| Categoria | Ferramenta / Tecnologia |
|------------|------------------------|
| **Linguagem** | Java 17 |
| **Framework** | Spring Boot 3.3.5 |
| **Gerenciador de build** | Maven |
| **Testes Unitários** | JUnit 5 + Mockito |
| **Testes Funcionais** | Cypress |
| **Cobertura** | JaCoCo |
| **Qualidade de Código** | SonarCloud |
| **Pipeline CI/CD** | GitHub Actions |
| **Hospedagem / Deploy** | Render.com |
| **Monitoramento** | New Relic (opcional) |

---

## 📦 Estrutura do Projeto

```bash
renderapi/
├── src/
│   ├── main/java/com/claudiobianco/java/renderapi/
│   │   ├── RenderApiApplication.java         # Classe principal (Spring Boot)
│   │   ├── domain/
│   │   │   ├── ItemService.java              # Lógica de negócio (CRUD)
│   │   │   ├── ItemNotFoundException.java    # Exceção de domínio
│   │   └── web/
│   │       ├── ItemController.java           # Endpoints REST
│   │       ├── ApiExceptionHandler.java      # Handler global de erros
│   │       └── dto/                          # DTOs de requisição e resposta
│   └── resources/
│       └── static/                           # Frontend AngularJS opcional
│
├── src/test/java/com/claudiobianco/java/renderapi/
│   ├── domain/ItemServiceTest.java           # Testes unitários do domínio
│   ├── web/ItemControllerTest.java           # Testes do controller com MockMvc
│   └── web/ItemControllerIT.java             # Teste de integração completo
│
├── .github/workflows/pipeline.yml            # CI/CD com Sonar + Cypress + Render
├── Dockerfile                                # Build da aplicação
├── docker-compose.yml                        # Stack local com frontend
├── pom.xml                                   # Configuração Maven + JaCoCo
└── README.md
```

* * *

## ⚙️ Como Executar Localmente

### 🧱 Requisitos

* Java 17+
    
* Maven 3.9+
    
* Docker (opcional)
    
* Node.js 18+ (para rodar os testes Cypress)
    

### 🔧 Build e execução

```bash
# Clonar o repositório
git clone https://github.com/claudio-bianco/pipeline-ci-cd-java.git
cd pipeline-ci-cd-java

# Compilar e testar
mvn clean verify

# Executar a aplicação
mvn spring-boot:run
```

Acesse:  
👉 **http://localhost:8080/api/v1/items**

* * *

## ✅ Endpoints Principais

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `GET` | `/api/v1/items` | Lista itens com paginação |
| `GET` | `/api/v1/items/{id}` | Busca item por ID |
| `POST` | `/api/v1/items` | Cria novo item |
| `PUT` | `/api/v1/items/{id}` | Atualiza item existente |
| `DELETE` | `/api/v1/items/{id}` | Remove item |

### 🧪 Exemplo via `curl`

```bash
curl -X POST http://localhost:8080/api/v1/items \
-H "Content-Type: application/json" \
-d '{"nome":"Notebook","descricao":"Dell i7","status":"ATIVO","preco":5999.99,"categoria":"Eletrônicos"}'
```

* * *

## 🧪 Testes e Cobertura

### Testes unitários e integração

```bash
mvn test
mvn verify
```

### Relatório JaCoCo

```bash
xdg-open target/site/jacoco/index.html
```

### Testes funcionais Cypress

```bash
npx cypress run --config baseUrl=http://localhost:8080
```

* * *

## 🧰 Pipeline CI/CD (GitHub Actions)

O pipeline realiza automaticamente:

1. **Build + Testes unitários e integração**
    
2. **Análise estática + cobertura no SonarCloud**
    
3. **Testes funcionais com Cypress**
    
4. **Deploy no Render.com** _(somente se cobertura ≥ 80% e Quality Gate OK)_
    

### Workflow principal (`.github/workflows/pipeline.yml`)

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with: { fetch-depth: 0 }

      - name: Configurar Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Build e Testes
        run: mvn -B clean verify

      - name: SonarCloud
        run: |
          mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
            -Dsonar.projectKey=claudio-bianco_pipeline-ci-cd-java \
            -Dsonar.organization=claudiobianco \
            -Dsonar.qualitygate.wait=true \
            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

      - name: Testes funcionais (Cypress)
        run: |
          npx cypress run --config baseUrl=http://localhost:8080

      - name: Deploy no Render (condicional)
        if: success()
        run: curl -X POST https://api.render.com/deploy/srv-xxxx
```

* * *

## 📊 Métricas e Qualidade (SonarCloud)

[🔗 **Ver análise no SonarCloud**](https://sonarcloud.io/dashboard?id=claudio-bianco_pipeline-ci-cd-java)

| Métrica | Valor |
| --- | --- |
| **Cobertura** | 90%+ |
| **Quality Gate** | ✅ Passed |
| **Security / Reliability / Maintainability** | A / A / A |
| **Bugs / Vulnerabilities** | 0 / 0 |
| **Hotspots Reviewed** | 100% |

* * *

## 🌐 Deploy no Render.com

A aplicação é automaticamente publicada após aprovação do pipeline.

* URL pública:  
    👉 **https://renderapi.onrender.com/api/v1/items**
    

* * *

## 👨‍💻 Autor

**Claudio Bianco**  
🧠 _DevOps | Platform Engineer | SRE_  
🌐 github.com/claudio-bianco  
💼 linkedin.com/in/claudiobianco  
📧 claudiobianco.dev@gmail.com

* * *

## 🏁 Conclusão

Este projeto demonstra um ciclo **completo de entrega contínua (CI/CD)** aplicado a uma aplicação Java moderna, com:

* Qualidade garantida por **SonarCloud**
    
* Cobertura de testes controlada via **JaCoCo**
    
* Testes ponta a ponta via **Cypress**
    
* Deploy automatizado com **Render.com**
    

🧩 Ideal como **prova de conceito de automação DevOps e boas práticas de engenharia de plataforma.**

* * *

🧭 _“Infraestrutura, código e qualidade — tudo versionado, automatizado e monitorado.”_

