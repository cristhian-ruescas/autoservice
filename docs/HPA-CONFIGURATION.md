# HPA Configuration - Autoservice Application

## Overview

This document describes the Horizontal Pod Autoscaler (HPA) configuration for the Autoservice application, including resource requests/limits, health probes, and scaling behavior.

## Architecture

```
┌─────────────────────────────────────────────────────┐
│         Horizontal Pod Autoscaler (HPA)             │
│  Monitors CPU/Memory → Scales Deployment            │
├─────────────────────────────────────────────────────┤
│         Metrics Server                              │
│  Provides resource metrics to HPA                   │
├─────────────────────────────────────────────────────┤
│         Autoservice Deployment (2-10 pods)          │
│  • Resource Requests: CPU 250m, Memory 512Mi        │
│  • Resource Limits: CPU 1000m, Memory 1Gi           │
│  • Probes: Readiness, Liveness, Startup            │
├─────────────────────────────────────────────────────┤
│         Postgres Service                            │
│  (deployed via Helm)                                │
└─────────────────────────────────────────────────────┘
```

## Resource Configuration

### Requests (Guaranteed Resources)
These are the minimum guaranteed resources for each pod:
- **CPU**: 250m (0.25 CPU core)
- **Memory**: 512Mi (512 MiB)

The scheduler uses these to determine pod placement. HPA decisions are also based on these request values.

### Limits (Maximum Resources)
These are the maximum resources a pod can consume:
- **CPU**: 1000m (1 CPU core)
- **Memory**: 1Gi (1 GiB)

Containers exceeding limits are throttled (CPU) or terminated (memory).

### Why These Values?
- Spring Boot applications typically require 300-500Mi at startup
- 250m CPU request allows good parallelization (4 pods per core)
- 1Gi limit provides headroom for application peaks
- Ratio of 1:4 (request:limit) provides flexibility for scaling

## Health Probes

### Readiness Probe
**Endpoint**: `/actuator/health/readiness`
- **Initial Delay**: 30s
- **Period**: 10s
- **Timeout**: 5s
- **Success Threshold**: 1
- **Failure Threshold**: 3

**Purpose**: Determines if a pod is ready to accept traffic. Failed readiness probes remove the pod from the service load balancer.

### Liveness Probe
**Endpoint**: `/actuator/health/liveness`
- **Initial Delay**: 60s
- **Period**: 15s
- **Timeout**: 5s
- **Failure Threshold**: 3

**Purpose**: Determines if a pod is alive and should be restarted. After 3 consecutive failures, Kubernetes restarts the pod.

### Startup Probe
**Endpoint**: `/actuator/health`
- **Initial Delay**: 0s
- **Period**: 10s
- **Timeout**: 5s
- **Failure Threshold**: 30

**Purpose**: Prevents liveness probe from killing containers during startup. Allows up to 5 minutes (30 × 10s) for the application to start.

## HPA Configuration

### Scaling Triggers
The HPA scales based on two metrics:

#### CPU-based Scaling
```yaml
- type: Resource
  resource:
    name: cpu
    target:
      type: Utilization
      averageUtilization: 70
```
**Scales up when average CPU utilization across all pods exceeds 70%**

#### Memory-based Scaling
```yaml
- type: Resource
  resource:
    name: memory
    target:
      type: Utilization
      averageUtilization: 80
```
**Scales up when average memory utilization across all pods exceeds 80%**

### Replica Limits
- **Minimum Replicas**: 2 (always running 2 pods for high availability)
- **Maximum Replicas**: 10 (prevents runaway scaling costs)

### Scaling Behavior

#### Scale-Down Behavior
- **Stabilization Window**: 300 seconds (5 minutes)
- **Remove 50% of pods** or **1 pod maximum** per 60 seconds
- **Conservative approach**: Waits before scaling down to avoid thrashing

