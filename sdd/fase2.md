Especificação de Desenvolvimento - MVP Sistema de Oficina Mecânica (Fase 2)
Objetivo

Evoluir a aplicação desenvolvida na Fase 1 para garantir maior qualidade, resiliência, escalabilidade e automação, incorporando boas práticas de arquitetura de software, infraestrutura como código e pipelines de integração e entrega contínua.

Evolução da Aplicação
Refatoração da Arquitetura

Refatorar a aplicação existente aplicando:

Clean Code
Nomes claros
Código simples
Alta coesão
Baixo acoplamento
Adotar uma das arquiteturas:
Clean Architecture
Arquitetura Hexagonal (Ports and Adapters)
Garantir separação adequada entre domínio, aplicação, infraestrutura e interfaces externas.
Testes

Expandir a cobertura de testes automatizados para os fluxos críticos utilizando:

Testes unitários
Testes de integração
APIs
1. Abertura de Ordem de Serviço

Criar ou atualizar endpoint responsável por abrir uma Ordem de Serviço.

Entrada:

Cliente
Veículo
Serviços
Peças

Saída:

Identificador único da Ordem de Serviço
2. Consulta de Status da Ordem de Serviço

Disponibilizar endpoint para consulta do status atual da OS.

Status possíveis:

Recebida
Em Diagnóstico
Aguardando Aprovação
Em Execução
Finalizada
Entregue
3. Aprovação de Orçamento

Criar endpoint para receber notificações externas de aprovação ou recusa do orçamento pelo cliente.

O endpoint deverá atualizar automaticamente o status correspondente da Ordem de Serviço.

4. Listagem de Ordens de Serviço

Disponibilizar endpoint para listagem das Ordens de Serviço obedecendo às seguintes regras:

Ordenação

Prioridade dos status:

Em Execução
Aguardando Aprovação
Em Diagnóstico
Recebida

Dentro do mesmo status:

Exibir primeiro as Ordens de Serviço mais antigas.
Filtro

Não exibir na listagem:

Ordens Finalizadas
Ordens Entregues

A exclusão deve ser lógica (soft delete da listagem), mantendo os registros no banco de dados.

5. Atualização Automática de Status

Implementar atualização automática do status da Ordem de Serviço por meio de integração com alguma ferramenta externa, como e-mail ou mecanismo equivalente de notificação.

Containerização

Garantir que a aplicação possa ser executada em containers utilizando Docker.

Deve incluir:

Dockerfile atualizado
docker-compose para ambiente de desenvolvimento local
Kubernetes

Preparar a aplicação para execução em Kubernetes.

Criar manifestos YAML contemplando:

Deployments

Configuração dos pods da aplicação.

Services

Exposição da aplicação.

ConfigMaps

Armazenamento de configurações da aplicação.

Secrets

Armazenamento de informações sensíveis, como:

Tokens
Credenciais
Chaves de API
Horizontal Pod Autoscaler (HPA)

Configurar escalabilidade automática baseada em:

Uso de CPU
Uso de memória
Infraestrutura como Código (IaC)

Criar scripts Terraform para provisionamento da infraestrutura.

Os scripts devem contemplar:

Cluster Kubernetes
Banco de dados
Recursos necessários para execução da aplicação

Toda infraestrutura deve ser reproduzível por meio do Terraform.

CI/CD

Configurar pipeline automatizada utilizando uma ferramenta como GitHub Actions, GitLab CI ou equivalente.

A pipeline deverá executar automaticamente:

Build da aplicação.
Execução dos testes automatizados.
Build da imagem Docker.
Publicação da imagem (quando aplicável).
Provisionamento ou atualização do banco de dados.
Deploy da aplicação no cluster Kubernetes.
Aplicação dos manifestos Kubernetes.
Requisitos Técnicos

A solução deverá manter:

Arquitetura baseada em Clean Architecture ou Arquitetura Hexagonal.
APIs REST.
Aplicação containerizada.
Execução local via Docker Compose.
Execução em Kubernetes.
Infraestrutura provisionada via Terraform.
Pipeline CI/CD totalmente automatizada.
Testes automatizados para os fluxos críticos.