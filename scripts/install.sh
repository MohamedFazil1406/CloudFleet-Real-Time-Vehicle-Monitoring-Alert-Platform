#!/bin/bash
set -e

echo "Preparing application directory..."

mkdir -p /opt/production-backend

chown -R ubuntu:ubuntu /opt/production-backend

echo "Installation preparation completed."