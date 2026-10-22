# Contrato de API - Agineligenas Couros (Grupo do Couro) para Vacas

Este pacote e o contrato B2B oficial entre o backend Agineligenas Couros e o cliente **Vacas**. Contem a colecao Postman pronta para uso e o resumo do contrato abaixo. A spec OpenAPI completa fica em `{baseUrl}/swagger-ui.html` (JSON em `{baseUrl}/v3/api-docs}`) - atencao: essa spec lista todas as rotas de `/api/v1/**` sem diferenciar por cliente, use este README/colecao para saber exatamente o que a sua chave pode acessar.

## Autenticacao

- Header: `X-API-Key: <sua chave>`
- Sua chave e o valor configurado no backend em `VACAS_CONSUMER_API_KEY` (atencao: nao e `VACAS_API_KEY`, reservada para uma integracao futura em sentido contrario).
- Sem header ou com chave invalida: `401 Unauthorized`.
- Com chave valida mas sem permissao para a rota (ex.: estoque, ver abaixo): `403 Forbidden`.

Formato de erro (`ApiError`), igual para 401 e 403:
```json
{
  "timestamp": "2026-10-15T12:00:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "...",
  "path": "/api/v1/estoque/1"
}
```

## Rotas liberadas para Vacas

| Rota | Authority exigida | O que retorna |
|---|---|---|
| `GET /api/v1/produtos` | `PRODUTO_READ` | Pagina de produtos (`content`, `page`), filtravel por `?categoriaId=` |
| `GET /api/v1/produtos/{id}` | `PRODUTO_READ` | Um produto |

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

## Rota NAO liberada para Vacas

| Rota | Comportamento |
|---|---|
| `GET /api/v1/estoque/{id}` | Sua chave e valida mas nao tem a authority `ESTOQUE_READ` -> sempre `403 Forbidden`. Isso e esperado, nao e um bug. Se precisar desse dado no futuro, nos avise para liberarmos a authority. |

## Como usar a colecao

1. Postman > Import > `collection.json` + `environment.json` (desta pasta).
2. Selecione o ambiente "Agineligenas Couros - Vacas".
3. Preencha a variavel `apiKey` com o valor real da sua chave (nunca commitar esse valor em nenhum repositorio).
4. Ajuste `baseUrl` se o endpoint de producao/homologacao for diferente de `http://localhost:8080`.
5. "Run collection" roda as 4 requests (2 rotas liberadas + 1 exemplo do 403 em estoque + 1 exemplo de erro 401) e mostra os testes de contrato passando.

## Observacoes

- Este e um contrato backend-to-backend: nao chame estas rotas direto do navegador (sem CORS liberado para `/api/v1/**` fora de `/public`).
- Qualquer mudanca de contrato (novos campos, novas rotas, mudanca de authority) sera comunicada antes de ir para producao.
