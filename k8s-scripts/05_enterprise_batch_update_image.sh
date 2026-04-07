#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
IMAGE_NAME="enterprise-batch"
BASE_DIR="${PROJECT_ROOT}/05_enterprise-batch"

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

echo "========== [${IMAGE_NAME}] 완료 =========="
