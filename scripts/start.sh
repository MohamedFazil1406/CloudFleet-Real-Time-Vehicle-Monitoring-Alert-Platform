#!/bin/bash
set -e

echo "Starting Production Backend..."

APP_DIR="/opt/production-backend"
JAR="$APP_DIR/target/production-backend-0.0.1-SNAPSHOT.jar"
ENV_FILE="/etc/production-backend.env"
LOG="/var/log/production-backend.log"

cd "$APP_DIR"

if [ ! -f "$JAR" ]; then
    echo "ERROR: JAR not found: $JAR"
    exit 1
fi

if [ ! -f "$ENV_FILE" ]; then
    echo "ERROR: Environment file not found: $ENV_FILE"
    exit 1
fi

echo "Loading environment variables..."

set -a
source "$ENV_FILE"
set +a

echo "Starting Spring Boot..."

nohup java -jar "$JAR" \
    > "$LOG" 2>&1 &

echo $! > "$APP_DIR/application.pid"

sleep 5

if kill -0 "$(cat "$APP_DIR/application.pid")" 2>/dev/null; then
    echo "Production Backend started successfully."
else
    echo "ERROR: Application failed to start."
    cat "$LOG"
    exit 1
fi