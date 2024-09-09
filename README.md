# apache-kafka-streams

# About
apache-kafka-streams codebase walks through creating Kafka Streams, Producer and Consumer services using Apache Kafka API libraries.

# Setup ZooKeeper, Apache Kafka
Assumption: ZooKeeper and Apache Kafka is downloaded and installed locally.

1. Start ZooKeeper
- % cd kafka_2.13-3.7.1
- % bin/zookeeper-server-start.sh config/zookeeper.properties

2. Start Apache Kafka Server
- % cd kafka_2.13-3.7.1
- % bin/kafka-server-start.sh config/server.properties

# Create Topics
% bin/kafka-topics.sh --create \
--bootstrap-server localhost:9092 \
--replication-factor 1 \
--partitions 1 \
--topic customer-order-input

% bin/kafka-topics.sh --create \
--bootstrap-server localhost:9092 \
--replication-factor 1 \
--partitions 1 \
--topic customer-order-output \
--config cleanup.policy=compact

# Build
- ./gradlew clean build

# Run the application
- ./gradlew bootRun

# Test the application
1. Call Producer to publish messages to input topic.
- ``% curl 'http://localhost:8080/api/customers/customerOrder' \
  --header 'Content-Type: application/json' \
  --data '{
  "orderId": "444", "transactionEvent": "packaging-shipped"
  }'``

2. Call Kafka Streams to read input topic and sink the messages to output topic.
- ``% curl http://localhost:8080/api/customers/customerOrderStream``

3. Call Consumer to consume messages from the output topic.
- ``% curl http://localhost:8080/api/customers/customerOrderEvents``
