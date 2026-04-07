#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
NAMESPACE="ad-batch"

echo "========== DAG ConfigMap 등록 =========="

kubectl create configmap ad-batch-dags \
  --from-file=ad_batch_dag.py="${PROJECT_ROOT}/01_batch_demo/k8s/airflow/dags/ad_batch_dag.py" \
  --from-file=data_sync_dag.py="${PROJECT_ROOT}/04_failing-batch-demo/k8s/airflow/dags/data_sync_dag.py" \
  --from-file=enterprise_batch_dag.py="${PROJECT_ROOT}/05_enterprise-batch/k8s/airflow/dags/enterprise_batch_dag.py" \
  --from-file=order_sync_dag.py="${PROJECT_ROOT}/06_enterprise-failing-batch/k8s/airflow/dags/order_sync_dag.py" \
  --namespace "${NAMESPACE}" \
  --dry-run=client -o yaml | kubectl apply -f -

# ── Airflow 반영 ──
# values.yml에 새 DAG 마운트가 추가된 경우 helm upgrade 필요 (최초 1회)
# 이후 DAG 내용만 변경 시에는 rollout restart로 충분
CURRENT_MOUNTS=$(kubectl get statefulset airflow-scheduler -n "${NAMESPACE}" -o jsonpath='{.spec.template.spec.containers[0].volumeMounts}' 2>/dev/null)

if echo "$CURRENT_MOUNTS" | grep -q "enterprise_batch_dag.py"; then
  echo "========== Airflow Pod 재시작 (DAG 내용 갱신) =========="
  kubectl rollout restart statefulset airflow-scheduler -n "${NAMESPACE}"
  kubectl rollout restart deployment airflow-dag-processor -n "${NAMESPACE}"
  kubectl rollout restart deployment airflow-api-server -n "${NAMESPACE}"
else
  echo "========== Airflow Helm upgrade (새 DAG 마운트 반영) =========="
  helm upgrade airflow apache-airflow/airflow \
    --namespace "${NAMESPACE}" \
    --values "$PROJECT_ROOT/01_batch_demo/k8s/airflow/values.yml" \
    --set migrateDatabaseJob.enabled=false \
    --timeout 10m
fi

echo "Waiting for Airflow pods to be ready..."
kubectl rollout status statefulset airflow-scheduler -n "${NAMESPACE}" --timeout=600s
kubectl rollout status deployment airflow-api-server -n "${NAMESPACE}" --timeout=600s

echo ""
echo "등록된 DAG:"
echo "  - ad_batch_dag.py           (daily_ad_stats_batch - 성공)"
echo "  - data_sync_dag.py          (data_sync_batch - 실패)"
echo "  - enterprise_batch_dag.py   (enterprise_batch - 성공)"
echo "  - order_sync_dag.py         (order_sync_batch - 실패)"
echo ""
echo "========== DAG 등록 완료 =========="
echo ""
echo "다음 단계: ./08_port_forward.sh 를 실행하세요 (포트 포워딩)"
echo "========================================="
