#!/bin/bash
set -e

echo "Preparing Production Backend..."

APP_DIR="/opt/production-backend"
ENV_FILE="/etc/production-backend.env"
SSM_PARAMETER="/cloudfleet/production/env"
AWS_REGION="ap-south-1"

echo "Creating application directory..."
mkdir -p "$APP_DIR"

echo "Installing AWS CLI..."
apt-get update -y
apt-get install -y awscli

echo "Fetching production environment from SSM Parameter Store..."

aws ssm get-parameter \
    --name "$SSM_PARAMETER" \
    --with-decryption \
    --region "$AWS_REGION" \
    --query "Parameter.Value" \
    --output text > "$ENV_FILE"

echo "Checking environment file..."

if [ ! -s "$ENV_FILE" ]; then
    echo "ERROR: Failed to create $ENV_FILE"
    exit 1
fi

chmod 600 "$ENV_FILE"
chown root:root "$ENV_FILE"

echo "Environment file created successfully."

echo "Preparing application directory permissions..."

chown -R ubuntu:ubuntu "$APP_DIR"

echo "Installation preparation completed successfully."