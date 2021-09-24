package com.knoldus

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Flow
import akka.stream.{ActorMaterializer, scaladsl}
import com.datastax.driver.core.{BoundStatement, Cluster, PreparedStatement}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import akka.stream.alpakka.cassandra.scaladsl.CassandraSink
object KafkaToCassandra extends App{

  implicit val system: ActorSystem = ActorSystem("AppToKafka")
  implicit  val materializer: ActorMaterializer = ActorMaterializer()

  val configProducer = ConfigFactory.load.getConfig("akka.kafka.producer")


  //create a cassandra cluster to connect cassandra with this application
  val cluster = Cluster.builder().addContactPoint("localhost").withPort(9042).build()
  implicit val session = cluster.connect("mykeyspace")

  // we have to consume a msg from a topic and then we add those msg to cassandra so we naed a consumer first and for that I defined a consumer setting
  val consumerStringSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("alpakka-template")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  //source that take data from a kafka topic
  val kafkaSource : scaladsl.Source[ConsumerRecord[String, String], Consumer.Control] =
    Consumer.plainSource(consumerStringSettings,Subscriptions.topics("cassandraTopic"))

  val flow : Flow[ConsumerRecord[String,String],String,NotUsed] =
    Flow[ConsumerRecord[String,String]].map(record => record.value().toUpperCase())


  //creating a cassandra sink
  val sink = {
    val statement = session.prepare(s"INSERT INTO mykeyspace.demo(msg) VALUES (?)")
    val statementBinder: (String, PreparedStatement) => BoundStatement = (msg, ps) =>
      ps.bind(msg)
    CassandraSink[String](parallelism = 10, statement = statement, statementBinder = statementBinder)
  }

  kafkaSource
    .via(flow)
    .runWith(sink)

}
