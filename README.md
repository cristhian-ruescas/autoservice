<h1>🚗 Autoservice API</h1>

<p>
Backend para gerenciamento de oficina mecânica desenvolvido com
<b>Java 21</b> e <b>Spring Boot</b>.
</p>

<hr>

<h2>📦 Tecnologias utilizadas</h2>

<ul>
<li>Java 21</li>
<li>Spring Boot</li>
<li>Maven</li>
<li>Docker + Docker Compose</li>
<li>PostgreSQL</li>
<li>IntelliJ IDEA (recomendado)</li>
</ul>

<hr>

<h2>✅ Pré-requisitos</h2>

<p>Verifique se você possui instalado:</p>

<pre>
java -version
docker -v
docker-compose -v
</pre>

<hr>

<h2>🚀 Como executar o projeto</h2>

<h3>1️⃣ Subir o banco de dados</h3>

<pre>
docker-compose up -d
</pre>

<h3>2️⃣ Criar os schemas necessários</h3>

<pre>
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS catalog;
CREATE SCHEMA IF NOT EXISTS workorder;

SELECT schema_name
FROM information_schema.schemata
WHERE schema_name IN ('customer', 'catalog', 'workorder');
</pre>

<h3>3️⃣ Importar o projeto na IDE</h3>

<p>Abrir como <b>Maven Project</b> no IntelliJ IDEA.</p>

<h3>4️⃣ Configurar o application.yaml</h3>

<p>Arquivo:</p>

<pre>
src/main/resources/application.yaml
</pre>

<p>Verifique:</p>

<pre>
spring:
  datasource:
    url:
    username:
    password:
</pre>

<h3>5️⃣ Executar a aplicação</h3>

<pre>
./mvnw spring-boot:run
</pre>

<p>Ou execute a classe:</p>

<pre>
Application.java
</pre>

<hr>

<h2>🧪 Executar testes</h2>

<pre>
./mvnw test
</pre>

<hr>

<h2>🌐 Acesso à API</h2>

<pre>
http://localhost:8080
</pre>

<p>A porta pode variar conforme o <code>application.yaml</code>.</p>

<hr>

<h2>📂 Estrutura de schemas</h2>

<table>
<tr>
<th>Schema</th>
<th>Responsabilidade</th>
</tr>
<tr>
<td>customer</td>
<td>Dados de clientes</td>
</tr>
<tr>
<td>catalog</td>
<td>Produtos e serviços</td>
</tr>
<tr>
<td>workorder</td>
<td>Ordens de serviço</td>
</tr>
</table>

<hr>

<h2>🐳 Parar o banco Docker</h2>

<pre>
docker-compose down
</pre>
