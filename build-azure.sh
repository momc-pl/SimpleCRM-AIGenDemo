#!/bin/bash
# Build script for Azure deployment from Mac (ARM64) to Azure (AMD64)

set -e

echo "🔨 Building Docker image for Azure (AMD64 platform)..."

# Build with explicit AMD64 platform for Azure compatibility and push directly
docker buildx build --platform linux/amd64 -t simplecrmtstacroxi4dxbglpndc.azurecr.io/simplecrm-app:latest --push .

echo "🎉 Successfully built and pushed AMD64 image to Azure Container Registry!"