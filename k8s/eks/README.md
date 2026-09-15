# Deploy da aplicação no EKS

Overlay para executar a API no EKS usando RDS externo (sem Postgres no cluster).

## Pré-requisitos

- Cluster EKS e API Gateway provisionados (`autoservice-infra-k8s`)
- RDS acessível a partir dos nodes/Lambda (`autoservice-infra-db`)
- Imagem da aplicação publicada no registry
- Ingress Controller compatível com `ingressClassName: nginx`

## Configuração

1. Copie o secret de exemplo e preencha os valores locais (não versionar o arquivo gerado):

```powershell
Copy-Item k8s\eks\secret-app.example.yaml k8s\eks\secret-app.yaml
```

`AUTOSERVICE_JWT_SECRET` deve ser o mesmo `JWT_SECRET` da Lambda de autenticação.

2. Ajuste `configmap-app-rds.yaml` (`SPRING_DATASOURCE_URL`, `APP_BASE_URL`) e a tag da imagem em `deployment-app-eks.yaml`.

3. Aplique:

```powershell
kubectl apply -f k8s/eks/secret-app.yaml
kubectl apply -k k8s/eks
kubectl -n autoservice get pods,svc,ingress
```

## Autenticação

| Endpoint | Autenticação |
|----------|----------------|
| `GET /ordens-servico/{id}/andamento` | JWT ADMIN ou CLIENTE (CPF dono da OS) |
| `GET .../aprovacao/aprovar` e `/reprovar` | público (link de e-mail) |
| Demais rotas de oficina | JWT ADMIN (`POST /auth/login`) |
| `POST /auth/cpf` | API Gateway → Lambda (`autoservice-lambda-auth`) |
