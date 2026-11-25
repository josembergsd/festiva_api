# Feature: Gerenciamento de eventos

Estado: PROPOSED. A implementação e os testes ainda dependem de validação humana.

## REQ-001 - Cadastro e autenticação

Usuários podem registrar-se com e-mail e senha e autenticar-se para obter um JWT. E-mails são normalizados para minúsculas; senhas são armazenadas com hash BCrypt e exigem ao menos 12 caracteres.

Critérios: registro válido retorna token; duplicidade de e-mail retorna conflito; credenciais inválidas não autenticam; senha nunca é retornada nem armazenada em texto puro.

## REQ-002 - Consulta pública de eventos

Qualquer cliente pode listar eventos com paginação e filtro opcional por status, ou consultar um evento por UUID. Evento inexistente retorna 404.

## REQ-003 - Escrita autenticada

Usuário autenticado por JWT pode criar e atualizar eventos. Título, início, fim, local e capacidade são obrigatórios; capacidade é positiva e fim deve ocorrer depois do início.

## REQ-004 - Cancelamento

Usuário autenticado pode cancelar um evento. O evento permanece persistido com status `CANCELLED`; não há exclusão física.

## REQ-005 - Contrato e operação

Erros de validação usam Problem Details. A API expõe OpenAPI/Swagger, health check, migrações Flyway e configuração containerizada com PostgreSQL 17.
