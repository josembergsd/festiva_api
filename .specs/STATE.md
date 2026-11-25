# Estado das especificações

- Estado: PROPOSED, aguardando validação humana após implementação e testes.
- Projeto: Festiva Event API.
- Demanda: TASK, criar API REST para gerenciamento de eventos.
- Capability: TLC Spec-Driven Development, variante Bradesco, datada de 07/07/2026; aceita para este DELTA.
- Stack confirmada pelo documento-fonte: Java 25, Spring Boot 4.1.1, PostgreSQL 17, JdbcTemplate, Spring Security, JWT, Lombok, Flyway, Maven, Docker Compose, Testcontainers, JUnit 5, Mockito e OpenAPI 3.
- Escopo MVP confirmado: criar, listar, consultar, atualizar e cancelar eventos; leitura pública e escrita autenticada por JWT.
- Premissas explícitas: cadastro e login emitem tokens; qualquer conta autenticada pode escrever eventos; cancelamento é lógico; listagem padrão usa paginação; status inicial `SCHEDULED`.
- Fora do escopo atual: autorização por papéis/organizador, inscrição de participantes, pagamento, notificações, busca avançada e exclusão física.
- Limitação do ambiente: Docker e PostgreSQL local não foram encontrados; testes de integração PostgreSQL dependem de Docker.
