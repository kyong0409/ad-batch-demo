#!/bin/bash
set -e

IMAGE_NAMES=("ad-batch-demo" "failing-batch-demo" "batch-api" "batch-ui" "enterprise-batch" "enterprise-failing-batch")
NAMESPACE="ad-batch"

echo "=========================================="
echo "  안 쓰는 이미지 정리 스크립트"
echo "=========================================="

# ── 1. Minikube 이미지 정리 ──
echo ""
echo "========== [Minikube] 이미지 정리 =========="
for IMAGE_NAME in "${IMAGE_NAMES[@]}"; do
  echo ""
  echo "--- ${IMAGE_NAME} ---"

  # minikube에 있는 해당 이미지 목록 (latest 제외하고 정리)
  ALL_TAGS=$(minikube image ls 2>/dev/null | grep "${IMAGE_NAME}:" | sed "s/.*${IMAGE_NAME}://" | sort -V)
  LATEST_TAG=$(echo "$ALL_TAGS" | tail -n 1)

  if [ -z "$ALL_TAGS" ]; then
    echo "  이미지 없음. 스킵."
    continue
  fi

  echo "  최신 태그: ${LATEST_TAG}"

  for TAG in $ALL_TAGS; do
    if [ "$TAG" != "$LATEST_TAG" ]; then
      echo "  삭제: docker.io/library/${IMAGE_NAME}:${TAG}"
      minikube ssh -- docker rmi -f "docker.io/library/${IMAGE_NAME}:${TAG}" &>/dev/null || true
    fi
  done
  echo "  정리 완료."
done

# ── 2. 로컬 Docker 이미지 정리 ──
echo ""
echo "========== [Local Docker] dangling 이미지 정리 =========="
DANGLING=$(docker images -f "dangling=true" -q 2>/dev/null)
if [ -n "$DANGLING" ]; then
  echo "  dangling 이미지 ${DANGLING} 삭제 중..."
  docker rmi $DANGLING 2>/dev/null || true
  echo "  완료."
else
  echo "  dangling 이미지 없음."
fi

echo ""
echo "=========================================="
echo "  정리 완료!"
echo "=========================================="
