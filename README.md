# Festiva Event API

API REST para cadastro e gerenciamento de eventos, implementada em Java 25 com Spring Boot 4.1.1 e arquitetura hexagonal.

## Requisitos

- JDK 25
- Maven 3.9+
- Docker com Docker Compose para executar a API e o PostgreSQL 17

## Executar com Docker Compose

1. Copie `.env.example` para `.env`.
2. Defina `DB_PASSWORD` e `JWT_SECRET_BASE64`. Gere uma chave com pelo menos 32 bytes aleatórios e codifique-a em Base64. Não use a chave de exemplo em ambientes reais.
3. Execute `docker compose up --build`.
4. A API estará em `http://localhost:8080`; Swagger UI em `/swagger-ui.html`; health check em `/actuator/health`.

O banco é inicializado pelo Flyway. O segredo JWT não possui valor padrão e não deve ser versionado.

## Swagger / OpenAPI

- Interface interativa: `http://localhost:8080/swagger-ui.html`
- Documento OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Autenticação na interface: execute `/api/v1/auth/register` ou `/api/v1/auth/login`, copie o `accessToken` retornado e informe-o em **Authorize**. O campo espera o token sem adicionar o prefixo `Bearer`.
- Os endpoints de consulta são públicos; endpoints de escrita de eventos exibem o requisito Bearer JWT no contrato.

## Executar localmente

Por padrão, a aplicação usa o Session pooler PostgreSQL do Supabase configurado em `application.yaml`, com SSL. Defina `DB_PASSWORD` e `JWT_SECRET_BASE64` como variáveis de ambiente antes de iniciar a aplicação. Não inclua a senha na URL nem no arquivo de configuração.

Para usar outro banco, defina também `DB_URL` e `DB_USER`. As variáveis precisam estar disponíveis no processo que inicia a aplicação; no IntelliJ, configure-as na Run Configuration.

O Flyway está desabilitado porque as tabelas devem ser criadas manualmente no Supabase. Para um banco novo e vazio, defina `SPRING_FLYWAY_ENABLED=true` para executar as migrations automaticamente, sem criar as tabelas manualmente.

## API

| Método | Caminho | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Registrar usuário e obter JWT |
| POST | `/api/v1/auth/login` | Público | Autenticar e obter JWT |
| GET | `/api/v1/events` | Público | Listar eventos com paginação e filtro opcional por status |
| GET | `/api/v1/events/{id}` | Público | Consultar evento |
| POST | `/api/v1/events` | JWT | Criar evento |
| PUT | `/api/v1/events/{id}` | JWT | Atualizar dados do evento |
| PATCH | `/api/v1/events/{id}/cancel` | JWT | Cancelar evento sem removê-lo |

O cadastro exige e-mail válido e senha com pelo menos 12 caracteres. As operações de escrita aceitam `Authorization: Bearer <token>`. Campos de evento: `title`, `description` (opcional), `startsAt`, `endsAt`, `venue` e `capacity`. A capacidade deve ser positiva e `endsAt` posterior a `startsAt`.

A listagem aceita `page` (base zero), `size` (1 a 100) e `status` (`SCHEDULED`, `CANCELLED` ou `COMPLETED`). Respostas inválidas usam `application/problem+json`.

## Verificação

- `mvn test`: testes unitários e de integração.
- Os testes PostgreSQL usam Testcontainers e são ignorados automaticamente quando Docker não está disponível.
- `mvn package`: compila e empacota a aplicação.

O projeto inclui um teste de integração que valida cadastro, login, autorização e persistência com PostgreSQL. Ele requer Docker.
