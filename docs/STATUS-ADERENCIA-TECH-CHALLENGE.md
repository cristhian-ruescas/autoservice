# Status de Aderência ao Escopo - Tech Challenge (autoservice)

Data de referência: 2026-08-21  
Repositório avaliado: `autoservice` (aplicação principal em Kubernetes)

## 1. Resumo executivo

Este repositório foi reposicionado para cumprir o papel de **aplicação principal** no modelo de 4 repositórios exigido pelo desafio.  
Já há evolução relevante em **segurança (CPF/JWT)**, **observabilidade (logs/metrics/correlação)**, **pipeline CI/CD de aplicação** e **cobertura de testes**.

Ainda faltam itens de fechamento da entrega, principalmente de **evidências publicadas** (links finais), **documentação formal complementar** (RFC/ADR/ER) e validações fim-a-fim entre os 4 repositórios.

---

## 2. O que foi feito neste repositório (e por quê)

## 2.1 Segregação por responsabilidade (4 repositórios)
- Remoção do conteúdo de `infra/` e `k8s/` deste repositório.
- README reescrito para papel de app repo.

**Por que foi feito:**  
Evitar acoplamento com infraestrutura e alinhar ao requisito de separação:
1. Lambda Auth  
2. Infra K8s (Terraform)  
3. Infra DB (Terraform)  
4. Aplicação principal (`autoservice`).

## 2.2 Segurança e autenticação por CPF/JWT
- Ajustes no fluxo de segurança com JWT.
- Implementação de `CpfAccessGuard` para reforçar autorização por CPF nas rotas sensíveis.
- Proteções aplicadas via `@PreAuthorize` (ex.: abertura de atendimento e consulta por CPF).
- Correção de injeção do `JwtUtil` para estabilidade no boot da aplicação.

**Por que foi feito:**  
Atender o requisito de proteger rotas sensíveis com autenticação/autorização vinculada ao CPF.

## 2.3 Observabilidade operacional
- Logs estruturados JSON (`logback-spring.xml`).
- Correlação de requisições com `X-Correlation-Id` (`RequestCorrelationFilter` + MDC).
- Métricas via Micrometer/Actuator/Prometheus em `application.yaml`.
- Métricas de negócio e notificação (ex.: `autoservice.service_orders.*`, `autoservice.notifications.webhook.*`).

**Por que foi feito:**  
Atender os requisitos de monitoramento, rastreabilidade e detecção de gargalos/falhas.

## 2.4 CI/CD da aplicação
- Workflow com build, testes e publicação de imagem Docker para `homolog`/`prod`.
- Responsabilidades de deploy/provisionamento mantidas nos repositórios de infra.

**Por que foi feito:**  
Separar pipeline de aplicação da pipeline de infraestrutura e manter fronteiras claras entre repositórios.

## 2.5 Testes e qualidade
- Ampliação de testes unitários em casos de uso e orquestradores.
- Execução local da suíte completa com sucesso.

**Por que foi feito:**  
Reduzir risco de regressão e fortalecer evidência técnica da entrega.

---

## 3. Aderência ao escopo (foco neste repositório)

## 3.1 Requisitos atendidos/endereçados
- [x] Repositório focado na aplicação principal.
- [x] Pipeline CI/CD funcional para build/test/publish da aplicação.
- [x] Rotas sensíveis protegidas com regras de segurança por CPF/JWT.
- [x] Logs JSON + correlação por requisição.
- [x] Exposição de health/metrics/prometheus.
- [x] Documentação com diagrama de arquitetura do repo e fluxo de autenticação + abertura de OS.
- [x] Definição de alertas operacionais com critérios objetivos (documentada).

## 3.2 Itens parcialmente atendidos (dependem de publicação externa)
- [~] Link Swagger/Postman publicado (README com placeholder).
- [~] Links de deploy ativo homolog/prod (README com placeholder).
- [~] Referência final de RFCs/ADRs (placeholders para links oficiais).

---

## 4. Pendências para fechar este repositório 100%

1. Substituir placeholders no `README.md`:
   - Swagger público
   - Postman publicado
   - URL homolog
   - URL produção
   - links oficiais de RFCs e ADRs

2. Consolidar evidências de observabilidade:
   - prints de dashboard (Datadog/New Relic),
   - prints de alertas ativos,
   - exemplo de trace/log com `correlationId`.

3. Publicar documentação final de arquitetura e governança:
   - RFCs/ADRs em pasta/repo oficial definitivo,
   - links cruzados estáveis entre os 4 repositórios.

4. Validar demonstração fim-a-fim para o vídeo:
   - autenticação por CPF,
   - uso das APIs protegidas com JWT emitido pela Lambda,
   - execução da pipeline,
   - deploy automatizado,
   - monitoramento e alertas em tempo real.

---

## 5. Dependências com os outros 3 repositórios

## `autoservice-lambda-auth`
- Deve publicar endpoint/autorizador e contrato de token (issuer/claims/expiração) compatível com a app.

## `autoservice-infra-db`
- Deve entregar DB gerenciado e parâmetros de conexão/secret para consumo da app.

## `autoservice-infra-k8s`
- Deve publicar cluster/ingress/gateway e automação de deploy para consumir a imagem do `autoservice`.

**Observação:** sem esses três concluídos e integrados, a aderência final do desafio fica incompleta, mesmo com este repo em bom estado.

---

## 6. Checklist de fechamento da entrega em grupo

- [ ] 4 repositórios com README completo e links funcionais.
- [ ] Branch protection + PR obrigatório configurados em todos.
- [ ] Deploy automático homolog/prod comprovado.
- [ ] API Gateway + Lambda CPF em produção/homolog funcionando.
- [ ] Dashboards e alertas ativos (com evidências).
- [ ] Documentação arquitetural completa (componentes, sequência, RFC, ADR, justificativa de banco + ER).
- [ ] Vídeo (até 15 min) com todos os pontos obrigatórios.
- [ ] PDF único com links de repositórios, vídeo, documentações e confirmação do usuário `soat-architecture`.

---

## 7. Conclusão

O repositório `autoservice` está **tecnicamente bem avançado e aderente ao papel de aplicação principal**.  
O que falta agora é majoritariamente **publicação/evidência final** e **amarração entre repositórios** para cumprir integralmente o escopo de entrega do Tech Challenge.
