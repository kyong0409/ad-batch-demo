#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
NAMESPACE="ad-batch"

# ── 1. Minikube 확인 ──
echo "========== Minikube 확인 =========="
if ! minikube status --format='{{.Host}}' 2>/dev/null | grep -q "Running"; then
  echo "minikube가 실행중이지 않습니다. 시작합니다 (8GB RAM, 4 CPU)..."
  minikube start --memory=8192 --cpus=4 --driver=docker
else
  echo "Minikube is already running."
fi

# ── 2. 스크립트 실행 권한 ──
echo "========== 스크립트 실행 권한 부여 =========="
chmod +x "$SCRIPT_DIR"/*.sh
echo "Done."

# ── 3. Namespace ──
echo "========== Namespace 생성 =========="
kubectl apply -f "$PROJECT_ROOT/01_batch_demo/k8s/namespace.yml"

# ── 4. MSSQL ──
echo "========== MSSQL 배포 =========="
if kubectl get deployment mssql -n "${NAMESPACE}" &>/dev/null \
   && kubectl get deployment mssql -n "${NAMESPACE}" -o jsonpath='{.status.readyReplicas}' 2>/dev/null | grep -q "1"; then
  echo "MSSQL is already running. Skipping."
else
  kubectl apply -f "$PROJECT_ROOT/01_batch_demo/k8s/mssql-deployment.yml"
  echo "Waiting for MSSQL to be ready..."
  kubectl rollout status deployment/mssql -n "${NAMESPACE}" --timeout=300s
  echo "Waiting for mssql-init Job..."
  kubectl wait --for=condition=complete job/mssql-init \
    -n "${NAMESPACE}" --timeout=300s || {
    echo "WARNING: mssql-init job timeout. Checking logs..."
    kubectl logs -n "${NAMESPACE}" -l job-name=mssql-init --tail=50 || true
  }
fi

# ── 5. DAG ConfigMap 등록 (Airflow 설치 전에 먼저 생성) ──
echo "========== DAG ConfigMap 등록 =========="
kubectl create configmap ad-batch-dags \
  --from-file=ad_batch_dag.py="${PROJECT_ROOT}/01_batch_demo/k8s/airflow/dags/ad_batch_dag.py" \
  --from-file=data_sync_dag.py="${PROJECT_ROOT}/04_failing-batch-demo/k8s/airflow/dags/data_sync_dag.py" \
  --from-file=enterprise_batch_dag.py="${PROJECT_ROOT}/05_enterprise-batch/k8s/airflow/dags/enterprise_batch_dag.py" \
  --from-file=order_sync_dag.py="${PROJECT_ROOT}/06_enterprise-failing-batch/k8s/airflow/dags/order_sync_dag.py" \
  --namespace "${NAMESPACE}" \
  --dry-run=client -o yaml | kubectl apply -f -

# ── 6. Airflow ──
echo "========== Airflow 설치 =========="
HELM_RELEASE="airflow"
HELM_CHART="apache-airflow/airflow"
if helm status "${HELM_RELEASE}" -n "${NAMESPACE}" &>/dev/null; then
  echo "Airflow is already running. Skipping."
else
  helm repo add apache-airflow https://airflow.apache.org 2>/dev/null || true
  helm repo update
  helm upgrade --install "${HELM_RELEASE}" "${HELM_CHART}" \
    --namespace "${NAMESPACE}" \
    --create-namespace \
    --values "$PROJECT_ROOT/01_batch_demo/k8s/airflow/values.yml" \
    --set migrateDatabaseJob.enabled=true \
    --timeout 30m
fi

# ── 7. Ingress addon ──
echo "========== Ingress Addon =========="
minikube addons enable ingress
echo "Waiting for ingress controller to be ready..."
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s

echo "Waiting for ingress admission webhook to be ready..."
for i in $(seq 1 30); do
  if kubectl get validatingwebhookconfiguration ingress-nginx-admission &>/dev/null \
     && kubectl wait --namespace ingress-nginx \
          --for=condition=ready pod \
          --selector=app.kubernetes.io/component=controller \
          --timeout=10s &>/dev/null \
     && kubectl apply -f "$PROJECT_ROOT/01_batch_demo/k8s/ingress.yml" 2>/dev/null; then
    echo "Ingress resource applied successfully."
    break
  fi
  echo "  Webhook not ready yet... retrying ($i/30)"
  sleep 5
done

if [ "$i" -eq 30 ]; then
  echo "WARNING: Webhook did not become ready in time. Attempting final apply..."
  kubectl apply -f "$PROJECT_ROOT/01_batch_demo/k8s/ingress.yml"
fi

echo ""
echo "========================================="
echo "  Setup 완료!"
echo "========================================="
echo ""
echo "hosts 파일에 추가:"
echo "  127.0.0.1  ui.batch.local airflow.batch.local"
echo ""
echo "다음 단계: ./00-1_build_all.sh 를 실행하세요 (전체 이미지 pull & 병렬 배포)"
echo "========================================="
