#!/bin/bash
set -e

echo "Preparing Production Backend..."

APP_DIR="/opt/production-backend"
ENV_FILE="/etc/production-backend.env"

mkdir -p "$APP_DIR"

chown -R ubuntu:ubuntu "$APP_DIR"

echo "Creating environment file from SSM Parameter Store..."

aws ssm get-parameter \
    --name "/cloudfleet/production/env" \
    --with-decryption \
    --query "Parameter.Value" \
    --output text > "$ENV_FILE"

chmod 600 "$ENV_FILE"

echo "Environment file created successfully."

echo "Production Backend installation completed."