package Models

import Broker.KafkaProducerClass.{Message, Terminate}
import akka.actor.{Actor, ActorRef, DeadLetter}

import scala.io.Source
import scala.util.Random

object Generator {

  val charKey = "test"
  val charTopic = "char"

  // random alpha string generator
  val alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  val size = alpha.size
  val randomString: Int => String = n => (1 to n).map(x => alpha(Random.nextInt.abs % size)).mkString

  // method to read dictionary from file
  val readDictionary: String => Array[String] = filename => {
    val bufferedSource = Source.fromFile(filename)
    val lines = (for (line <- bufferedSource.getLines()) yield line).toList
    bufferedSource.close
    lines.toArray
  }

  val dictionary = readDictionary("/home/bw/scala/Kafka/KafkaStreamsWordRecog/src/dictionary-common.txt")
  val dictSize = dictionary.size

  println("Dictionary Size: " +  dictSize)

  // Actor class for data record generator
  class GenData (producer: ActorRef) extends Actor {
    import GenClass._

    def receive: Receive = {

      case StartGenCharacters(numWords,maxRandomString) => {
        val sb: StringBuilder = new StringBuilder
        val randStr1 = randomString(50).toLowerCase
        sb.append(randStr1)
        for(idx <- 0 until numWords) {
          val randStr = randomString(Random.nextInt(maxRandomString)).toLowerCase
          val randWord = dictionary(Random.nextInt(dictSize)).toLowerCase
          sb.append(randStr + randWord)
          println(s"word generated: $randWord")
        }
        val randStr2 = randomString(50).toLowerCase
        sb.append(randStr2)
        println(sb.toString())
        val chars = sb.toString().toCharArray
        chars.foreach(ch => {
          producer ! Message(charTopic, charKey, ch.toString)
        })
      }

      case StartGenDictionary => {
        var ctr = 0
        dictionary.foreach((word) => {
          Trie.add(word)
          ctr += 1
          if (ctr % 5000 == 0) println(s"\n$ctr words added to Trie\n")
        })
        println(s"$ctr words added to Trie")
      }

      case StopGenData => {
        producer ! Terminate
        println("GenData stopped...")
      }

      case d: DeadLetter => {
        println(s"DeadLetterMonitorActor : saw dead letter $d")
      }

      case _ => println("GenData Actor received something unexpected...")
    }
  }

  object GenClass {
    case class StartGenCharacters(numWords: Int, maxRandomString:Int)
    case object StartGenDictionary
    case object StopGenData
  }

}
