#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

SA_PASSWORD="BatchDemo2026!"
DB_NAME="ad_batch_demo"
SQLCMD="/opt/mssql-tools18/bin/sqlcmd"

# ── 1. Start MSSQL container ─────────────────────────────────────────────────
echo "[1/4] Starting MSSQL container..."
docker compose up -d mssql

# ── 2. Wait for MSSQL to be healthy ──────────────────────────────────────────
echo "[2/4] Waiting for MSSQL to be healthy..."
MAX_WAIT=120
ELAPSED=0
until MSYS_NO_PATHCONV=1 docker compose exec -T mssql \
        "$SQLCMD" -S localhost -U sa -P "$SA_PASSWORD" -Q "SELECT 1" -C -b \
        > /dev/null 2>&1; do
    if [ "$ELAPSED" -ge "$MAX_WAIT" ]; then
        echo "ERROR: MSSQL did not become healthy within ${MAX_WAIT}s." >&2
        exit 1
    fi
    echo "  ...still waiting (${ELAPSED}s elapsed)"
    sleep 5
    ELAPSED=$((ELAPSED + 5))
done
echo "  MSSQL is healthy."

# ── 3. Create database if it does not exist ───────────────────────────────────
echo "[3/4] Ensuring database '${DB_NAME}' exists..."
MSYS_NO_PATHCONV=1 docker compose exec -T mssql \
    "$SQLCMD" -S localhost -U sa -P "$SA_PASSWORD" -C -b -Q \
    "IF DB_ID('${DB_NAME}') IS NULL CREATE DATABASE [${DB_NAME}];"
echo "  Database '${DB_NAME}' is ready."

# ── 4. Run Spring Batch application ──────────────────────────────────────────
echo "[4/4] Starting Spring Batch application (profile=local)..."
./gradlew bootRun --args='--spring.profiles.active=local'
