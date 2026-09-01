# HPA Quick Start Guide

## 1. Prerequisites

Before running HPA, ensure:

- ✅ Cluster Kubernetes com metrics-server (repo `autoservice-infra-k8s`)
- ✅ kubectl instalado: `kubectl version --client`
- ✅ Credenciais do banco (Neon) no secret `autoservice-app-secret`
- ✅ Docker running (para build local da imagem, se necessário)

## 2. Deploy da Aplicação

### Step 1: Criar secret e aplicar manifests

```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
# edite com credenciais Neon, JWT e SMTP

kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/11-secret-app.yaml
kubectl apply -k k8s
```

### Step 2: Verify Deployment

```bash
kubectl get namespace autoservice
kubectl get pods -n autoservice
kubectl get svc -n autoservice
kubectl get hpa -n autoservice
```

## 3. Validate HPA Configuration

### Run Validation Script

```bash
chmod +x scripts/validate-hpa.sh
./scripts/validate-hpa.sh
```

This script checks:
- ✅ Deployment resource requests/limits
- ✅ Health probes configuration
- ✅ HPA status
- ✅ Metrics server
- ✅ Pod readiness
- ✅ Service availability

## 4. Load Testing

```bash
chmod +x scripts/test-hpa-load.sh
./scripts/test-hpa-load.sh 300  # 5-minute test
```

Watch scaling:

```bash
kubectl get hpa autoservice-app-hpa -n autoservice --watch
```

## 5. Configuration Reference

### Resource Requests/Limits
**File**: `k8s/30-deployment-app.yaml`
- CPU Request: 200m
- CPU Limit: 1000m
- Memory Request: 512Mi
- Memory Limit: 1Gi

### HPA Thresholds
**File**: `k8s/32-hpa-app.yaml`
- Scale up when CPU > 70%
- Scale up when Memory > 75%
- Min replicas: 2
- Max replicas: 10

### Health Probes
**File**: `k8s/30-deployment-app.yaml`
- Readiness: `/actuator/health/readiness`
- Liveness: `/actuator/health/liveness`
- Startup: até ~300s

## 6. Documentation

For detailed information, see:
- [HPA Configuration Guide](./HPA-CONFIGURATION.md)
- [k8s/README.md](../k8s/README.md)

## 7. Troubleshooting

**HPA metrics show "Unknown"** — metrics-server não instalado ou pods ainda não reportando métricas.

**Pods not starting** — verifique logs e credenciais do Neon no secret.

**Database connectivity** — confirme `SPRING_DATASOURCE_URL` com `sslmode=require` para Neon.
