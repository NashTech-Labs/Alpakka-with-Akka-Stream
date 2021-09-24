package com.knoldus

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

object AppToKafka extends App {

  implicit val system: ActorSystem = ActorSystem("AppToKafka")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val configProducer = ConfigFactory.load.getConfig("akka.kafka.producer")


  //creating a producer stream on localhost:9092
  val producerSettings: ProducerSettings[String, String] =
    ProducerSettings(configProducer, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")

  // creating a source that will go through a flow and then produce data to a kafka sink.
  val applicationSource : Source[Int , NotUsed] = Source(List(1, 2, 3))

  val firstFlow : Flow[Int,String,NotUsed] =  Flow[Int].map(
    value => s" The value from application is $value"
  )

  val producerFlow :  Flow[String,ProducerRecord[String, String],NotUsed] = Flow[String].map{
    value => new ProducerRecord[String, String]("appToKafka", "Record : " + value)
  }


//kafka sink
  val kafkaPlainSink: Sink[ProducerRecord[String, String], Future[Done]] = Producer.plainSink(producerSettings)

//combining source, flow and sink.
  applicationSource.via(firstFlow).via(producerFlow).runWith(kafkaPlainSink)

}