#### Scale-Up Behavior
- **Stabilization Window**: 0 seconds (immediate scaling)
- **Add 100% of current pods** or **2 pods maximum** per 15 seconds
- **Aggressive approach**: Quickly adds capacity to handle load

## Deployment Files

### `/k8s/30-deployment-app.yaml`
Deployment canônico da API (CI + kustomize):
- Resource requests/limits
- Probes (startup, readiness, liveness)
- `envFrom` ConfigMap + Secret (`autoservice-app-secret`, inclui senha JDBC do infra-db)
- `imagePullSecrets: ghcr-pull-secret`

### `/k8s/31-service-app.yaml`
Service da API na porta 8088.

### `/k8s/11-secret-app.example.yaml`
Exemplo de Secret da app (JWT, senha DB gerenciado, mail). Copiar, preencher e aplicar — não versionar valores reais.

### `/k8s/32-hpa-app.yaml`
HPA: CPU 70% e memória 75%, min 2 / max 10 réplicas (`autoservice-app-hpa`).

## Prerequisites

### Metrics Server
The Metrics Server must be running for HPA to work:
```bash
kubectl get deployment metrics-server -n kube-system
```

**Auto-installed by Terraform in `kube-system` namespace**

### Spring Boot Actuator Health Endpoints
The application must have Spring Boot Actuator enabled with:
```properties
management.endpoints.web.exposure.include=health,metrics
management.health.readiness.enabled=true
management.health.liveness.enabled=true
```

(Already configured in `src/main/resources/application.yaml`)

## Deployment

### Using Terraform (cluster)

Provisionamento do cluster/metrics-server fica em **`autoservice-infra-k8s`**. Neste repo:

```bash
kubectl apply -k k8s
kubectl apply -k k8s/gateway
```

Dependências:
1. Cluster + Traefik + metrics-server (`autoservice-infra-k8s`)
2. Banco gerenciado e credenciais (`autoservice-infra-db` → Secret da app)
3. Auth CPF externo (mesmo `AUTOSERVICE_JWT_SECRET`)
5. Instala Metrics Server
6. Aplica app + HPA (`k8s/10`, `30`–`32`) se `deploy_app=true`

### Manual Kubectl / Kustomize
```bash
cp k8s/11-secret-app.example.yaml k8s/11-secret-app.yaml
cp k8s/21-secret-postgres.example.yaml k8s/21-secret-postgres.yaml
# edite credenciais, depois:
kubectl apply -f k8s/11-secret-app.yaml
kubectl apply -f k8s/21-secret-postgres.yaml
kubectl apply -k k8s
```

## Validation

### 1. Check Deployment Resources
```bash
kubectl get deployment autoservice-app -n autoservice -o yaml | grep -A 20 "resources:"
```

### 2. Verify Probes
```bash
kubectl get pods -n autoservice
kubectl describe pod <pod-name> -n autoservice
```

### 3. Check HPA Status
```bash
kubectl get hpa autoservice-app-hpa -n autoservice
kubectl describe hpa autoservice-app-hpa -n autoservice
```

### 4. View Metrics
```bash
kubectl top nodes
kubectl top pods -n autoservice
```

### 5. Run Validation Script
```bash
chmod +x scripts/validate-hpa.sh
./scripts/validate-hpa.sh
```

## Testing HPA Scaling

### Quick Load Test
```bash
chmod +x scripts/test-hpa-load.sh
./scripts/test-hpa-load.sh 300  # 5-minute load test
```

### Manual Load Generation
```bash
# Terminal 1: Watch HPA scaling
kubectl get hpa autoservice-app-hpa -n autoservice --watch

# Terminal 2: Watch pods
kubectl get pods -n autoservice --watch

# Terminal 3: Generate load
kubectl run -i --tty load-gen --rm --image=busybox \
  --restart=Never --namespace=autoservice -- \
  sh -c 'while true; do wget -q -O- http://autoservice:8088/swagger-ui.html; done'
```

