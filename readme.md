http://localhost:8080/api/v1/items?page=0&size=10


curl -X POST http://localhost:8080/api/v1/items \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook Dell",
    "descricao": "Laptop com 16GB RAM e SSD 512GB",
    "status": "ATIVO",
    "preco": 4500.00,
    "categoria": "ELETRONICOS"
  }'


curl -X GET http://localhost:8080/api/v1/items


curl -X GET http://localhost:8080/api/v1/items/1


curl -X PUT http://localhost:8080/api/v1/items/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Notebook Dell Inspiron",
    "descricao": "Atualizado com 32GB RAM",
    "status": "ATIVO",
    "preco": 4800.00,
    "categoria": "ELETRONICOS"
  }'


curl -X DELETE http://localhost:8080/api/v1/items/1



chmod +x crud_test.sh
./crud_test.sh


mvn test
# roda testes e gera relatório JaCoCo.

mvn verify
# roda testes + verifica se a cobertura mínima foi atendida (e quebra se não).

mvn clean test
# Rodar os testes e gerar o relatório

# rodar todos os testes (unitários + integração)
mvn clean verify

# Executar somente testes de integração
mvn -Dtest=*IT test

# rodar apenas uma classe específica:
mvn -Dtest=ItemControllerIT test


Isso vai gerar os arquivos de cobertura:
target/jacoco.exec (binário)
target/site/jacoco/index.html (relatório em HTML)

# Para abrir o relatório:
xdg-open target/site/jacoco/index.html


mvn spring-boot:run


docker compose up --build

curl http://localhost:8080/api/v1/hello
curl http://localhost:8080/api/v1/items

docker compose down


# build da imagem
docker build -t java-render-api:latest .

# subir o container
docker run --rm -p 8080:8080 --name java-render-api java-render-api:latest

