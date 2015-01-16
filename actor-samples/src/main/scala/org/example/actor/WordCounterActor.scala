package org.example.actor

import akka.actor.{Actor, ActorRef, Props}
/**
 * Created by kailianghe on 1/7/15.
 */
case class StartProcessFileMsg()

class WordCounterActor(filename: String) extends Actor {

  private var running = false
  private var totalLines = 0
  private var lineProcessed = 0
  private var result = 0
  private var fileSender : Option[ActorRef] = None

  override def receive = {
    case StartProcessFileMsg() => {
      if(running) {
        println("Warning: duplicate start message received")
      } else {
        running = true
        fileSender = Some(sender)
        import scala.io.Source._
        fromFile(filename).getLines.foreach { line =>
          context.actorOf(Props[StringCounterActor]) ! ProcessStringMsg(line)
          totalLines += 1
        }
      }
    }
    case StringProcessedMsg(words) => {
      result+=words
      lineProcessed += 1
      if(lineProcessed == totalLines) {
        fileSender map {
          println("send result "+result)
          _ ! result
        }
      }
    }
    case _ => println("message not recognized!")
  }
}
