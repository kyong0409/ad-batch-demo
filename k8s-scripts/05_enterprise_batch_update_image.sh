#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
IMAGE_NAME="enterprise-batch"
REGISTRY="kyong0409"

echo "========== [${IMAGE_NAME}] 이미지 Pull & 로드 =========="

# 1. Docker Hub에서 최신 이미지 Pull
echo "--- Docker pull ---"
docker pull "${REGISTRY}/${IMAGE_NAME}:latest"

# 2. Load into minikube
echo "--- Minikube image load ---"
minikube ssh -- docker rmi -f "docker.io/${REGISTRY}/${IMAGE_NAME}:latest" &>/dev/null || true
minikube image load "${REGISTRY}/${IMAGE_NAME}:latest"

echo "========== [${IMAGE_NAME}] 완료 =========="
