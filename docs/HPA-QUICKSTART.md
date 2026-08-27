# HPA Quick Start Guide

## 1. Prerequisites

Before running HPA, ensure:

- ✅ k3d installed: `k3d --version`
- ✅ kubectl installed: `kubectl version --client`
- ✅ Terraform installed: `terraform --version`
- ✅ Docker running (for k3d)

## 2. Deploy Infrastructure

### Step 1: Initialize and Deploy

Cluster, Traefik e metrics-server são provisionados no repositório **`autoservice-infra-k8s`** (não neste repo).

```bash
# No repositório autoservice-infra-k8s (exemplo)
cd ../autoservice-infra-k8s/terraform
terraform init
terraform plan
terraform apply
```

Depois, neste repo, aplique apenas os manifests da app:

```bash
kubectl apply -k k8s
kubectl apply -k k8s/gateway
```

Expected output (resumo):
```
null_resource.k3d_cluster: Creation complete
kubernetes_namespace.app: Creation complete
kubernetes_secret.postgres / app: Creation complete
kubernetes_manifest.postgres["20-..." ... "24-..."]: Creation complete
helm_release.metrics_server: Creation complete
kubernetes_manifest.app_before_deploy / app_deployment / app_hpa: Creation complete
```

### Step 2: Verify Deployment
```bash
# Set context to local cluster
kubectl config use-context k3d-autoservice-local

# Check namespace
kubectl get namespace autoservice

# Check pods
kubectl get pods -n autoservice

# Check services
kubectl get svc -n autoservice

# Check HPA
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

### Expected Output
```
Deployment: autoservice-app
  CPU Request:  250m
  CPU Limit:    1000m
  Memory Request: 512Mi
  Memory Limit:   1Gi

✓ Readiness Probe configured
✓ Liveness Probe configured
✓ Startup Probe configured

✓ HPA exists
    Min Replicas: 2
    Max Replicas: 10

✓ Metrics Server is running
```

## 4. Test HPA Scaling

### Option A: Automated Load Test (5 minutes)
```bash
chmod +x scripts/test-hpa-load.sh
./scripts/test-hpa-load.sh 300
```

This script:
1. Creates a load generator pod
2. Sends continuous HTTP requests
3. Monitors HPA scaling in real-time
4. Shows metrics and pod counts

### Option B: Manual Load Test

**Terminal 1 - Watch HPA**
```bash
kubectl get hpa autoservice-app-hpa -n autoservice --watch
```

**Terminal 2 - Watch Pods**
```bash
kubectl get pods -n autoservice --watch
```

**Terminal 3 - Monitor Metrics**
```bash
watch -n 2 'kubectl top pods -n autoservice'
```

**Terminal 4 - Generate Load**
```bash
kubectl run -i --tty load-gen --rm \
  --image=busybox \
  --restart=Never \
  --namespace=autoservice \
  -- sh -c 'while true; do \
    wget -q -O- http://autoservice:8088/swagger-ui.html > /dev/null; \
  done'
```

### Expected Behavior

1. **Minutes 0-1**: Load generator starts, pods receive requests
2. **Minutes 1-2**: CPU utilization increases to ~70%
3. **Minute 2**: HPA detects threshold and scales up
   ```
   Current replicas: 2 → Desired replicas: 3
   Current replicas: 3 → Desired replicas: 4
   ```
4. **Minute 3**: Load stabilizes across more pods
5. **Minute 4**: Metrics return to normal
6. **After 5 minutes**: Load stops, HPA waits stabilization window
7. **Minute 9+**: HPA scales back down to 2 pods

### Cleanup Load Test
```bash
# If manual load test, press Ctrl+C in Terminal 4

# Cleanup load generator pod
kubectl delete pod load-gen -n autoservice --ignore-not-found
```

## 5. Monitor Application

### View Logs
```bash
# Live logs from deployment
kubectl logs -f deployment/autoservice-app -n autoservice

# Logs from specific pod
kubectl logs -f <pod-name> -n autoservice

# Previous logs (if pod restarted)
kubectl logs --previous <pod-name> -n autoservice
```

### Check Pod Status
```bash
# Detailed pod information
kubectl describe pod <pod-name> -n autoservice

