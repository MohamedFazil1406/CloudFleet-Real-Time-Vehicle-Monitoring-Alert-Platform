#!/bin/bash
set -e

echo "Starting Production Backend..."

cd /opt/production-backend

JAR="/opt/production-backend/target/production-backend-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR" ]; then
    echo "ERROR: JAR not found: $JAR"
    exit 1
fi

nohup java -jar "$JAR" \
    > /var/log/production-backend.log 2>&1 &

echo "Production Backend started."