#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "${SCRIPT_DIR}")"
WEB_DIR="${ROOT_DIR}/web-app"

# Configuration
GOOSE_HOST="${GOOSE_HOST:-127.0.0.1}"
GOOSE_PORT="${GOOSE_PORT:-3000}"
GOOSE_SECRET_KEY="${GOOSE_SECRET_KEY:-test}"
VITE_PORT="${VITE_PORT:-5173}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Cleanup on exit
cleanup() {
    if [[ -n "${GOOSED_PID:-}" ]] && kill -0 "${GOOSED_PID}" 2>/dev/null; then
        log_info "Stopping goosed..."
        kill "${GOOSED_PID}" 2>/dev/null || true
        wait "${GOOSED_PID}" 2>/dev/null || true
    fi
}
trap cleanup EXIT INT TERM

# 1. Shutdown existing services
log_info "Shutting down existing services..."
"${SCRIPT_DIR}/shutdown.sh"

# 2. Start goosed
log_info "Starting goosed at http://${GOOSE_HOST}:${GOOSE_PORT}"
GOOSE_HOST="${GOOSE_HOST}" \
GOOSE_PORT="${GOOSE_PORT}" \
GOOSE_SERVER__SECRET_KEY="${GOOSE_SECRET_KEY}" \
/Users/buyangnie/.local/bin/goosed agent &
GOOSED_PID=$!

# Wait for goosed to be ready
sleep 2
if ! kill -0 "${GOOSED_PID}" 2>/dev/null; then
    log_error "Failed to start goosed"
    exit 1
fi
log_info "goosed started (PID: ${GOOSED_PID})"

# 3. Start webapp
log_info "Starting webapp at http://${GOOSE_HOST}:${VITE_PORT}"
cd "${WEB_DIR}"
npm run dev -- --host "${GOOSE_HOST}"
