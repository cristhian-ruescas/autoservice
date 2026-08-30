#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}HPA Validation and Testing${NC}"
echo -e "${BLUE}========================================${NC}"

NAMESPACE="autoservice"
DEPLOYMENT="autoservice-app"
HPA="autoservice-app-hpa"
SERVICE="autoservice-app"

# Check 1: Verify Deployment exists and has resource requests/limits
echo -e "\n${BLUE}1. Checking Deployment resources...${NC}"
DEPLOYMENT_INFO=$(kubectl get deployment $DEPLOYMENT -n $NAMESPACE -o json)
echo "Deployment: $DEPLOYMENT"

CPU_REQUEST=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].resources.requests.cpu' -r)
CPU_LIMIT=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].resources.limits.cpu' -r)
MEM_REQUEST=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].resources.requests.memory' -r)
MEM_LIMIT=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].resources.limits.memory' -r)

echo -e "  CPU Request:  ${GREEN}$CPU_REQUEST${NC}"
echo -e "  CPU Limit:    ${GREEN}$CPU_LIMIT${NC}"
echo -e "  Memory Request: ${GREEN}$MEM_REQUEST${NC}"
echo -e "  Memory Limit:   ${GREEN}$MEM_LIMIT${NC}"

# Check 2: Verify Probes
echo -e "\n${BLUE}2. Checking Health Probes...${NC}"
READINESS=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].readinessProbe' -r)
LIVENESS=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].livenessProbe' -r)
STARTUP=$(echo $DEPLOYMENT_INFO | jq '.spec.template.spec.containers[0].startupProbe' -r)

[[ "$READINESS" != "null" ]] && echo -e "  ${GREEN}✓ Readiness Probe configured${NC}" || echo -e "  ${YELLOW}⚠ Readiness Probe NOT configured${NC}"
[[ "$LIVENESS" != "null" ]] && echo -e "  ${GREEN}✓ Liveness Probe configured${NC}" || echo -e "  ${YELLOW}⚠ Liveness Probe NOT configured${NC}"
[[ "$STARTUP" != "null" ]] && echo -e "  ${GREEN}✓ Startup Probe configured${NC}" || echo -e "  ${YELLOW}⚠ Startup Probe NOT configured${NC}"

# Check 3: Verify HPA exists
echo -e "\n${BLUE}3. Checking HPA...${NC}"
HPA_INFO=$(kubectl get hpa $HPA -n $NAMESPACE -o json 2>/dev/null || echo "{}")
HPA_NAME=$(echo $HPA_INFO | jq '.metadata.name' -r 2>/dev/null || echo "null")

if [[ "$HPA_NAME" != "null" ]]; then
  echo -e "  ${GREEN}✓ HPA exists${NC}"
  MIN_REPLICAS=$(echo $HPA_INFO | jq '.spec.minReplicas' -r)
  MAX_REPLICAS=$(echo $HPA_INFO | jq '.spec.maxReplicas' -r)
  echo -e "    Min Replicas: ${GREEN}$MIN_REPLICAS${NC}"
  echo -e "    Max Replicas: ${GREEN}$MAX_REPLICAS${NC}"
else
  echo -e "  ${YELLOW}⚠ HPA NOT found${NC}"
fi

# Check 4: Verify metrics-server
echo -e "\n${BLUE}4. Checking Metrics Server...${NC}"
METRICS_READY=$(kubectl get deployment metrics-server -n kube-system -o json 2>/dev/null | jq '.status.conditions[] | select(.type=="Available") | .status' -r 2>/dev/null || echo "Unknown")

if [[ "$METRICS_READY" == "True" ]]; then
  echo -e "  ${GREEN}✓ Metrics Server is running${NC}"
else
  echo -e "  ${YELLOW}⚠ Metrics Server status: $METRICS_READY${NC}"
fi

# Check 5: Verify Pod status
echo -e "\n${BLUE}5. Checking Pod Status...${NC}"
PODS=$(kubectl get pods -n $NAMESPACE -l app=autoservice-app -o json)
POD_COUNT=$(echo $PODS | jq '.items | length')
READY_COUNT=$(echo $PODS | jq '[.items[] | select(.status.conditions[] | select(.type=="Ready") | select(.status=="True"))] | length')

echo -e "  Total Pods: ${GREEN}$POD_COUNT${NC}"
echo -e "  Ready Pods: ${GREEN}$READY_COUNT${NC}"

# Check 6: Verify Service
echo -e "\n${BLUE}6. Checking Service...${NC}"
SERVICE_IP=$(kubectl get svc $SERVICE -n $NAMESPACE -o jsonpath='{.spec.clusterIP}' 2>/dev/null || echo "Unknown")
SERVICE_PORT=$(kubectl get svc $SERVICE -n $NAMESPACE -o jsonpath='{.spec.ports[0].port}' 2>/dev/null || echo "Unknown")

echo -e "  Cluster IP: ${GREEN}$SERVICE_IP${NC}"
echo -e "  Port: ${GREEN}$SERVICE_PORT${NC}"

# Check 7: Current HPA Status
echo -e "\n${BLUE}7. HPA Current Status...${NC}"
kubectl get hpa $HPA -n $NAMESPACE -w 2>/dev/null || echo -e "${YELLOW}⚠ Could not get HPA status${NC}"

echo -e "\n${BLUE}========================================${NC}"
echo -e "${GREEN}Validation Complete!${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\n${YELLOW}Next Steps:${NC}"
echo -e "1. Test readiness: kubectl exec -it <pod-name> -n $NAMESPACE -- curl localhost:8088/actuator/health/readiness"
echo -e "2. Generate load: kubectl run -i --tty load-generator --rm --image=busybox --restart=Never -n $NAMESPACE -- /bin/sh -c 'while true; do wget -q -O- http://autoservice-app:8088/actuator/health; done'"
echo -e "3. Watch HPA scaling: kubectl get hpa $HPA -n $NAMESPACE --watch"
echo -e "4. Monitor metrics: kubectl top pods -n $NAMESPACE"
