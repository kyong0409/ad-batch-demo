#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========== 전체 이미지 병렬 빌드 시작 =========="

"$SCRIPT_DIR/01_api_update_image.sh" &
"$SCRIPT_DIR/02_ui_update_image.sh" &
"$SCRIPT_DIR/03_batch_update_image.sh" &
"$SCRIPT_DIR/04_failing_batch_update_image.sh" &
"$SCRIPT_DIR/05_enterprise_batch_update_image.sh" &
"$SCRIPT_DIR/06_enterprise_failing_batch_update_image.sh" &

wait

echo "========== 전체 이미지 빌드 완료 =========="
