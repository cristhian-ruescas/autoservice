# Kubernetes Manifests for Autoservice

This directory contains the Kubernetes manifests for deploying the Autoservice application.

## Kustomize structure (`base/` + `overlays/`)

- `base/app/` — namespace, configmap, deployment, service e hpa da aplicação (o que o CI aplica em homolog/prod)
- `base/postgres/` — manifests do PostgreSQL para uso local/manual (não aplicado pelo CI)
- `base/networking/` — Ingress (uso local/manual)
- `base/` (raiz) — agrega os três acima, para subir o stack completo localmente: `kubectl apply -k k8s/base`
- `overlays/homolog/` e `overlays/prod/` — usam só `base/app` e sobrescrevem `DD_ENV` e a label `tags.datadoghq.com/env` para o ambiente correspondente. É isso que o `ci-cd.yml` aplica via `kubectl apply -k k8s/overlays/prod`.

O restante deste documento (`deployment.yaml`, `service.yaml`, `hpa.yaml`, `postgres-secret.yaml` na raiz de `k8s/`) descreve os manifests legados usados manualmente — mantidos como estão, sem relação com a estrutura kustomize acima.

## Files Overview

- **deployment.yaml** - Main application deployment with resource requests/limits and health probes
- **service.yaml** - Service exposing the application on port 8088
- **postgres-secret.yaml** - Kubernetes Secret with PostgreSQL credentials
- **hpa.yaml** - HorizontalPodAutoscaler for CPU and memory-based scaling

## Quick Start

### With Terraform (Recommended)
```bash
cd infra/
terraform init
terraform apply
```

### Manual with kubectl
```bash
kubectl apply -f k8s/postgres-secret.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/hpa.yaml
```

## Resource Configuration

### CPU and Memory

Each pod has:
- **Requests**: CPU 250m, Memory 512Mi (guaranteed)
- **Limits**: CPU 1000m, Memory 1Gi (maximum)

This allows 4 pods per CPU core with good scaling characteristics.

### Why These Values?

- Spring Boot baseline: ~300-400Mi at startup
- 250m CPU allows efficient multi-tenancy (4 pods/core)
- 1Gi memory limit provides safety margin for heap growth
- Ratio 1:4 (request:limit) enables flexible scaling

## Health Probes

### Readiness Probe (/actuator/health/readiness)
- **Purpose**: Pod is ready to serve traffic
- **Initial Delay**: 30s
- **Frequency**: Every 10s
- **Failure Threshold**: 3

### Liveness Probe (/actuator/health/liveness)
- **Purpose**: Pod is alive and should be restarted if failing
- **Initial Delay**: 60s
- **Frequency**: Every 15s
- **Failure Threshold**: 3

### Startup Probe (/actuator/health)
- **Purpose**: Application has completed startup (up to 5 minutes)
- **Initial Delay**: 0s
- **Frequency**: Every 10s
- **Failure Threshold**: 30

## HPA Configuration

### Scaling Triggers
- **CPU Threshold**: 70% utilization
- **Memory Threshold**: 80% utilization

### Replica Range
- **Minimum**: 2 pods (high availability)
- **Maximum**: 10 pods (cost control)

### Scaling Speed
- **Scale Up**: Immediate, add up to 2 pods per 15 seconds
- **Scale Down**: Wait 5 minutes, remove up to 50% of pods per 60 seconds

## Environment Requirements

### Prerequisites
- Kubernetes cluster with Metrics Server installed
- Spring Boot Actuator endpoints enabled
- PostgreSQL database accessible

### Spring Boot Configuration
Ensure `application.yaml` includes:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics
  health:
    readiness:
      enabled: true
    liveness:
      enabled: true
```

## Validation

### Check Deployment
```bash
kubectl get deployment autoservice-app -n autoservice
kubectl describe deployment autoservice-app -n autoservice
```

### Check Pods
```bash
kubectl get pods -n autoservice
kubectl logs -f deployment/autoservice-app -n autoservice
```

### Check HPA
```bash
kubectl get hpa autoservice-hpa -n autoservice
kubectl describe hpa autoservice-hpa -n autoservice
```

### View Metrics
```bash
kubectl top nodes
kubectl top pods -n autoservice
```

## Testing HPA

### Run Validation Script
```bash
./scripts/validate-hpa.sh
```

### Run Load Test
```bash
./scripts/test-hpa-load.sh 300  # 5-minute test
```

### Manual Load Generation
```bash
# Terminal 1: Watch scaling
kubectl get hpa autoservice-hpa -n autoservice --watch

# Terminal 2: Generate load
kubectl run -i --tty load-gen --rm --image=busybox \
  --restart=Never --namespace=autoservice -- \
  sh -c 'while true; do wget -q -O- http://autoservice:8088/swagger-ui.html; done'
```

## Documentation

See [docs/HPA-CONFIGURATION.md](../docs/HPA-CONFIGURATION.md) for detailed documentation.

## Troubleshooting

### HPA Metrics Show "Unknown"
1. Verify Metrics Server is running: `kubectl get deployment metrics-server -n kube-system`
2. Wait 1-2 minutes for metrics to be collected
3. Check if pods are running: `kubectl get pods -n autoservice`

### Pods Not Starting
1. Check pod status: `kubectl describe pod <pod-name> -n autoservice`
2. Review logs: `kubectl logs <pod-name> -n autoservice`
3. Verify database connectivity: `kubectl exec <pod-name> -n autoservice -- nc -zv postgresql.autoservice.svc.cluster.local 5432`

### Scaling Not Happening
1. Verify resource requests are set: `kubectl get deployment autoservice-app -n autoservice -o yaml | grep -A 10 resources`
2. Check HPA events: `kubectl describe hpa autoservice-hpa -n autoservice`
3. Ensure metrics are available: `kubectl top pods -n autoservice`

## Next Steps

1. **Monitor**: Watch the application scaling under load
2. **Tune**: Adjust thresholds based on performance requirements
3. **Integrate**: Add monitoring with Prometheus/Grafana
4. **Extend**: Add custom metrics for business logic-based scaling
