#!/bin/bash

echo "🚀 Starting TheMall services..."
echo "================================"

# Start all services
echo "📦 Starting Docker containers..."
docker-compose up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be ready..."

# Wait for MySQL
echo "🔄 Waiting for MySQL..."
while ! docker exec themall-mysql mysqladmin ping -h localhost --silent; do
    echo "   MySQL not ready yet, waiting..."
    sleep 3
done
echo "✅ MySQL is ready"

# Wait for MongoDB
echo "🔄 Waiting for MongoDB..."
while ! docker exec themall-mongodb mongosh --eval "db.runCommand('ping')" --quiet; do
    echo "   MongoDB not ready yet, waiting..."
    sleep 3
done
echo "✅ MongoDB is ready"

# Wait for Cassandra
echo "🔄 Waiting for Cassandra..."
while ! docker exec themall-cassandra cqlsh -e "describe cluster" > /dev/null 2>&1; do
    echo "   Cassandra not ready yet, waiting..."
    sleep 5
done
echo "✅ Cassandra is ready"

# Wait for Kafka
echo "🔄 Waiting for Kafka..."
while ! docker exec themall-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; do
    echo "   Kafka not ready yet, waiting..."
    sleep 3
done
echo "✅ Kafka is ready"

# Create databases and keyspaces
echo "🗄️  Setting up databases..."

# Create Payment database in MySQL
echo "📊 Creating payment_db database..."
docker exec themall-mysql mysql -uroot -ppassword -e "CREATE DATABASE IF NOT EXISTS payment_db;"
echo "✅ payment_db created"

# Create Cassandra keyspace
echo "📊 Creating order_service keyspace..."
docker exec themall-cassandra cqlsh -e "
CREATE KEYSPACE IF NOT EXISTS order_service 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
"
echo "✅ order_service keyspace created"

# Create Cassandra tables
echo "📊 Creating Cassandra tables..."
docker exec themall-cassandra cqlsh -e "
USE order_service;

CREATE TABLE IF NOT EXISTS orders (
    order_id text PRIMARY KEY,
    user_id text,
    status text,
    total_amount decimal,
    created_at timestamp
);

CREATE TABLE IF NOT EXISTS order_items (
    order_id text,
    item_id text,
    item_name text,
    quantity int,
    unit_price decimal,
    PRIMARY KEY (order_id, item_id)
);
"
echo "✅ Cassandra tables created"

# Create Kafka topics
echo "📊 Creating Kafka topics..."
docker exec themall-kafka kafka-topics --create --topic order-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --if-not-exists
docker exec themall-kafka kafka-topics --create --topic payment-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 --if-not-exists
echo "✅ Kafka topics created"

echo ""
echo "🎉 All services are ready!"
echo "================================"
echo "📋 Service endpoints:"
echo "   MySQL:     localhost:3306 (root/password)"
echo "   MongoDB:   localhost:27017"
echo "   Cassandra: localhost:9042"
echo "   Kafka:     localhost:9092"
echo ""
echo "🛠️  You can now start your microservices:"
echo "   Account Service:  mvn spring-boot:run (port 8081)"
echo "   Item Service:     mvn spring-boot:run (port 8082)"
echo "   Order Service:    mvn spring-boot:run (port 8083)"
echo "   Payment Service:  mvn spring-boot:run (port 8084)"
echo ""
echo "🔍 To check service status: docker-compose ps"
echo "🛑 To stop all services:    ./stop.sh"