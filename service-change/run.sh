# --- Run the Docker Container ---
echo "Stopping and removing existing container if it exists..."
docker rm -f service-change-container || true

echo "Running the Docker container..."
docker run --name service-change-container -p 8082:8080 -p 9991:9990 -d banque/service-change:latest

echo "http://localhost:8082/service-change-web"
