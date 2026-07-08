Especificação de Desenvolvimento - MVP Sistema de Oficina Mecânica
Objetivo

Desenvolver o MVP do back-end de um Sistema Integrado de Atendimento e Execução de Serviços para uma oficina mecânica, utilizando arquitetura monolítica em camadas, aplicando boas práticas de desenvolvimento, segurança e qualidade de software.

Contexto

A oficina realiza atualmente seus processos de forma manual, causando problemas como:

Erros na priorização dos atendimentos.
Falhas no controle de peças e insumos.
Dificuldade no acompanhamento das ordens de serviço.
Perda do histórico de clientes e veículos.
Ineficiência no fluxo de orçamentos e aprovações.

O objetivo do sistema é centralizar essas operações, permitindo o gerenciamento completo das Ordens de Serviço (OS), clientes, veículos, serviços e estoque.

Funcionalidades
1. Gestão de Clientes

Implementar CRUD completo para clientes.

Cada cliente deverá possuir, no mínimo:

Nome
CPF ou CNPJ (validado)
Telefone
E-mail
2. Gestão de Veículos

Implementar CRUD completo para veículos.

Cada veículo deverá conter:

Placa (validada)
Marca
Modelo
Ano
Cliente proprietário
3. Gestão de Serviços

Implementar CRUD de serviços oferecidos pela oficina.

Cada serviço deverá possuir:

Nome
Descrição
Valor base

Exemplos:

Troca de óleo
Alinhamento
Balanceamento
Revisão
4. Gestão de Peças e Insumos

Implementar CRUD de peças e insumos.

Cada item deverá conter:

Nome
Descrição
Valor unitário
Quantidade em estoque

O sistema deverá controlar automaticamente o estoque conforme utilização nas Ordens de Serviço.

5. Ordem de Serviço (OS)

Permitir criação de Ordens de Serviço contendo:

Cliente
Veículo
Serviços solicitados
Peças utilizadas
Orçamento calculado automaticamente

Fluxo esperado:

Identificar cliente pelo CPF/CNPJ.
Selecionar ou cadastrar veículo.
Informar serviços.
Adicionar peças e insumos.
Gerar orçamento automaticamente.
Disponibilizar orçamento para aprovação do cliente.
6. Fluxo de Status da Ordem de Serviço

Cada OS deverá possuir os seguintes status:

Recebida
Em diagnóstico
Aguardando aprovação
Em execução
Finalizada
Entregue

As mudanças de status devem ocorrer conforme as ações executadas no sistema.

7. Consulta da Ordem de Serviço

Disponibilizar endpoint para que o cliente possa consultar o andamento da OS.

Deve retornar informações como:

Status atual
Serviços executados
Peças utilizadas
Valor atualizado
Datas relevantes
8. Gestão Administrativa

Disponibilizar funcionalidades para:

Listagem de Ordens de Serviço
Consulta detalhada da OS
Monitoramento do tempo médio de execução dos serviços
Requisitos Não Funcionais
Segurança

Implementar:

Autenticação JWT para APIs administrativas.
Validação de CPF/CNPJ.
Validação de placa de veículo.
Qualidade

Implementar:

Testes unitários.
Testes de integração.
Cobertura mínima de 80% para os domínios críticos.
Arquitetura

O sistema deverá ser desenvolvido como:

Back-end monolítico.
Arquitetura em camadas.
APIs RESTful.
Separação clara entre domínio, aplicação, infraestrutura e apresentação.
Banco de Dados

A escolha do banco de dados é livre, porém deverá ser justificada.

Documentação da API

Documentar todas as APIs utilizando Swagger (OpenAPI) ou equivalente.

Containerização

Disponibilizar:

Dockerfile
docker-compose.yml

para execução completa da aplicação.

Configuração do Projeto

Fornecer um README contendo:

Requisitos
Como executar o projeto
Como executar os testes
Como acessar a documentação da API
Como subir a aplicação via Docker