package Broker

import akka.actor.{Actor, ActorSystem}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

class KafkaProducer extends Actor {

  import KafkaProducerClass._

  // define ActorSystem and Materializer for akka streams
  implicit val system = ActorSystem("Kafka")
  implicit val materializer = ActorMaterializer()

  // kafka producer config/settings
  val proConfig = system.settings.config.getConfig("akka.kafka.producer")
  val producerSettings: ProducerSettings[String, String] = ProducerSettings(proConfig, new StringSerializer, new StringSerializer)

  // -- Producer actor receives messages with topic, key, value and then forms ProducerRecord
  def receive: Receive = {
    case Message(topic, key, value) => {
      Source.single(Message(topic, key, value))
        .map(msg => new ProducerRecord[String, String](topic, 0, msg.key, msg.value))
        .runWith(Producer.plainSink(producerSettings))
    }
    case Terminate => {
      system.terminate()
      println("System terminate for Kafka Producer...")
    }
    case _ => println("KafkaProducer received something unexpected... No action taken...")
  }
}

object KafkaProducerClass {

  case class Message(topic: String, key: String, value: String)

  case object Terminate

}