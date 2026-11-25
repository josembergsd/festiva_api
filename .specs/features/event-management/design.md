# Desenho: Gerenciamento de eventos

## Arquitetura

- `domain`: entidades, enums, validações e portas de persistência.
- `application`: casos de uso e portas de entrada.
- `adapters.in.web`: controllers, DTOs e mapeamento HTTP.
- `adapters.out.persistence`: implementações JDBC das portas.
- `infrastructure`: configuração Spring Security, JWT, OpenAPI e relógio.

## Contratos HTTP

- `POST /api/v1/auth/register`, `POST /api/v1/auth/login`
- `GET /api/v1/events?page=0&size=20&status=SCHEDULED`
- `GET /api/v1/events/{id}`
- `POST /api/v1/events`
- `PUT /api/v1/events/{id}`
- `PATCH /api/v1/events/{id}/cancel`

Leitura pública e escrita autenticada. Paginação inicia em zero e tamanho máximo é 100. Datas são ISO-8601 com offset; armazenamento é `TIMESTAMPTZ`.

OpenAPI 3 está disponível em `/v3/api-docs` e a interface Swagger UI em `/swagger-ui.html`. O esquema HTTP Bearer JWT é descrito para rotas de escrita; os controllers e modelos documentam operações, parâmetros, respostas, validações e exemplos.

## Persistência e segurança

PostgreSQL 17 com migrations Flyway e acesso exclusivamente via `JdbcTemplate`. JWT HS256 é configurado por segredo Base64 externo, mínimo de 32 bytes; senha é protegida por BCrypt. O segredo não tem fallback embutido.

## Decisões ainda abertas

Papéis/ownership, políticas de rate limiting, duração de retenção e políticas de operação em produção não foram definidos e não são inferidos nesta versão.
