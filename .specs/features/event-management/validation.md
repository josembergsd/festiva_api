# Validação

- Build: `mvn -B package`.
- Testes: `mvn -B test`.
- Testes de integração: PostgreSQL 17 via Testcontainers; requer Docker.
- Verificações HTTP de integração: registro/login, leitura pública, escrita sem token (401), escrita com token e cancelamento.
- OpenAPI: `/v3/api-docs`; Swagger UI: `/swagger-ui.html`.
- Health check: `/actuator/health`.
- Resultado local: `mvn -B package` concluiu com sucesso; testes unitários de domínio, autenticação e configuração OpenAPI aprovados.
- Limitação observada: Docker e PostgreSQL não estão instalados/disponíveis no ambiente atual; testes HTTP e verificação do documento Swagger em runtime são ignorados e permanecem pendentes.
