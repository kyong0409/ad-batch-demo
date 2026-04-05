#!/usr/bin/env bash
set -euo pipefail

# cd to project root (01_batch_demo/)
cd "$(dirname "$0")/.."

# Project root is one level up from 01_batch_demo/
PROJECT_ROOT="$(cd .. && pwd)"

NAMESPACE="ad-batch"
APP_IMAGE="ad-batch-demo:latest"
HELM_RELEASE="airflow"
HELM_CHART="apache-airflow/airflow"
AIRFLOW_VALUES="k8s/airflow/values.yml"
DAG_FILE="k8s/airflow/dags/ad_batch_dag.py"

# ─── 1. Minikube ─────────────────────────────────────────────────────────────
echo "==> Checking minikube status..."
if ! minikube status --format='{{.Host}}' 2>/dev/null | grep -q "Running"; then
  echo "==> Starting minikube (8192 MB RAM, 4 CPUs)..."
  minikube start --memory=8192 --cpus=4
else
  echo "==> Minikube is already running."
fi

# ─── 2. Namespace ────────────────────────────────────────────────────────────
echo "==> Creating namespace..."
kubectl apply -f k8s/namespace.yml

# ─── 3. Deploy MSSQL ─────────────────────────────────────────────────────────
if kubectl get deployment mssql -n "${NAMESPACE}" &>/dev/null \
   && kubectl get deployment mssql -n "${NAMESPACE}" -o jsonpath='{.status.readyReplicas}' 2>/dev/null | grep -q "1"; then
  echo "==> MSSQL is already running. Skipping deploy."
else
  echo "==> Deploying MSSQL..."
  kubectl apply -f k8s/mssql-deployment.yml

  echo "==> Waiting for MSSQL deployment to be ready..."
  kubectl rollout status deployment/mssql -n "${NAMESPACE}" --timeout=300s

  # ─── 4. Create database (init Job) ─────────────────────────────────────────
  echo "==> Waiting for mssql-init Job to complete..."
  kubectl wait --for=condition=complete job/mssql-init \
    -n "${NAMESPACE}" --timeout=300s || {
    echo "WARNING: mssql-init job did not complete within timeout; checking logs..."
    kubectl logs -n "${NAMESPACE}" -l job-name=mssql-init --tail=50 || true
  }
fi

# ─── 5. Build app image and load into minikube ───────────────────────────────
echo "==> Building Spring Boot fat jar..."
./gradlew bootJar

echo "==> Building Docker image..."
docker build -t "${APP_IMAGE}" .

echo "==> Loading image into minikube..."
minikube image load "${APP_IMAGE}"

# ─── 5-1. Build failing-batch-demo image and load into minikube ──────────────
echo "==> Building Failing Batch Demo image..."
cd "${PROJECT_ROOT}/04_failing-batch-demo"
./gradlew bootJar
docker build -t failing-batch-demo:latest .
minikube image load failing-batch-demo:latest
cd "${PROJECT_ROOT}/01_batch_demo"

# ─── 6. Install / upgrade Airflow via Helm ───────────────────────────────────
if helm status "${HELM_RELEASE}" -n "${NAMESPACE}" &>/dev/null; then
  echo "==> Airflow is already running. Skipping Helm install."
else
  echo "==> Adding apache-airflow Helm repo..."
  helm repo add apache-airflow https://airflow.apache.org 2>/dev/null || true
  helm repo update

  echo "==> Installing Airflow..."
  helm upgrade --install "${HELM_RELEASE}" "${HELM_CHART}" \
    --namespace "${NAMESPACE}" \
    --create-namespace \
    --values "${AIRFLOW_VALUES}" \
    --set migrateDatabaseJob.enabled=true \
    --timeout 30m
fi

# ─── 7. Create DAG ConfigMap ─────────────────────────────────────────────────
echo "==> Creating DAG ConfigMap from ${DAG_FILE}..."
kubectl create configmap ad-batch-dags \
  --from-file=ad_batch_dag.py="${DAG_FILE}" \
  --from-file=data_sync_dag.py="${PROJECT_ROOT}/04_failing-batch-demo/k8s/airflow/dags/data_sync_dag.py" \
  --namespace "${NAMESPACE}" \
  --dry-run=client -o yaml | kubectl apply -f -

# ─── 8. Build & deploy Batch API ─────────────────────────────────────────────
echo "==> [8] Building Batch API image..."
cd "${PROJECT_ROOT}/03_batch-api"
docker build -t batch-api:latest .
minikube image load batch-api:latest
echo "  Applying Batch API deployment..."
kubectl apply -f k8s/deployment.yml

# ─── 9. Build & deploy Batch UI ──────────────────────────────────────────────
echo "==> [9] Building Batch UI image..."
cd "${PROJECT_ROOT}/02_dscore-batch-ui"
docker build -t batch-ui:latest .
minikube image load batch-ui:latest
echo "  Applying Batch UI deployment..."
kubectl apply -f k8s/deployment.yml

# ─── 10. Enable Ingress & apply rules ────────────────────────────────────────
cd "${PROJECT_ROOT}/01_batch_demo"
echo "==> [10] Enabling Ingress addon..."
minikube addons enable ingress

echo "==> Waiting for Ingress Controller to be ready..."
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
echo "  Ingress Controller is ready."

echo "  Applying Ingress rules..."
kubectl apply -f k8s/ingress.yml

# ─── 11. Print access info & start port-forward ─────────────────────────────
echo ""
echo "========================================="
echo "  Deployment complete!"
echo "========================================="
echo ""
echo "Add the following to your hosts file:"
echo "  127.0.0.1  ui.batch.local api.batch.local airflow.batch.local"
echo ""
echo "Windows: C:\\Windows\\System32\\drivers\\etc\\hosts"
echo "Linux/Mac: /etc/hosts"
echo ""
echo "Then access:"
echo "  UI:      http://ui.batch.local"
echo "  API:     http://api.batch.local"
echo "  Airflow: http://airflow.batch.local"
echo ""
echo "  Airflow credentials (default):"
echo "    Username : admin"
echo "    Password : admin"
echo ""
echo "  To run the batch job manually:"
echo "    kubectl apply -f k8s/spring-batch-job.yml"
echo ""
echo "  To watch job status:"
echo "    kubectl get pods -n ${NAMESPACE} -w"
echo ""
echo "========================================="
echo "  Starting Ingress port-forward (Ctrl+C to stop)..."
echo "========================================="
kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 80:80 443:443
echo ""
echo "  MSSQL connection (port-forward):"
echo "    kubectl port-forward svc/mssql-service 1433:1433 -n ${NAMESPACE}"
echo "    Server  : localhost,1433"
echo "    Login   : SA"
echo "    Password: BatchDemo2026!"
echo "    Database: ad_batch_demo"
echo "========================================="
