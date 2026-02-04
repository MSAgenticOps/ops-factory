#!/usr/bin/env bash
set -euo pipefail

# Configuration
GOOSE_PORT="${GOOSE_PORT:-3000}"
VITE_PORT="${VITE_PORT:-5173}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }

stop_port() {
    local port=$1
    local name=$2
    if lsof -ti:"${port}" >/dev/null 2>&1; then
        log_info "Stopping ${name} on port ${port}..."
        kill $(lsof -ti:"${port}") 2>/dev/null || true
        sleep 1
    fi
}

# Stop services
stop_port "${VITE_PORT}" "webapp"
stop_port "${GOOSE_PORT}" "goosed"

log_info "All services stopped"
