#!/usr/bin/env bash
set -euo pipefail

# Collect a complete debug bundle for one Ops Factory session.
#
# Typical usage from gateway/scripts:
#   ./collect-session-debug.sh --session 20260420_30 --user admin --agent qos-agent
#
# Environment variables are also supported:
#   SESSION_ID=20260420_30 USER_ID=admin AGENT_ID=qos-agent ./collect-session-debug.sh

usage() {
  cat <<'EOF'
Collect debug information for one Ops Factory session.

Required:
  --session, --session-id ID       Session id to inspect. Can also use SESSION_ID.

Common options:
  --user, --user-id ID             User id. Defaults to USER_ID or admin.
  --agent, --agent-id ID           Agent id. Defaults to AGENT_ID or qos-agent.
  --root DIR                       ops-factory repository root. Defaults to ROOT, script location, or cwd.
  --gateway-root DIR               Gateway root. Defaults to GATEWAY_ROOT or <root>/gateway.
  --db FILE                        sessions.db path. Defaults to the selected user/agent sessions.db.
  --out-dir DIR                    Output directory. Defaults to /tmp/session-debug-...
  --gateway-url URL                Gateway URL. Defaults to http://127.0.0.1:3000.
  --gateway-api-prefix PREFIX      API prefix. Defaults to /gateway.
  --gateway-secret SECRET          Gateway secret header value. Also reads GATEWAY_API_PASSWORD.
  --goosed-port PORT               Optional direct goosed port.
  --goosed-secret SECRET           Optional direct goosed secret.
  --gateway-jar FILE               Optional running gateway jar for fingerprinting.
  --no-api                         Skip gateway/goosed HTTP requests.
  --no-archive                     Do not create the .tar.gz archive.
  -h, --help                       Show this help.

Useful environment knobs:
  LOG_TAIL_LINES=2000              Max matching lines per log file.
  PREVIEW_CHARS=500                Preview length in TSV summaries.
  TRANSCRIPT_CHARS_PER_PART=4000   Max text per content part in db-transcript.md.
  COPY_GOOSED_LOGS=1               Copy full goosed logs related to this session.
  MAX_GOOSED_LOG_BYTES=104857600   Max total bytes of copied goosed logs.
  MAX_AGENT_FILE_BYTES=1048576     Max small agent text file size to copy.
  COPY_WORKDIR=0                   Set to 1 to copy the session working directory.
  MAX_WORKDIR_COPY_BYTES=52428800  Safety limit for COPY_WORKDIR.
EOF
}

die() {
  echo "ERROR: $*" >&2
  echo >&2
  usage >&2
  exit 1
}

require_value() {
  local opt="$1"
  local value="${2:-}"
  [ -n "$value" ] || die "$opt requires a value"
}

USER_ID="${USER_ID:-admin}"
AGENT_ID="${AGENT_ID:-qos-agent}"
SESSION_ID="${SESSION_ID:-}"
ROOT="${ROOT:-}"
GATEWAY_ROOT="${GATEWAY_ROOT:-}"
DB="${DB:-}"
GATEWAY_URL="${GATEWAY_URL:-http://127.0.0.1:3000}"
GATEWAY_API_PREFIX="${GATEWAY_API_PREFIX:-/gateway}"
GATEWAY_SECRET="${GATEWAY_SECRET:-${GATEWAY_API_PASSWORD:-}}"
GOOSED_PORT="${GOOSED_PORT:-}"
GOOSED_SECRET="${GOOSED_SECRET:-}"
GATEWAY_JAR="${GATEWAY_JAR:-}"
OUT_DIR="${OUT_DIR:-}"
FETCH_API="${FETCH_API:-1}"
CREATE_ARCHIVE="${CREATE_ARCHIVE:-1}"
LOG_TAIL_LINES="${LOG_TAIL_LINES:-2000}"
PREVIEW_CHARS="${PREVIEW_CHARS:-500}"
TRANSCRIPT_CHARS_PER_PART="${TRANSCRIPT_CHARS_PER_PART:-4000}"
COPY_GOOSED_LOGS="${COPY_GOOSED_LOGS:-1}"
MAX_GOOSED_LOG_BYTES="${MAX_GOOSED_LOG_BYTES:-104857600}"
MAX_AGENT_FILE_BYTES="${MAX_AGENT_FILE_BYTES:-1048576}"
COPY_WORKDIR="${COPY_WORKDIR:-0}"
MAX_WORKDIR_COPY_BYTES="${MAX_WORKDIR_COPY_BYTES:-52428800}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

while [ "$#" -gt 0 ]; do
  case "$1" in
    --session|--session-id)
      require_value "$1" "${2:-}"
      SESSION_ID="$2"
      shift 2
      ;;
    --user|--user-id)
      require_value "$1" "${2:-}"
      USER_ID="$2"
      shift 2
      ;;
    --agent|--agent-id)
      require_value "$1" "${2:-}"
      AGENT_ID="$2"
      shift 2
      ;;
    --root)
      require_value "$1" "${2:-}"
      ROOT="$2"
      shift 2
      ;;
    --gateway-root)
      require_value "$1" "${2:-}"
      GATEWAY_ROOT="$2"
      shift 2
      ;;
    --db)
      require_value "$1" "${2:-}"
      DB="$2"
      shift 2
      ;;
    --out-dir)
      require_value "$1" "${2:-}"
      OUT_DIR="$2"
      shift 2
      ;;
    --gateway-url)
      require_value "$1" "${2:-}"
      GATEWAY_URL="$2"
      shift 2
      ;;
    --gateway-api-prefix)
      require_value "$1" "${2:-}"
      GATEWAY_API_PREFIX="$2"
      shift 2
      ;;
    --gateway-secret)
      require_value "$1" "${2:-}"
      GATEWAY_SECRET="$2"
      shift 2
      ;;
    --goosed-port)
      require_value "$1" "${2:-}"
      GOOSED_PORT="$2"
      shift 2
      ;;
    --goosed-secret)
      require_value "$1" "${2:-}"
      GOOSED_SECRET="$2"
      shift 2
      ;;
    --gateway-jar)
      require_value "$1" "${2:-}"
      GATEWAY_JAR="$2"
      shift 2
      ;;
    --no-api)
      FETCH_API=0
      shift
      ;;
    --no-archive)
      CREATE_ARCHIVE=0
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    --)
      shift
      break
      ;;
    -*)
      die "Unknown option: $1"
      ;;
    *)
      if [ -z "$SESSION_ID" ]; then
        SESSION_ID="$1"
      else
        die "Unexpected positional argument: $1"
      fi
      shift
      ;;
  esac
