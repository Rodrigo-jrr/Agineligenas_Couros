# Contrato de API - Agineligenas Couros (Grupo do Couro) para Sapatos

Este pacote e o contrato B2B oficial entre o backend Agineligenas Couros e o cliente **Sapatos**. Contem a colecao Postman pronta para uso e o resumo do contrato abaixo. A spec OpenAPI completa e interativa fica em `{baseUrl}/swagger-ui.html` (JSON em `{baseUrl}/v3/api-docs`).

## Autenticacao

- Header: `X-API-Key: <sua chave>`
- Sua chave e o valor configurado no backend em `SAPATOS_API_KEY` (nunca trafega por outro canal que nao este).
- Sem header ou com chave invalida: `401 Unauthorized`.
- Com chave valida mas sem permissao para a rota: `403 Forbidden` (nao se aplica a Sapatos hoje - sua chave tem acesso a todas as rotas abaixo).

Formato de erro (`ApiError`), igual para 401 e 403:
```json
{
  "timestamp": "2026-10-15T12:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "...",
  "path": "/api/v1/produtos"
}
```

## Rotas liberadas para Sapatos

| Rota | Authority exigida | O que retorna |
|---|---|---|
| `GET /api/v1/produtos` | `PRODUTO_READ` | Pagina de produtos (`content`, `page`), filtravel por `?categoriaId=` |
| `GET /api/v1/produtos/{id}` | `PRODUTO_READ` | Um produto |
| `GET /api/v1/estoque/{id}` | `ESTOQUE_READ` | Estoque atual do produto |

### `ProdutoDTO`
```json
{
  "id": 1,
  "nome": "string",
  "descricao": "string | null",
  "preco": "12.50",
  "quantidadeDisponivel": 42,
  "unidadeMedida": "string | null",
  "fornecedorId": 3
}
```
`preco`, quando presente, sempre com 2 casas decimais.

### `EstoqueDTO`
```json
{
  "produtoId": 1,
  "quantidadeDisponivel": 42,
  "atualizadoEm": "2026-10-15T09:00:00-03:00"
}
```
`atualizadoEm` sempre no horario de Brasilia, com offset explicito.

## Como usar a colecao

1. Postman > Import > `collection.json` + `environment.json` (desta pasta).
2. Selecione o ambiente "Agineligenas Couros - Sapatos".
3. Preencha a variavel `apiKey` com o valor real da sua chave (nunca commitar esse valor em nenhum repositorio).
4. Ajuste `baseUrl` se o endpoint de producao/homologacao for diferente de `http://localhost:8080`.
5. "Run collection" roda as 4 requests (3 rotas liberadas + 1 exemplo de erro 401) e mostra os testes de contrato passando.

## Observacoes

- Este e um contrato backend-to-backend: nao chame estas rotas direto do navegador (sem CORS liberado para `/api/v1/**` fora de `/public`).
- Qualquer mudanca de contrato (novos campos, novas rotas, mudanca de authority) sera comunicada antes de ir para producao.
