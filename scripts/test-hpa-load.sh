#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}HPA Load Testing${NC}"
echo -e "${BLUE}========================================${NC}"

NAMESPACE="autoservice"
SERVICE="autoservice"
SERVICE_PORT="8088"
LOAD_DURATION="${1:-300}"  # Default 5 minutes

echo -e "\n${YELLOW}Configuration:${NC}"
echo -e "  Namespace: ${GREEN}$NAMESPACE${NC}"
echo -e "  Service: ${GREEN}$SERVICE${NC}"
echo -e "  Port: ${GREEN}$SERVICE_PORT${NC}"
echo -e "  Duration: ${GREEN}${LOAD_DURATION}s${NC}"

# Check if service is ready
echo -e "\n${BLUE}Checking service availability...${NC}"
SERVICE_IP=$(kubectl get svc $SERVICE -n $NAMESPACE -o jsonpath='{.spec.clusterIP}' 2>/dev/null)

if [[ -z "$SERVICE_IP" ]]; then
  echo -e "${RED}✗ Service not found in namespace $NAMESPACE${NC}"
  exit 1
fi

echo -e "${GREEN}✓ Service found at $SERVICE_IP:$SERVICE_PORT${NC}"

# Create a load generator pod
LOAD_POD_NAME="load-generator-$(date +%s)"
echo -e "\n${BLUE}Starting load generator pod: $LOAD_POD_NAME${NC}"

kubectl run "$LOAD_POD_NAME" \
  --namespace="$NAMESPACE" \
  --image="busybox" \
  --restart=Never \
  -- sh -c "
    echo 'Load test starting...'
    END=\$((SECONDS + $LOAD_DURATION))
    REQUEST_COUNT=0
    
    while [ \$SECONDS -lt \$END ]; do
      wget -q -O- http://$SERVICE:$SERVICE_PORT/actuator/health/readiness > /dev/null 2>&1 || true
      REQUEST_COUNT=\$((REQUEST_COUNT + 1))
      echo \"[Request \$REQUEST_COUNT] at \$(date)\"
    done
    
    echo \"Load test completed. Total requests: \$REQUEST_COUNT\"
  " &

LOAD_PID=$!

echo -e "${GREEN}✓ Load generator started (PID: $LOAD_PID)${NC}"

# Monitor HPA while load is running
echo -e "\n${BLUE}Monitoring HPA scaling (${LOAD_DURATION}s)...${NC}"
echo -e "${YELLOW}(Press Ctrl+C to stop monitoring)${NC}\n"

MONITOR_END=$((SECONDS + LOAD_DURATION + 60))

while [ $SECONDS -lt $MONITOR_END ]; do
  echo -e "\n${BLUE}--- $(date '+%H:%M:%S') ---${NC}"
  
  # Show current pod count
  POD_COUNT=$(kubectl get pods -n $NAMESPACE -l app=autoservice --no-headers 2>/dev/null | wc -l)
  echo -e "Current Pods: ${GREEN}$POD_COUNT${NC}"
  
  # Show pod status
  kubectl get pods -n $NAMESPACE -l app=autoservice --no-headers 2>/dev/null | \
    awk '{print "  " $1 " - " $3}'
  
  # Show HPA status
  HPA_STATUS=$(kubectl get hpa autoservice-hpa -n $NAMESPACE --no-headers 2>/dev/null || echo "")
  if [[ -n "$HPA_STATUS" ]]; then
    echo -e "HPA Status:"
    echo -e "$HPA_STATUS" | awk '{print "  " $0}'
  fi
  
  # Show resource metrics if available
  echo -e "\nResource Usage:"
  kubectl top pods -n $NAMESPACE -l app=autoservice 2>/dev/null || echo "  (metrics not yet available)"
  
  sleep 10
done

echo -e "\n${BLUE}========================================${NC}"
echo -e "${GREEN}Load testing completed!${NC}"
echo -e "${BLUE}========================================${NC}"

echo -e "\n${YELLOW}Clean up:${NC}"
echo "To remove the load generator pod, run:"
echo "  kubectl delete pod $LOAD_POD_NAME -n $NAMESPACE"

echo -e "\n${YELLOW}Next steps:${NC}"
echo "1. Check final HPA status:"
echo "   kubectl get hpa autoservice-hpa -n $NAMESPACE"
echo ""
echo "2. View scaling history:"
echo "   kubectl describe hpa autoservice-hpa -n $NAMESPACE"
echo ""
echo "3. Check pod logs:"
echo "   kubectl logs -f deployment/autoservice-app -n $NAMESPACE"