done

[ -n "$SESSION_ID" ] || die "SESSION_ID is required"

safe_name() {
  printf '%s' "$1" | sed 's/[^A-Za-z0-9_.-]/_/g'
}

sql_escape() {
  printf '%s' "$1" | sed "s/'/''/g"
}

file_size() {
  stat -f%z "$1" 2>/dev/null || stat -c%s "$1" 2>/dev/null || wc -c < "$1"
}

file_mtime_epoch() {
  stat -f%m "$1" 2>/dev/null || stat -c%Y "$1" 2>/dev/null || echo 0
}

sha256_file() {
  sha256sum "$1" 2>/dev/null || shasum -a 256 "$1" 2>/dev/null || true
}

iso_now() {
  date '+%Y-%m-%dT%H:%M:%S%z'
}

validate_uint_range() {
  local name="$1"
  local value="$2"
  local min="$3"
  local max="$4"
  if ! [[ "$value" =~ ^[0-9]+$ ]] || [ "$value" -lt "$min" ] || [ "$value" -gt "$max" ]; then
    die "$name must be an integer between $min and $max: $value"
  fi
}

validate_bool_flag() {
  local name="$1"
  local value="$2"
  case "$value" in
    0|1) ;;
    *) die "$name must be 0 or 1: $value" ;;
  esac
}

validate_optional_port() {
  local name="$1"
  local value="$2"
  [ -z "$value" ] && return 0
  validate_uint_range "$name" "$value" 1 65535
}

validate_gateway_url() {
  local value="$1"
  case "$value" in
    http://*|https://*) ;;
    *) die "GATEWAY_URL must start with http:// or https://: $value" ;;
  esac
  if printf '%s' "$value" | grep -Eq '[[:space:]]'; then
    die "GATEWAY_URL must not contain whitespace: $value"
  fi
}

validate_api_prefix() {
  local value="$1"
  if printf '%s' "$value" | grep -Eq '[[:space:]?#]'; then
    die "GATEWAY_API_PREFIX must not contain whitespace, ? or #: $value"
  fi
}

normalize_api_prefix() {
  local prefix="$1"
  [ -n "$prefix" ] || {
    printf ''
    return
  }
  case "$prefix" in
    /*) ;;
    *) prefix="/$prefix" ;;
  esac
  prefix="${prefix%/}"
  printf '%s' "$prefix"
}

validate_uint_range "LOG_TAIL_LINES" "$LOG_TAIL_LINES" 1 1000000
validate_uint_range "PREVIEW_CHARS" "$PREVIEW_CHARS" 1 1000000
validate_uint_range "TRANSCRIPT_CHARS_PER_PART" "$TRANSCRIPT_CHARS_PER_PART" 1 10000000
validate_uint_range "MAX_GOOSED_LOG_BYTES" "$MAX_GOOSED_LOG_BYTES" 1 1099511627776
validate_uint_range "MAX_AGENT_FILE_BYTES" "$MAX_AGENT_FILE_BYTES" 1 1099511627776
validate_uint_range "MAX_WORKDIR_COPY_BYTES" "$MAX_WORKDIR_COPY_BYTES" 1 1099511627776
validate_bool_flag "FETCH_API" "$FETCH_API"
validate_bool_flag "CREATE_ARCHIVE" "$CREATE_ARCHIVE"
validate_bool_flag "COPY_GOOSED_LOGS" "$COPY_GOOSED_LOGS"
validate_bool_flag "COPY_WORKDIR" "$COPY_WORKDIR"
validate_optional_port "GOOSED_PORT" "$GOOSED_PORT"
validate_gateway_url "$GATEWAY_URL"
validate_api_prefix "$GATEWAY_API_PREFIX"

if [ -z "$ROOT" ]; then
  if [ -d "$SCRIPT_DIR/.." ] && [ "$(basename "$SCRIPT_DIR")" = "scripts" ] && [ "$(basename "$(cd "$SCRIPT_DIR/.." && pwd)")" = "gateway" ]; then
    ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
  elif [ -d "$PWD/gateway" ] && [ -d "$PWD/web-app" ]; then
    ROOT="$PWD"
  else
    ROOT="$PWD"
  fi
fi

GATEWAY_ROOT="${GATEWAY_ROOT:-$ROOT/gateway}"
GATEWAY_API_PREFIX="$(normalize_api_prefix "$GATEWAY_API_PREFIX")"

SESSION_SQL="$(sql_escape "$SESSION_ID")"

if [ -z "$DB" ]; then
  DB="$GATEWAY_ROOT/users/$USER_ID/agents/$AGENT_ID/data/sessions/sessions.db"
fi

detect_db_for_session() {
  [ -d "$GATEWAY_ROOT/users" ] || return 0
  find "$GATEWAY_ROOT/users" -path '*/data/sessions/sessions.db' -type f -print 2>/dev/null | while IFS= read -r candidate; do
    if sqlite3 "$candidate" "select 1 from sessions where id = '$SESSION_SQL' limit 1;" 2>/dev/null | grep -q 1; then
      printf '%s\n' "$candidate"
      break
    elif sqlite3 "$candidate" "select 1 from messages where session_id = '$SESSION_SQL' limit 1;" 2>/dev/null | grep -q 1; then
      printf '%s\n' "$candidate"
      break
    fi
  done
}

if [ ! -f "$DB" ] && command -v sqlite3 >/dev/null 2>&1; then
  DETECTED_DB="$(detect_db_for_session | head -1 || true)"
  if [ -n "$DETECTED_DB" ]; then
    DB="$DETECTED_DB"
    rel="${DB#"$GATEWAY_ROOT/users/"}"
    if [ "$rel" != "$DB" ] && printf '%s' "$rel" | grep -q '/agents/'; then
      USER_ID="${rel%%/agents/*}"
      agent_part="${rel#*/agents/}"
      AGENT_ID="${agent_part%%/data/sessions/sessions.db}"
    fi
  fi
