# Kubernetes — Autoservice

Manifestos para executar a aplicação em um cluster Kubernetes.

## Conteúdo

| Arquivo | Recurso | Descrição |
|---------|---------|-----------|
| `namespace.yaml` | Namespace | Namespace `autoservice` |
| `configmap.yaml` | ConfigMap | Configurações não sensíveis da aplicação |
| `secret.yaml` | Secret | Credenciais, JWT e senha do banco |
| `postgres.yaml` | StatefulSet + Service + PVC | Banco PostgreSQL com volume persistente |
| `deployment.yaml` | Deployment | Pods da aplicação (2 réplicas, probes) |
| `service.yaml` | Service | Exposição interna da aplicação (ClusterIP) |
| `ingress.yaml` | Ingress | Exposição externa via `autoservice.local` |
| `hpa.yaml` | HorizontalPodAutoscaler | Escala por CPU (70%) e memória (80%) |

## Pré-requisitos

- Cluster Kubernetes (kind, minikube, k3d ou gerenciado).
- `kubectl` configurado.
- Metrics Server instalado (necessário para o HPA).
- Ingress Controller NGINX (para o Ingress).
- Imagem publicada. Ajuste `image:` em `deployment.yaml` para o seu registry
  (ex.: `ghcr.io/<owner>/autoservice:<tag>`).

## Aplicar

```bash
# Tudo de uma vez (kustomize embutido no kubectl)
kubectl apply -k k8s/

# Ou individualmente, na ordem
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

## Verificar

```bash
kubectl -n autoservice get pods,svc,hpa,ingress
kubectl -n autoservice logs deploy/autoservice-app -f
```

## Acessar localmente (sem Ingress)

```bash
kubectl -n autoservice port-forward svc/autoservice-app 8088:80
# Swagger: http://localhost:8088/swagger-ui.html
```

## Observações

- As probes usam o Spring Boot Actuator (`/actuator/health/liveness` e
  `/actuator/health/readiness`), habilitado no `application.yaml`.
- Em produção, substitua os valores de `secret.yaml` e prefira um gerenciador de
  segredos (Sealed Secrets, External Secrets, Vault, etc.).
