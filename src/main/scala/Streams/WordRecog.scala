package Streams

import java.util.Properties

import Models.Trie
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{StreamsConfig, Topology}


object WordRecog {

  val charTopic = "char"

  // Load properties
  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "barnwaldo") // group.Id for Kafka Streams
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.21.10:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000.asInstanceOf[Object])


  def createTopology(): Topology = {

    val builder: StreamsBuilder = new StreamsBuilder

    // KStream for chunks of characters - check Trie with each character read
    builder.stream[String, String](charTopic)(Consumed.`with`(Serdes.String, Serdes.String))
      .flatMapValues(value => value.toList)
      .map((key, value) => {
          val foundWords: String = Trie.findWord(value)
          (key, foundWords)
      })
      // print entry if word(s) found in dictionary
      .foreach((key, value) => {
        if (value != "") println(s"words found: $value")
      })
      // uncomment this block to test consumed character stream
      // .foreach((key, value) => {
      //   print(value)
      // })

    builder.build()
  }

}
