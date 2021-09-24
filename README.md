# Alpakka-with-Akka-Stream

**HOW TO RUN** 

First you need to clone this project on you local and start your kafka and cassandra on default port.
To run go to your project directory do sbt run, It will ask which class you want to run.
Multiple main classes detected. Select one to run:
 [1] com.knoldus.AppToKafka
 [2] com.knoldus.KafkaToCassandra
 [3] com.knoldus.KafkatoKafka
 
 Select the one which you want to run.
 


In this techhub tempalte ,I basically focused on Alpakka.

**Alpakka is an open source project, that is built on top of Akka-Streams to provide a DSL for reactive and stream-oriented programming. It can be used using Java and Scala both. It has built-in support for backpressure to handle the flow of data.**

**Alpakka provides us a number of connectors for different technologies through which we can create sources, sinks and flows for them. It basically provides API that integrates a technology with Akka-Streams. This makes it easier to work with Akka-Streams and different technologies together.**

In this template I created 3 applications , they are - 

# **1 ) AppToKafka**

In this AppToKafka application I am using my application as a Source and kafka as a Sink.
To run this application 

First we need to start zookeeper and broker of kafka and create a kafka topic - **"appToKafka"**

Command to create topic ->  **bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic appToKafka**

Now start kafka consumer for this topic using command -> **bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic appToKafka --from-beginning**


Now do **"sbt run"** and then type 1 and see your producer to see the output.

# 2) KafkaToKafka
In this AppToKafka application I am using Kafka as a Source and also Kafka as a Sink.
To run this application 

First we need to start zookeeper and broker of kafka and create two new kafka topic one for source and other for sink - "input" and "output"

Command to create topic ->  **bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic input**
                            **bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic output**
                            
Now start kafka producer for input topic using command -> **bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic input**  and produce some String msg to this topic 

Now start consumer for output topic ausing command -> **bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic output --from-beginning**

Do **"sbt run"** from you project directory and select the class and see the output to consumer console.

# 3) KafkaToCassandra
In this AppToKafka application I am using Kafka as a Source and also cassandra as a Sink.
To run this application 

To work with cassandra as a sink we have to create keyspace and table in cassandra by - 

To create keyspace - 
**CREATE KEYSPACE IF NOT EXISTS mykeyspace
WITH REPLICATION = {
  'class' : 'SimpleStrategy',
  'replication_factor': 1
};**

To create table -
**CREATE TABLE IF NOT EXISTS mykeyspace.demo (
  msg TEXT,
  PRIMARY KEY(msg)
);**

Now we need to start zookeeper and broker of kafka and create a new kafka topic "cassandrTopic"

Command to create topic ->  **bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic cassandrTopic**

Start your application now by doing **"sbt run"** and select 3 for running this class 

Now start kafka producer for 'cassandraTopic' topic using command -> **bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic cassandraTopic**  and produce some String msg to this topic.

Go and check your cassandra **mykeyspace.demo** table and you will get the data. 