### Expected Behavior
1. Load generator starts sending requests
2. CPU/memory utilization increases
3. After ~1-2 minutes, HPA detects high utilization
4. New pods are created (up to maxReplicas=10)
5. Load is distributed across pods
6. Metrics return to normal
7. After 5 minutes of low utilization, pods are scaled down
8. Returns to minReplicas=2

## Monitoring

### Continuous Monitoring
```bash
watch -n 5 'kubectl get hpa,pod -n autoservice'
```

### Prometheus Metrics
With Prometheus installed, monitor:
- `kube_hpa_status_current_replicas`
- `kube_hpa_status_desired_replicas`
- `kube_deployment_status_replicas`
- `container_cpu_usage_seconds_total`
- `container_memory_usage_bytes`

### Logs
```bash
# View deployment logs
kubectl logs -f deployment/autoservice-app -n autoservice

# View HPA events
kubectl get events -n autoservice --sort-by='.lastTimestamp'
```

## Troubleshooting

### HPA Shows Unknown Metrics
**Problem**: HPA status shows "unknown" for CPU/memory

**Solution**:
1. Verify Metrics Server is running:
   ```bash
   kubectl get deployment metrics-server -n kube-system
   ```
2. Check if metrics are available:
   ```bash
   kubectl top nodes
   kubectl top pods -n autoservice
   ```
3. Ensure pods have been running for at least 1 minute

### Pod Stuck in Pending State
**Problem**: New pods cannot be scheduled

**Solution**:
1. Check node resources:
   ```bash
   kubectl describe nodes
   ```
2. Verify requests don't exceed available capacity
3. Check MaxReplicas limit

### Probes Failing
**Problem**: Pods are killing and restarting frequently

**Solution**:
1. Verify application is healthy:
   ```bash
   kubectl exec -it <pod> -n autoservice -- curl localhost:8088/actuator/health
   ```
2. Check pod logs:
   ```bash
   kubectl logs <pod> -n autoservice
   ```
3. Increase `initialDelaySeconds` for startup time
4. Verify database connectivity

### Metrics Not Updating
**Problem**: `kubectl top` shows no data

**Solution**:
1. Wait 1-2 minutes after pod creation
2. Restart Metrics Server:
   ```bash
   kubectl rollout restart deployment/metrics-server -n kube-system
   ```
3. Check Metrics Server logs:
   ```bash
   kubectl logs -n kube-system -l k8s-app=metrics-server
   ```

## Performance Tuning

### Adjusting Scaling Thresholds
Edit `k8s/32-hpa-app.yaml` to change trigger points:
```yaml
metrics:
- type: Resource
  resource:
    name: cpu
    target:
      type: Utilization
      averageUtilization: 70  # Change this value
```

Lower values (50-60%) = more aggressive scaling, higher cost
Higher values (80-90%) = less aggressive, possible latency spikes

### Adjusting Scaling Speed
Modify `behavior.scaleUp` or `behavior.scaleDown`:
```yaml
behavior:
  scaleUp:
    stabilizationWindowSeconds: 0  # 0 = immediate
    policies:
    - type: Percent
      value: 100  # 100% = double pods each period
      periodSeconds: 15  # Every 15 seconds
```

### Resource Request Optimization
If pods are too small/large, adjust in `k8s/30-deployment-app.yaml`:
```yaml
resources:
  requests:
    cpu: 250m  # Increase for CPU-bound apps
    memory: 512Mi  # Increase for memory-bound apps
  limits:
    cpu: 1000m
    memory: 1Gi
```

## References

- [Kubernetes HPA Documentation](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
- [Kubernetes Metrics API](https://kubernetes.io/docs/tasks/debug-application-cluster/resource-metrics-pipeline/)
- [Spring Boot Actuator Health](https://spring.io/guides/gs/actuator-service/)
- [k3d Documentation](https://k3d.io/)
