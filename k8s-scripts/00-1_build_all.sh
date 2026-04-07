#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========== 전체 이미지 pull & 병렬 배포 시작 =========="

"$SCRIPT_DIR/01_api_update_image.sh" &
"$SCRIPT_DIR/02_ui_update_image.sh" &
"$SCRIPT_DIR/03_batch_update_image.sh" &
"$SCRIPT_DIR/04_failing_batch_update_image.sh" &
"$SCRIPT_DIR/05_enterprise_batch_update_image.sh" &
"$SCRIPT_DIR/06_enterprise_failing_batch_update_image.sh" &

wait

echo "========== 전체 이미지 배포 완료 =========="
echo ""
echo "다음 단계: ./08_port_forward.sh 를 실행하세요 (포트 포워딩)"
echo "========================================="
