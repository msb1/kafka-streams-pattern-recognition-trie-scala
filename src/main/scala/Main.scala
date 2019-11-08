import java.time.Duration

import Broker.KafkaProducer
import Models.Generator.GenClass.{StartGenCharacters, StartGenDictionary}
import Models.Generator.GenData
import akka.actor.{ActorSystem, Props}
import org.apache.kafka.streams.KafkaStreams
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import Streams.WordRecog.{createTopology, props}

object Main {


  def main(args: Array[String]) {

    // set Logger levels to WARN (to avoid excess verbosity)
    LoggerFactory.getLogger("org").asInstanceOf[Logger].setLevel(Level.WARN)
    LoggerFactory.getLogger("akka").asInstanceOf[Logger].setLevel(Level.WARN)
    LoggerFactory.getLogger("kafka").asInstanceOf[Logger].setLevel(Level.WARN)

    println("Kafka Streams Word Recognition Test program...")

    //Create actors for Kafka Producer, character/word simulator
    val system = ActorSystem("WordRecog")
    val kafkaProducer = system.actorOf(Props[KafkaProducer], "kafkaProducer")
    val genData = system.actorOf(Props(new GenData(kafkaProducer)), "genData")

    val numWords = 100 // number of words to stream between between random characters
    val maxRandomString = 25 // max number of random characters between words

    // Generate Card, Verification and User data - send to Kafka Producer
    genData ! StartGenDictionary // start genDictionary
    genData ! StartGenCharacters(numWords, maxRandomString) // start character stream

    // initialize and start Kafka Streams
    val topology = createTopology()
    val streams: KafkaStreams = new KafkaStreams(topology, props)
    streams.start()

    sys.ShutdownHookThread {
      streams.close(Duration.ofSeconds(30))
    }
  }
}