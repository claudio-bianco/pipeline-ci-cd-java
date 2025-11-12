#!/bin/bash

# Criar
curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{"nome":"Mouse Gamer","descricao":"RGB 16000 DPI","status":"ATIVO","preco":199.99,"categoria":"ACESSORIOS"}'
echo -e "\n---"

# Listar
curl -X GET http://localhost:8080/api/v1/items
echo -e "\n---"

# Buscar por ID
curl -X GET http://localhost:8080/api/v1/items/1
echo -e "\n---"

# Atualizar
curl -X PUT http://localhost:8080/api/v1/items/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Mouse Gamer Pro","descricao":"Atualizado com sensor novo","status":"ATIVO","preco":249.90,"categoria":"ACESSORIOS"}'
echo -e "\n---"

# Deletar
curl -X DELETE http://localhost:8080/api/v1/items/1