fi

RUN_STAMP="$(date +%Y%m%d%H%M%S)"
if [ -z "$OUT_DIR" ]; then
  OUT_DIR="/tmp/session-debug-$(safe_name "$USER_ID")-$(safe_name "$AGENT_ID")-$(safe_name "$SESSION_ID")-$RUN_STAMP"
fi
mkdir -p "$OUT_DIR"

cleanup_tmp_files() {
  [ -n "${OUT_DIR:-}" ] && [ -d "$OUT_DIR" ] || return 0
  find "$OUT_DIR" -name '*.tmp' -type f -delete 2>/dev/null || true
}

trap cleanup_tmp_files EXIT

run() {
  local name="$1"
  shift
  echo ">>> $name"
  {
    echo "### $name"
    printf '$'
    printf ' %q' "$@"
    echo
    "$@"
  } > "$OUT_DIR/$name.txt" 2>&1 || true
}

run_sh() {
  local name="$1"
  shift
  echo ">>> $name"
  {
    echo "### $name"
    echo "\$ $*"
    bash -lc "$*"
  } > "$OUT_DIR/$name.txt" 2>&1 || true
}

copy_small_file() {
  local src="$1"
  local dest="$2"
  [ -f "$src" ] || return 0
  local size
  size="$(file_size "$src" | tr -d ' ')"
  case "$size" in
    ''|*[!0-9]*) size=0 ;;
  esac
  if [ "$size" -le "$MAX_AGENT_FILE_BYTES" ]; then
    mkdir -p "$(dirname "$dest")"
    cp "$src" "$dest" 2>/dev/null || true
  else
    echo "Skipped large file ($size bytes): $src" >> "$OUT_DIR/skipped-agent-files.txt"
  fi
}

