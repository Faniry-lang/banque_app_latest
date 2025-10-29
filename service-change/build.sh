#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Build the Java Project ---
echo "Building the entire project with Maven..."
mvn clean install

# --- Build the Docker Image ---
echo "Building the Docker image for service-change..."
docker build -t banque/service-change:latest .

