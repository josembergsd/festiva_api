# Tasks - Gerenciamento de eventos

| Task | Descrição | Requisitos | Dependências |
|---|---|---|---|
| T001 | Criar projeto Maven, configurações, containers e migrações | REQ-005 | - |
| T002 | Implementar domínio, casos de uso e persistência JDBC de eventos | REQ-002, REQ-003, REQ-004 | T001 |
| T003 | Implementar registro, login e segurança JWT | REQ-001, REQ-003 | T001 |
| T004 | Implementar endpoints REST, validação, erros e OpenAPI | REQ-001 a REQ-005 | T002, T003 |
| T005 | Criar testes unitários e integração PostgreSQL, documentação e evidências | REQ-001 a REQ-005 | T001 a T004 |

## Matriz de rastreabilidade

| Requisito | Tasks | Evidência planejada |
|---|---|---|
| REQ-001 | T003, T004, T005 | testes de serviço e integração de autenticação |
| REQ-002 | T002, T004, T005 | testes de domínio, paginação e consulta HTTP |
| REQ-003 | T002, T003, T004, T005 | validação de domínio e autenticação HTTP |
| REQ-004 | T002, T004, T005 | teste de cancelamento lógico |
| REQ-005 | T001, T004, T005 | build, migrations, documentação e health endpoint |
