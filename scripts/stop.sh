#!/bin/bash

echo "Stopping Production Backend..."

pkill -f 'production-backend-.*\.jar' || true

echo "Application stopped."