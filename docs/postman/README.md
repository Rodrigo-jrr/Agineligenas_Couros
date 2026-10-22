# Postman - Agineligenas Couros API v1

Colecao e ambiente para testar manualmente as 5 rotas GET de `/api/v1`
(produto/estoque, protegidas por API Key, e catalogo publico, sem auth).

## Arquivos

- `collection.json` - a colecao (5 rotas, cenarios 401/403/200 por rota protegida).
- `environment.json` - ambiente Postman com as variaveis usadas pela colecao.

## Como importar

1. Postman > Import > selecione `collection.json` e `environment.json` (ou arraste os dois arquivos).
2. No canto superior direito, selecione o ambiente "Agineligenas Couros - dev".
3. Clique no ambiente (icone de olho ou "Edit") e preencha as variaveis marcadas como `PREENCHA_AQUI...`:
   - `sapatosApiKey` -> valor real de `SAPATOS_API_KEY` usado para subir o backend.
   - `vacasApiKey` -> valor real de `VACAS_CONSUMER_API_KEY` usado para subir o backend (atencao: NAO e `VACAS_API_KEY`, que e outra chave, reservada para a Etapa 4).
4. Ajuste `baseUrl` se o backend nao estiver em `http://localhost:8080` (ex.: `http://localhost:8099` se voce subiu com `--server.port=8099`).
5. `produtoIdValido` ja vem como `1` (produto do seed inicial). Troque se necessario.

As chaves reais NUNCA ficam no `environment.json` versionado - os valores aqui sao so placeholders. Preencha localmente no Postman (ou use um ambiente pessoal nao versionado) e nunca commite o ambiente com as chaves reais preenchidas.

## Rodando

- Individualmente: abra qualquer request e clique "Send".
- Colecao inteira: botao direito na colecao > "Run collection" (Postman Runner) - roda todos os cenarios e mostra os `pm.test` passando/falhando.

## Pre-requisito

Backend rodando contra um MySQL real com o seed aplicado (ver `backend/README.md`), com `SAPATOS_API_KEY` e `VACAS_CONSUMER_API_KEY` configuradas nas variaveis de ambiente do backend - os mesmos valores devem ser preenchidos no ambiente Postman.
