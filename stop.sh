#!/bin/bash

echo "🛑 Stopping TheMall services..."
echo "================================"

# Stop all containers
echo "📦 Stopping Docker containers..."
docker-compose down

echo "✅ All services stopped!"
echo ""
echo "💡 To remove all data volumes (WARNING: This will delete all data):"
echo "   docker-compose down -v"
echo ""
echo "🧹 To clean up unused Docker resources:"
echo "   docker system prune"