api_url() {
  local path="$1"
  case "$path" in
    /*) ;;
    *) path="/$path" ;;
  esac
  printf '%s%s%s' "${GATEWAY_URL%/}" "$GATEWAY_API_PREFIX" "$path"
}

curl_to_file() {
  local name="$1"
  local url="$2"
  shift 2
  if ! command -v curl >/dev/null 2>&1; then
    echo "curl not found" > "$OUT_DIR/$name.err"
    return 0
  fi
  local status
  status="$(
    curl -sS \
      -D "$OUT_DIR/$name.headers" \
      -o "$OUT_DIR/$name.json" \
      -w '%{http_code}' \
      "$@" \
      "$url" \
      2> "$OUT_DIR/$name.err" || true
  )"
  printf '%s\n' "$status" > "$OUT_DIR/$name.status"
  printf '%s\n' "$url" > "$OUT_DIR/$name.url"
  case "$status" in
    2*|3*) ;;
    *)
      {
        echo "HTTP request returned status $status"
        echo "URL: $url"
      } >> "$OUT_DIR/$name.err"
      ;;
  esac
}

echo "Collecting session debug bundle to: $OUT_DIR"

cat > "$OUT_DIR/context.txt" <<EOF
USER_ID=$USER_ID
AGENT_ID=$AGENT_ID
SESSION_ID=$SESSION_ID
ROOT=$ROOT
GATEWAY_ROOT=$GATEWAY_ROOT
DB=$DB
GATEWAY_URL=$GATEWAY_URL
GATEWAY_API_PREFIX=$GATEWAY_API_PREFIX
GATEWAY_JAR=$GATEWAY_JAR
GOOSED_PORT=$GOOSED_PORT
OUT_DIR=$OUT_DIR
FETCH_API=$FETCH_API
CREATE_ARCHIVE=$CREATE_ARCHIVE
COPY_WORKDIR=$COPY_WORKDIR
COPY_GOOSED_LOGS=$COPY_GOOSED_LOGS
collected_at=$(iso_now)
hostname=$(hostname)
gateway_secret_set=$([ -n "$GATEWAY_SECRET" ] && echo true || echo false)
goosed_secret_set=$([ -n "$GOOSED_SECRET" ] && echo true || echo false)
EOF

run_sh "env-summary" "pwd; uname -a; date '+%Y-%m-%dT%H:%M:%S%z'; command -v sqlite3 || true; command -v jq || true; command -v curl || true; command -v java || true; command -v jar || true; command -v javap || true; command -v git || true"
run_sh "processes" "ps -ef | grep -E 'gateway-service|goosed|opsfactory|java|vite|node' | grep -v grep || true"
run_sh "ports" "(command -v lsof >/dev/null 2>&1 && lsof -nP -iTCP:3000 -iTCP:5173 ${GOOSED_PORT:+-iTCP:$GOOSED_PORT} -sTCP:LISTEN) || true"

if [ -d "$ROOT/.git" ]; then
  run_sh "git-summary" "cd '$ROOT' && git rev-parse --show-toplevel && git rev-parse --abbrev-ref HEAD && git rev-parse HEAD && git status --short"
fi

AGENT_RUNTIME_DIR="$GATEWAY_ROOT/users/$USER_ID/agents/$AGENT_ID"
BASE_AGENT_DIR="$GATEWAY_ROOT/agents/$AGENT_ID"

run_sh "agent-runtime-tree" "find '$AGENT_RUNTIME_DIR' -maxdepth 4 -print 2>/dev/null | sort || true"
run_sh "agent-base-tree" "find '$BASE_AGENT_DIR' -maxdepth 3 -print 2>/dev/null | sort || true"

mkdir -p "$OUT_DIR/agent-context/runtime" "$OUT_DIR/agent-context/base"
if [ -d "$AGENT_RUNTIME_DIR" ]; then
  find "$AGENT_RUNTIME_DIR" -maxdepth 2 -type f \
    \( -name 'AGENTS.md' -o -name '*.md' -o -name '*.yaml' -o -name '*.yml' -o -name '*.json' -o -name '*.txt' \) \
    ! -path '*/data/*' ! -path '*/state/*' ! -path '*/home/*' ! -path '*/uploads/*' \
    -print 2>/dev/null | while IFS= read -r file; do
      rel="${file#"$AGENT_RUNTIME_DIR/"}"
      copy_small_file "$file" "$OUT_DIR/agent-context/runtime/$rel"
    done
fi

if [ -d "$BASE_AGENT_DIR" ]; then
  find "$BASE_AGENT_DIR" -maxdepth 2 -type f \
    \( -name 'AGENTS.md' -o -name '*.md' -o -name '*.yaml' -o -name '*.yml' -o -name '*.json' -o -name '*.txt' \) \
    -print 2>/dev/null | while IFS= read -r file; do
      rel="${file#"$BASE_AGENT_DIR/"}"
      copy_small_file "$file" "$OUT_DIR/agent-context/base/$rel"
    done
fi

if [ -z "$GATEWAY_JAR" ]; then
  DETECTED_JAR="$(ps -ef | tr -s ' ' | grep -E 'java .*gateway-service.*\.jar|java .*-jar .*gateway[^ ]*\.jar' | grep -v grep | sed -n 's/.*-jar \([^ ]*gateway[^ ]*\.jar\).*/\1/p' | head -1 || true)"
  if [ -n "$DETECTED_JAR" ]; then
    GATEWAY_JAR="$DETECTED_JAR"
    echo "GATEWAY_JAR_DETECTED=$GATEWAY_JAR" >> "$OUT_DIR/context.txt"
  fi
fi

if [ -n "$GATEWAY_JAR" ] && [ -f "$GATEWAY_JAR" ]; then
  run_sh "gateway-jar-fingerprint" "ls -l '$GATEWAY_JAR'; sha256sum '$GATEWAY_JAR' 2>/dev/null || shasum -a 256 '$GATEWAY_JAR' 2>/dev/null || true"
  run_sh "gateway-jar-manifest" "tmp='$OUT_DIR/jarcheck-manifest'; rm -rf \"\$tmp\"; mkdir -p \"\$tmp\"; (cd \"\$tmp\" && jar xf '$GATEWAY_JAR' META-INF/MANIFEST.MF 2>/dev/null && sed -n '1,200p' META-INF/MANIFEST.MF) || true"
  run_sh "gateway-jar-session-classes" "jar tf '$GATEWAY_JAR' | grep -E 'Session|Message|Thread|Conversation' | sed -n '1,300p' || true"
else
  echo "Gateway jar not found or not supplied: GATEWAY_JAR=$GATEWAY_JAR" > "$OUT_DIR/gateway-jar-missing.txt"
fi

THREAD_ID=""
WORKING_DIR=""
SESSION_MIN_CREATED=""
SESSION_MAX_CREATED=""
SESSION_MIN_DATE_UTC=""
SESSION_MAX_DATE_UTC=""

if [ -f "$DB" ]; then
  run_sh "db-file" "ls -l '$DB' '$DB-wal' '$DB-shm' 2>/dev/null || true; sqlite3 '$DB' '.tables'; sqlite3 '$DB' '.schema'"

  if command -v sqlite3 >/dev/null 2>&1; then
    sqlite3 "$DB" ".backup '$OUT_DIR/sessions.db.snapshot'" > "$OUT_DIR/db-backup.txt" 2>&1 || true

    THREAD_ID="$(sqlite3 "$DB" "select coalesce(thread_id, '') from sessions where id = '$SESSION_SQL' limit 1;" 2>/dev/null || true)"
    WORKING_DIR="$(sqlite3 "$DB" "select coalesce(working_dir, '') from sessions where id = '$SESSION_SQL' limit 1;" 2>/dev/null || true)"
    SESSION_MIN_CREATED="$(sqlite3 "$DB" "select coalesce(min(created_timestamp), '') from messages where session_id = '$SESSION_SQL';" 2>/dev/null || true)"
    SESSION_MAX_CREATED="$(sqlite3 "$DB" "select coalesce(max(created_timestamp), '') from messages where session_id = '$SESSION_SQL';" 2>/dev/null || true)"
    SESSION_MIN_DATE_UTC="$(sqlite3 "$DB" "select coalesce(date(min(created_timestamp), 'unixepoch'), '') from messages where session_id = '$SESSION_SQL';" 2>/dev/null || true)"
    SESSION_MAX_DATE_UTC="$(sqlite3 "$DB" "select coalesce(date(max(created_timestamp), 'unixepoch'), '') from messages where session_id = '$SESSION_SQL';" 2>/dev/null || true)"
    THREAD_SQL="$(sql_escape "$THREAD_ID")"

    sqlite3 -header -csv "$DB" "
select *
from sessions
where id = '$SESSION_SQL';
" > "$OUT_DIR/db-session.csv" 2> "$OUT_DIR/db-session.err" || true

    sqlite3 -json "$DB" "
select *
from sessions
where id = '$SESSION_SQL';
" > "$OUT_DIR/db-session.json" 2> "$OUT_DIR/db-session-json.err" || true

    sqlite3 -json "$DB" "
select *
from messages
where session_id = '$SESSION_SQL'
order by id;
" > "$OUT_DIR/db-messages-by-id.json" 2> "$OUT_DIR/db-messages-by-id-json.err" || true

    sqlite3 -json "$DB" "
select *
from messages
where session_id = '$SESSION_SQL'
order by created_timestamp, id;
" > "$OUT_DIR/db-messages-by-created.json" 2> "$OUT_DIR/db-messages-by-created-json.err" || true

    sqlite3 -header -csv "$DB" "
select
  id,
  message_id,
  role,
  created_timestamp,
  datetime(created_timestamp, 'unixepoch') as created_time,
  timestamp as inserted_time,
  tokens,
  length(content_json) as content_bytes,
  substr(content_json, 1, $PREVIEW_CHARS) as content_preview,
  metadata_json
from messages
where session_id = '$SESSION_SQL'
order by id;
" > "$OUT_DIR/db-messages-summary-by-id.csv" 2> "$OUT_DIR/db-messages-summary-by-id.err" || true

    sqlite3 -header -csv "$DB" "
select
  id,
  message_id,
  role,
  created_timestamp,
  datetime(created_timestamp, 'unixepoch') as created_time,
  timestamp as inserted_time,
  tokens,
  length(content_json) as content_bytes,
  substr(content_json, 1, $PREVIEW_CHARS) as content_preview,
  metadata_json
from messages
where session_id = '$SESSION_SQL'
order by created_timestamp, id;
" > "$OUT_DIR/db-messages-summary-by-created.csv" 2> "$OUT_DIR/db-messages-summary-by-created.err" || true

    sqlite3 -header -csv "$DB" "
select
  role,
  count(*) as count,
  min(id) as min_id,
  max(id) as max_id,
  min(created_timestamp) as min_created,
  datetime(min(created_timestamp), 'unixepoch') as min_created_time,
  max(created_timestamp) as max_created,
  datetime(max(created_timestamp), 'unixepoch') as max_created_time,
  sum(coalesce(tokens, 0)) as tokens
from messages
where session_id = '$SESSION_SQL'
group by role
order by role;
" > "$OUT_DIR/db-role-summary.csv" 2> "$OUT_DIR/db-role-summary.err" || true

    sqlite3 -header -csv "$DB" "
select
  timestamp as inserted_time,
  count(*) as count,
  min(id) as min_id,
  max(id) as max_id,
  min(created_timestamp) as min_created,
  datetime(min(created_timestamp), 'unixepoch') as min_created_time,
  max(created_timestamp) as max_created,
  datetime(max(created_timestamp), 'unixepoch') as max_created_time
from messages
where session_id = '$SESSION_SQL'
group by timestamp
order by timestamp;
" > "$OUT_DIR/db-inserted-time-groups.csv" 2> "$OUT_DIR/db-inserted-time-groups.err" || true

    # Group messages into user-visible turns. Tool responses are stored as user
    # messages in the DB, so only non-toolResponse user rows start a new turn.
    sqlite3 -header -csv "$DB" "
with classified as (
  select
    id,
    message_id,
    role,
    created_timestamp,
    timestamp,
    content_json,
    case
      when role = 'user' and content_json not like '%\"type\":\"toolResponse\"%' then 1
      else 0
    end as is_human_turn,
    case when content_json like '%\"type\":\"toolRequest\"%' then 1 else 0 end as has_tool_request,
    case when content_json like '%\"type\":\"toolResponse\"%' then 1 else 0 end as has_tool_response
  from messages
  where session_id = '$SESSION_SQL'
),
turns as (
  select
    *,
    sum(is_human_turn) over (order by id rows between unbounded preceding and current row) as turn_no
  from classified
)
select
  turn_no,
  min(id) as min_id,
  max(id) as max_id,
  count(*) as message_count,
  sum(case when role = 'user' then 1 else 0 end) as user_messages,
  sum(case when role = 'assistant' then 1 else 0 end) as assistant_messages,
  sum(has_tool_request) as messages_with_tool_requests,
  sum(has_tool_response) as messages_with_tool_responses,
  min(created_timestamp) as min_created,
  datetime(min(created_timestamp), 'unixepoch') as min_created_time,
  max(created_timestamp) as max_created,
  datetime(max(created_timestamp), 'unixepoch') as max_created_time,
  substr(group_concat(role || ':' || id, ' -> '), 1, 1000) as role_id_path
from turns
group by turn_no
order by turn_no;
" > "$OUT_DIR/db-turn-summary.csv" 2> "$OUT_DIR/db-turn-summary.err" || true

    sqlite3 -header -csv "$DB" "
select
  id,
  message_id,
  role,
  created_timestamp,
  datetime(created_timestamp, 'unixepoch') as created_time,
  timestamp as inserted_time,
  case when content_json like '%\"type\":\"toolRequest\"%' then 1 else 0 end as has_tool_request,
  case when content_json like '%\"type\":\"toolResponse\"%' then 1 else 0 end as has_tool_response,
  length(content_json) as content_bytes,
  substr(content_json, 1, $PREVIEW_CHARS) as content_preview,
  metadata_json
from messages
where session_id = '$SESSION_SQL'
order by id desc
limit 40;
" > "$OUT_DIR/db-last-40-messages.csv" 2> "$OUT_DIR/db-last-40-messages.err" || true

    sqlite3 -header -csv "$DB" "
select
  id,
  message_id,
  role,
  created_timestamp,
  datetime(created_timestamp, 'unixepoch') as created_time,
  timestamp as inserted_time,
  tokens,
  length(content_json) as content_bytes,
  substr(content_json, 1, $PREVIEW_CHARS) as content_preview,
  metadata_json
from messages
where session_id = '$SESSION_SQL'
  and role = 'user'
  and content_json not like '%\"type\":\"toolResponse\"%'
order by id;
" > "$OUT_DIR/db-human-user-messages.csv" 2> "$OUT_DIR/db-human-user-messages.err" || true

    if [ -n "$THREAD_ID" ]; then
      sqlite3 -header -csv "$DB" "
select *
from threads
where id = '$THREAD_SQL';
" > "$OUT_DIR/db-thread.csv" 2> "$OUT_DIR/db-thread.err" || true

      sqlite3 -json "$DB" "
select *
from thread_messages
where thread_id = '$THREAD_SQL'
order by id;
" > "$OUT_DIR/db-thread-messages.json" 2> "$OUT_DIR/db-thread-messages-json.err" || true

      sqlite3 -header -csv "$DB" "
select
  id,
  thread_id,
  session_id,
  message_id,
  role,
  created_timestamp,
  datetime(created_timestamp, 'unixepoch') as created_time,
  length(content_json) as content_bytes,
  substr(content_json, 1, $PREVIEW_CHARS) as content_preview,
  metadata_json
from thread_messages
where thread_id = '$THREAD_SQL'
order by id;
" > "$OUT_DIR/db-thread-messages-summary.csv" 2> "$OUT_DIR/db-thread-messages-summary.err" || true
    else
      sqlite3 -json "$DB" "
select *
from thread_messages
where session_id = '$SESSION_SQL'
order by id;
" > "$OUT_DIR/db-thread-messages.json" 2> "$OUT_DIR/db-thread-messages-json.err" || true
    fi
  fi
else
  echo "DB not found: $DB" > "$OUT_DIR/db-missing.txt"
fi

cat >> "$OUT_DIR/context.txt" <<EOF
THREAD_ID=$THREAD_ID
WORKING_DIR=$WORKING_DIR
SESSION_MIN_CREATED=$SESSION_MIN_CREATED
SESSION_MAX_CREATED=$SESSION_MAX_CREATED
SESSION_MIN_DATE_UTC=$SESSION_MIN_DATE_UTC
SESSION_MAX_DATE_UTC=$SESSION_MAX_DATE_UTC
EOF

if command -v jq >/dev/null 2>&1 && [ -s "$OUT_DIR/db-messages-by-id.json" ]; then
  jq '
    def parsed($v): ($v | fromjson? // $v);
    map(. + {
      content: parsed(.content_json // "[]"),
      metadata: parsed(.metadata_json // "{}")
    })
  ' "$OUT_DIR/db-messages-by-id.json" > "$OUT_DIR/db-messages-expanded.json" 2> "$OUT_DIR/db-messages-expanded.err" || true

  jq -r --argjson preview "$PREVIEW_CHARS" '
    def parsed($v): ($v | fromjson? // []);
    def content_array: parsed(.content_json // "[]") | if type == "array" then . elif type == "object" then [.] else [] end;
    def part_label:
      if type != "object" then tostring
      elif .type == "text" then (.text // "")
      elif .type == "reasoning" then (.text // .reasoning // "")
      elif .type == "thinking" then (.thinking // .text // "")
      elif .type == "toolRequest" then ("toolRequest:" + (.toolCall.value.name // .toolCall.name // .id // ""))
      elif .type == "toolResponse" then ("toolResponse:" + (.id // .toolCallId // ""))
      else (.type // "")
      end;
    def preview_text:
      (content_array | map(part_label) | join(" ") | gsub("[[:space:]]+"; " ") | .[0:$preview]);
    ([
      "db_id",
      "message_id",
      "role",
      "created_timestamp",
      "created_time",
      "inserted_time",
      "tokens",
      "content_types",
      "preview"
    ] | @tsv),
    (.[] | [
      (.id // ""),
      (.message_id // ""),
      (.role // ""),
      (.created_timestamp // ""),
      (if .created_timestamp then (.created_timestamp | strftime("%Y-%m-%dT%H:%M:%SZ")) else "" end),
      (.timestamp // ""),
      (.tokens // ""),
      (content_array | map(if type == "object" then (.type // "unknown") else type end) | join(",")),
      preview_text
    ] | @tsv)
  ' "$OUT_DIR/db-messages-by-id.json" > "$OUT_DIR/db-conversation-timeline.tsv" 2> "$OUT_DIR/db-conversation-timeline.err" || true

  jq -r --argjson limit "$TRANSCRIPT_CHARS_PER_PART" '
    def parsed($v): ($v | fromjson? // []);
    def content_array: parsed(.content_json // "[]") | if type == "array" then . elif type == "object" then [.] else [] end;
    def trunc:
      tostring | if length > $limit then .[0:$limit] + "\n...[truncated in transcript; full content is in db-messages-by-id.json]" else . end;
    def part_text:
      if type != "object" then tostring
      elif .type == "text" then (.text // "" | trunc)
      elif .type == "reasoning" then ("[reasoning]\n" + ((.text // .reasoning // "") | trunc))
      elif .type == "thinking" then ("[thinking]\n" + ((.thinking // .text // "") | trunc))
      elif .type == "toolRequest" then ("[toolRequest] " + (.toolCall.value.name // .toolCall.name // .id // "") + "\n" + ((.toolCall.value.arguments // .toolCall.arguments // .arguments // "") | trunc))
      elif .type == "toolResponse" then ("[toolResponse] " + (.id // .toolCallId // "") + "\n" + ((.text // .content // .output // .error // .) | trunc))
      else ("[" + (.type // "unknown") + "]\n" + (. | trunc))
      end;
    .[] |
      "## db_id=\(.id // "") role=\(.role // "") message_id=\(.message_id // "") created=\(.created_timestamp // "") inserted=\(.timestamp // "")\n\n" +
      (content_array | map(part_text) | join("\n\n")) +
      "\n"
  ' "$OUT_DIR/db-messages-by-id.json" > "$OUT_DIR/db-transcript.md" 2> "$OUT_DIR/db-transcript.err" || true
fi

REQ_ID="session-debug-${SESSION_ID}-${RUN_STAMP}"
printf '%s\n' "$REQ_ID" > "$OUT_DIR/gateway-request-id.txt"

HEADERS=(-H "X-Request-Id: $REQ_ID" -H "x-user-id: $USER_ID")
if [ -n "$GATEWAY_SECRET" ]; then
  HEADERS+=(-H "x-secret-key: $GATEWAY_SECRET")
fi

if [ "$FETCH_API" = "1" ]; then
  curl_to_file "gateway-agent-session" "$(api_url "/agents/$AGENT_ID/sessions/$SESSION_ID")" "${HEADERS[@]}"
  curl_to_file "gateway-global-session" "$(api_url "/sessions/$SESSION_ID?agentId=$AGENT_ID")" "${HEADERS[@]}"
  curl_to_file "gateway-file-capsules" "$(api_url "/agents/$AGENT_ID/file-capsules?sessionId=$SESSION_ID")" "${HEADERS[@]}"

  if [ -n "$GOOSED_PORT" ] && [ -n "$GOOSED_SECRET" ]; then
    curl_to_file "goosed-session" "http://127.0.0.1:$GOOSED_PORT/sessions/$SESSION_ID" -H "x-secret-key: $GOOSED_SECRET"
  else
    echo "Skipped direct goosed API. Set GOOSED_PORT and GOOSED_SECRET to enable." > "$OUT_DIR/goosed-skipped.txt"
  fi
else
  echo "Skipped API collection because FETCH_API=$FETCH_API." > "$OUT_DIR/api-skipped.txt"
fi

if command -v jq >/dev/null 2>&1; then
  for json in "$OUT_DIR"/gateway-agent-session.json "$OUT_DIR"/gateway-global-session.json "$OUT_DIR"/goosed-session.json; do
    [ -s "$json" ] || continue
    base="$(basename "$json" .json)"
    jq -r --argjson preview "$PREVIEW_CHARS" '
      def content_array($v): $v | if type == "array" then . elif type == "object" then [.] else [] end;
      def part_label:
        if type != "object" then tostring
        elif .type == "text" then (.text // "")
        elif .type == "reasoning" then (.text // .reasoning // "")
        elif .type == "thinking" then (.thinking // .text // "")
        elif .type == "toolRequest" then ("toolRequest:" + (.toolCall.value.name // .toolCall.name // .id // ""))
        elif .type == "toolResponse" then ("toolResponse:" + (.id // .toolCallId // ""))
        else (.type // "")
        end;
      ([
        "index",
        "id",
        "role",
        "created",
        "userVisible",
        "content_types",
        "preview"
      ] | @tsv),
      ((.conversation // []) | to_entries[] | [
        .key,
        (.value.id // ""),
        (.value.role // ""),
        (.value.created // ""),
        ((.value.metadata.userVisible // "") | tostring),
        (content_array(.value.content // []) | map(if type == "object" then (.type // "unknown") else type end) | join(",")),
        (content_array(.value.content // []) | map(part_label) | join(" ") | gsub("[[:space:]]+"; " ") | .[0:$preview])
      ] | @tsv)
    ' "$json" > "$OUT_DIR/$base-conversation.tsv" 2> "$OUT_DIR/$base-conversation.err" || true
  done
fi

if [ -n "$WORKING_DIR" ]; then
  run_sh "working-dir-files" "test -d '$WORKING_DIR' && { echo 'WORKING_DIR=$WORKING_DIR'; du -sh '$WORKING_DIR' 2>/dev/null || true; find '$WORKING_DIR' -maxdepth 5 -type f -exec ls -lh {} + 2>/dev/null | sed -n '1,1000p'; } || echo 'Working directory not found: $WORKING_DIR'"
  if [ "$COPY_WORKDIR" = "1" ] && [ -d "$WORKING_DIR" ]; then
    WORKDIR_BYTES="$(du -sk "$WORKING_DIR" 2>/dev/null | awk '{print $1 * 1024}' || echo 0)"
    if [ "${WORKDIR_BYTES:-0}" -le "$MAX_WORKDIR_COPY_BYTES" ]; then
      mkdir -p "$OUT_DIR/workdir-copy"
      tar -czf "$OUT_DIR/workdir-copy/workdir.tar.gz" -C "$(dirname "$WORKING_DIR")" "$(basename "$WORKING_DIR")" > "$OUT_DIR/workdir-copy.txt" 2>&1 || true
    else
      echo "Skipped COPY_WORKDIR because working dir is $WORKDIR_BYTES bytes, limit is $MAX_WORKDIR_COPY_BYTES." > "$OUT_DIR/workdir-copy-skipped.txt"
    fi
  fi
else
  echo "No working_dir found for session." > "$OUT_DIR/working-dir-missing.txt"
fi

if [ -d "$AGENT_RUNTIME_DIR/uploads" ]; then
  run_sh "uploads-files" "find '$AGENT_RUNTIME_DIR/uploads' -maxdepth 5 -type f -exec ls -lh {} + 2>/dev/null | sed -n '1,1000p' || true"
fi

LOG_PATTERN_FILE="$OUT_DIR/log-patterns.txt"
{
  printf '%s\n' "$SESSION_ID"
  printf '%s\n' "$REQ_ID"
  printf '%s\n' "sessionId=$SESSION_ID"
  printf '%s\n' "session_id=$SESSION_ID"
  printf '%s\n' "\"session_id\":\"$SESSION_ID\""
  printf '%s\n' "\"sessionId\":\"$SESSION_ID\""
} > "$LOG_PATTERN_FILE"

collect_log_matches() {
  local log="$1"
  [ -f "$log" ] || return 0
  local out
  local tmp
  out="$OUT_DIR/log-$(safe_name "$log").txt"
  tmp="$out.tmp"
  grep -F -f "$LOG_PATTERN_FILE" "$log" 2>/dev/null | tail -n "$LOG_TAIL_LINES" > "$tmp" || true
  if [ -s "$tmp" ]; then
    {
      echo "### $log"
      cat "$tmp"
    } > "$out"
  fi
  rm -f "$tmp"
}

add_log_candidate() {
  local list_file="$1"
  local log="$2"
  local reason="$3"
  [ -f "$log" ] || return 0
  if ! grep -Fqx "$log" "$list_file" 2>/dev/null; then
    printf '%s\n' "$log" >> "$list_file"
  fi
  printf '%s\t%s\t%s\n' "$reason" "$(file_size "$log" | tr -d ' ')" "$log" >> "$OUT_DIR/goosed-log-candidate-reasons.tsv"
}

for log in \
  "$GATEWAY_ROOT/logs/gateway.log" \
  "$GATEWAY_ROOT/logs/gateway-stdout-stderr.log" \
  "$ROOT/gateway/logs/gateway.log" \
  "$ROOT/gateway/logs/gateway-stdout-stderr.log"
do
  collect_log_matches "$log"
done

if [ -d "$AGENT_RUNTIME_DIR/state/logs" ]; then
  find "$AGENT_RUNTIME_DIR/state/logs" -type f \
    \( -name '*.log' -o -name '*.txt' -o -name '*.jsonl' \) \
    -print 2>/dev/null | while IFS= read -r log; do
      collect_log_matches "$log"
    done
fi

GOOSED_LOG_ROOT="$AGENT_RUNTIME_DIR/state/logs/server"
GOOSED_CANDIDATES="$OUT_DIR/goosed-log-candidates.txt"
: > "$GOOSED_CANDIDATES"
: > "$OUT_DIR/goosed-log-candidate-reasons.tsv"

if [ "$COPY_GOOSED_LOGS" = "1" ] && [ -d "$GOOSED_LOG_ROOT" ]; then
  echo ">>> goosed-full-logs"
  find "$GOOSED_LOG_ROOT" -type f \
    \( -name '*.log' -o -name '*.txt' -o -name '*.jsonl' \) \
    -print 2>/dev/null | while IFS= read -r log; do
      if grep -Fq "$SESSION_ID" "$log" 2>/dev/null; then
        add_log_candidate "$GOOSED_CANDIDATES" "$log" "contains-session-id"
      elif [ -n "$SESSION_MIN_DATE_UTC" ] && printf '%s' "$log" | grep -Fq "/$SESSION_MIN_DATE_UTC/"; then
        add_log_candidate "$GOOSED_CANDIDATES" "$log" "same-utc-date-as-session-start"
      elif [ -n "$SESSION_MAX_DATE_UTC" ] && printf '%s' "$log" | grep -Fq "/$SESSION_MAX_DATE_UTC/"; then
        add_log_candidate "$GOOSED_CANDIDATES" "$log" "same-utc-date-as-session-end"
      fi
    done

  mkdir -p "$OUT_DIR/goosed-full-logs"
  GOOSED_COPIED_BYTES=0
  while IFS= read -r log; do
    [ -f "$log" ] || continue
    size="$(file_size "$log" | tr -d ' ')"
    case "$size" in
      ''|*[!0-9]*) size=0 ;;
    esac
    next_total=$((GOOSED_COPIED_BYTES + size))
    if [ "$next_total" -gt "$MAX_GOOSED_LOG_BYTES" ]; then
      printf 'Skipped because copy limit would be exceeded: size=%s limit=%s path=%s\n' \
        "$size" "$MAX_GOOSED_LOG_BYTES" "$log" >> "$OUT_DIR/goosed-full-logs-skipped.txt"
      continue
    fi
    rel="${log#"$GOOSED_LOG_ROOT/"}"
    dest="$OUT_DIR/goosed-full-logs/$rel"
    mkdir -p "$(dirname "$dest")"
    cp "$log" "$dest" 2>/dev/null || true
    GOOSED_COPIED_BYTES="$next_total"
  done < "$GOOSED_CANDIDATES"
  printf '%s\n' "$GOOSED_COPIED_BYTES" > "$OUT_DIR/goosed-full-logs-copied-bytes.txt"

  {
    echo "### copied goosed logs"
    find "$OUT_DIR/goosed-full-logs" -type f -exec ls -lh {} + 2>/dev/null | sort || true
    echo
    echo "### candidate reasons"
    sed -n '1,500p' "$OUT_DIR/goosed-log-candidate-reasons.tsv" 2>/dev/null || true
  } > "$OUT_DIR/goosed-full-logs-index.txt"

  {
    echo "### goosed lifecycle/termination/session signals"
    find "$OUT_DIR/goosed-full-logs" -type f -print 2>/dev/null | sort | while IFS= read -r copied_log; do
      grep -Ein \
        "session_id=$SESSION_ID|session\\.id=$SESSION_ID|session $SESSION_ID|Session loaded|Restoring evicted session|Session started|Session completed|exit_type=|shutdown|cancel|Cancelled|Closed|timeout|timed out|panic|error|failed|provider|WAITING_LLM|WAITING_TOOL|Tool call|evict|dropped|EOF|broken pipe|connection" \
        "$copied_log" 2>/dev/null | sed "s#^#$copied_log:#" || true
    done
  } > "$OUT_DIR/goosed-session-signals.txt"
else
  echo "Skipped full goosed log copy. COPY_GOOSED_LOGS=$COPY_GOOSED_LOGS GOOSED_LOG_ROOT=$GOOSED_LOG_ROOT" > "$OUT_DIR/goosed-full-logs-skipped.txt"
fi

find "$OUT_DIR" -name '*.err' -type f -size 0 -print 2>/dev/null | while IFS= read -r empty_err; do
  rm -f "$empty_err"
done

{
  echo "# Session Debug Bundle"
  echo
  echo "- collected_at: $(iso_now)"
  echo "- user_id: $USER_ID"
  echo "- agent_id: $AGENT_ID"
  echo "- session_id: $SESSION_ID"
  echo "- thread_id: ${THREAD_ID:-<none>}"
  echo "- db: $DB"
  echo "- gateway_url: $GATEWAY_URL$GATEWAY_API_PREFIX"
  echo "- request_id: $REQ_ID"
  echo "- working_dir: ${WORKING_DIR:-<none>}"
  echo
  echo "## Most useful files"
  echo
  echo "- context.txt: collection context with secrets redacted"
  echo "- db-session.json / db-session.csv: session row"
  echo "- db-messages-by-id.json: full raw message rows ordered by DB insertion id"
  echo "- db-messages-expanded.json: message rows with parsed content_json and metadata_json"
  echo "- db-conversation-timeline.tsv: compact chronological review table"
  echo "- db-transcript.md: readable transcript, with long parts truncated but raw JSON preserved"
  echo "- db-turn-summary.csv: per-human-turn grouping for long task continuity analysis"
  echo "- db-last-40-messages.csv: tail of the session, useful when the agent stopped near the end"
  echo "- db-thread-messages.json: associated thread messages when available"
  echo "- gateway-agent-session.json: gateway API session response"
  echo "- gateway-agent-session-conversation.tsv: gateway response timeline"
  echo "- gateway-file-capsules.json: files exposed by the gateway for this session"
  echo "- goosed-full-logs/: full goosed logs that contain the session id or fall on the session UTC date"
  echo "- goosed-session-signals.txt: lifecycle, provider, tool, shutdown, timeout, and error lines from copied goosed logs"
  echo "- log-*.txt: focused matching gateway/goosed log lines"
  echo "- sessions.db.snapshot: sqlite backup of the session database"
  echo
  echo "## Counts"
  echo
  sed -n '1,80p' "$OUT_DIR/db-role-summary.csv" 2>/dev/null || true
  echo
  echo "## Turn Summary"
  echo
  sed -n '1,120p' "$OUT_DIR/db-turn-summary.csv" 2>/dev/null || true
  echo
  echo "## Timeline Preview"
  echo
  sed -n '1,80p' "$OUT_DIR/db-conversation-timeline.tsv" 2>/dev/null || true
  echo
  echo "## Bundle Files"
  find "$OUT_DIR" -maxdepth 2 -type f -print | sort
} > "$OUT_DIR/SUMMARY.md"

if [ "$CREATE_ARCHIVE" = "1" ]; then
  tar -czf "$OUT_DIR.tar.gz" -C "$(dirname "$OUT_DIR")" "$(basename "$OUT_DIR")"
fi

echo
echo "Done."
echo "Directory: $OUT_DIR"
if [ "$CREATE_ARCHIVE" = "1" ]; then
  echo "Archive:   $OUT_DIR.tar.gz"
fi