# Check probe status
kubectl get pod <pod-name> -n autoservice -o jsonpath='{.status.conditions[*]}'
```

### View Events
```bash
# Watch recent events in namespace
kubectl get events -n autoservice --sort-by='.lastTimestamp'

# Watch in real-time
kubectl get events -n autoservice --watch
```

## 6. Access Application

### From Cluster
```bash
# Port forward to local machine
kubectl port-forward svc/autoservice 8088:8088 -n autoservice

# Access in browser: http://localhost:8088/swagger-ui.html
```

### From Inside Pod
```bash
# Execute command in pod
kubectl exec -it <pod-name> -n autoservice -- \
  curl http://localhost:8088/actuator/health

# Open shell in pod
kubectl exec -it <pod-name> -n autoservice -- sh
```

## 7. Troubleshooting

### HPA Shows "Unknown" for Metrics
```bash
# Check if metrics are available
kubectl top nodes
kubectl top pods -n autoservice

# Wait 1-2 minutes and try again
sleep 60; kubectl top pods -n autoservice

# Check metrics server
kubectl get deployment metrics-server -n kube-system
kubectl logs -n kube-system -l k8s-app=metrics-server
```

### Pods Not Starting
```bash
# Check pod status
kubectl describe pod <pod-name> -n autoservice

# View logs
kubectl logs <pod-name> -n autoservice

# Test database connectivity
kubectl exec <pod-name> -n autoservice -- \
  nc -zv postgresql.autoservice.svc.cluster.local 5432
```

### Probes Failing
```bash
# Check probe status
kubectl get pod <pod-name> -n autoservice -o yaml | \
  grep -A 20 readinessProbe

# Manual health check
kubectl exec <pod-name> -n autoservice -- \
  curl http://localhost:8088/actuator/health
```

## 8. Cleanup

### Remove Load Test Pods
```bash
kubectl delete pod -l app=load-gen -n autoservice --ignore-not-found
```

### Destroy Entire Infrastructure

Cluster e Traefik: destruir no repositório **`autoservice-infra-k8s`**.

```bash
# Exemplo no repo infra-k8s
terraform destroy -auto-approve
```

App neste repo:

```bash
kubectl delete -k k8s/gateway --ignore-not-found
kubectl delete -k k8s --ignore-not-found
```

## 9. Configuration Reference

### Resource Requests/Limits
**File**: `k8s/30-deployment-app.yaml`
- CPU Request: 200m · Limit: 1
- Memory Request: 512Mi · Limit: 1Gi

### HPA Thresholds
**File**: `k8s/32-hpa-app.yaml`
- Scale up when CPU > 70%
- Scale up when Memory > 75%
- Min replicas: 2 · Max replicas: 10

### Health Probes
**File**: `k8s/30-deployment-app.yaml`
- Startup: `/actuator/health/liveness` (até ~5 min)
- Readiness: `/actuator/health/readiness`
- Liveness: `/actuator/health/liveness`

## 10. Documentation

For detailed information, see:
- [HPA Configuration Guide](../docs/HPA-CONFIGURATION.md)
- [Kubernetes Manifests](../k8s/README.md)
- [Validation Scripts](../scripts/validate-hpa.sh)
- [Load Test Script](../scripts/test-hpa-load.sh)

## Key Metrics to Monitor

| Metric | Target | Good Range | Alert |
|--------|--------|------------|-------|
| CPU Utilization | <70% | 40-60% | >80% |
| Memory Utilization | <80% | 50-70% | >85% |
| Pod Count | 2-10 | 2-4 | >8 (sustained) |
| Scaling Events/min | 0-1 | 0 | >2 |
| Pod Restart Count | 0 | 0 | >3 (24h) |

## Getting Help

Check pod status:
```bash
kubectl get pods -n autoservice
kubectl describe pod <pod-name> -n autoservice
kubectl logs <pod-name> -n autoservice
```

View HPA details:
```bash
kubectl describe hpa autoservice-app-hpa -n autoservice
```

Check cluster status:
```bash
kubectl get nodes
kubectl get namespaces
kubectl cluster-info
```
