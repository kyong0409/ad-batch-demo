#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
IMAGE_NAME="batch-api"
BASE_DIR="${PROJECT_ROOT}/03_batch-api"
NAMESPACE="ad-batch"

echo "========== [${IMAGE_NAME}] 이미지 빌드 =========="

# 1. Gradle build
echo "--- Gradle bootJar ---"
cd "$BASE_DIR"
./gradlew bootJar

# 2. Docker build
echo "--- Docker build ---"
docker build --no-cache -t "${IMAGE_NAME}:latest" .

# 3. Load into minikube
echo "--- Minikube image load ---"
minikube ssh -- docker rmi -f "docker.io/library/${IMAGE_NAME}:latest" &>/dev/null || true
minikube image load "${IMAGE_NAME}:latest"

# 4. Apply deployment & restart
echo "--- kubectl apply + restart ---"
kubectl apply -f "$BASE_DIR/k8s/deployment.yml"
kubectl rollout restart deployment/batch-api -n "${NAMESPACE}"

echo "========== [${IMAGE_NAME}] 완료 =========="
