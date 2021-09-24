package com.knoldus

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, scaladsl}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

object KafkaToKafka extends App {

  implicit val system: ActorSystem = ActorSystem("KafkaToKafka")
  implicit  val materializer: ActorMaterializer = ActorMaterializer()


  //we are using both consumer and producer here so we have to provide both setting.
  //consumer setting
  val consumerStringSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("alpakka-template")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")


  //producer setting
  val producerSettings: ProducerSettings[String, String] =
    ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")


  //source that take data from a kafka topic
  val kafkaSource : scaladsl.Source[ConsumerRecord[String, String], Consumer.Control] =
    Consumer.plainSource(consumerStringSettings,Subscriptions.topics("input"))

  //flow
  val flow : Flow[ConsumerRecord[String,String],String,NotUsed] =
    Flow[ConsumerRecord[String,String]].map{
      record =>  s" Converting msg and returning in Upper Case ----------->>  ${record.value().toUpperCase()}"
    }

  //secondFlow
val producerFlow : Flow[String,ProducerRecord[String,String],NotUsed] = Flow[String].map(
  record =>new ProducerRecord[String,String]("output", record)
)


  //A kafka sink for output
  val kafkaPlainSink: Sink[ProducerRecord[String, String], Future[Done]] = Producer.plainSink(producerSettings)

  //combining source , flow and sink.
  kafkaSource.via(flow).via(producerFlow).runWith(kafkaPlainSink)
}
