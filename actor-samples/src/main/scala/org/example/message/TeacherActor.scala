package org.example.message

import akka.actor.{Actor, ActorLogging}
import org.example.message.TeacherProtocol._

/**
 * Created by kailianghe on 1/8/15.
 */
class TeacherActor extends Actor with ActorLogging {
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest => {
      import scala.util.Random

      //Get a random Quote from the list and construct a response
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))
      log.info(quoteResponse.toString())
    }
  }

  //We'll cover the purpose of this method in the Testing section
  def quoteList = quotes

}
