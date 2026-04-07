#!/bin/bash
set -e

echo "========== Ingress Controller 확인 =========="
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=120s
echo "========== Ingress Controller Ready =========="

echo ""
echo "접속 URL:"
echo "  UI:      http://ui.batch.local"
echo "  API:     http://api.batch.local"
echo "  Airflow: http://airflow.batch.local (admin/admin)"
echo ""
echo "종료하려면 Ctrl+C 를 누르세요."
echo "========== Port Forward Start =========="
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
  kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 80:80 443:443
else
  sudo kubectl port-forward -n ingress-nginx service/ingress-nginx-controller 80:80 443:443
fi
