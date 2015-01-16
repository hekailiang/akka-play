package org.example.actor

/**
 * Created by kailianghe on 1/7/15.
 */
case class ProcessStringMsg(string: String)
case class StringProcessedMsg(words: Integer)

import akka.actor.Actor

class StringCounterActor extends Actor {
  override def receive = {
    case ProcessStringMsg(string) => {
      val wordsInLine = string.split(" ").length
      sender ! StringProcessedMsg(wordsInLine)
    }
    case _ => println("Error: message not recognized")
  }
}
