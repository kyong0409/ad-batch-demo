#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
IMAGE_NAME="failing-batch-demo"
BASE_DIR="${PROJECT_ROOT}/04_failing-batch-demo"

echo "========== [${IMAGE_NAME}] 이미지 빌드 =========="

# 1. Docker build (멀티스테이지 Dockerfile 내에서 Gradle 빌드 포함)
cd "$BASE_DIR"
echo "--- Docker build ---"
docker build --no-cache -t "${IMAGE_NAME}:latest" .

# 3. Load into minikube
echo "--- Minikube image load ---"
minikube ssh -- docker rmi -f "docker.io/library/${IMAGE_NAME}:latest" &>/dev/null || true
minikube image load "${IMAGE_NAME}:latest"

echo "========== [${IMAGE_NAME}] 완료 =========="
