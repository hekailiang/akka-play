package org.example.message

import akka.actor.{ActorRef, Actor, ActorLogging}
import org.example.message.TeacherProtocol.{QuoteResponse, QuoteRequest, InitSignal}
import scala.concurrent.duration._

/**
 * Created by kailianghe on 1/9/15.
 */
class StudentDelayedActor(teacherActorRef:ActorRef) extends Actor with ActorLogging {

  /*
     * This InitSignal is received from the DriverApp.
     * On receipt and after 5 seconds, the Student sends a message to the Teacher actor.
     * The teacher actor on receipt of the QuoteRequest responds with a QuoteResponse
     */
  def receive = {
    case InitSignal => {
      import context.dispatcher
      context.system.scheduler.schedule(0 seconds, 1 seconds, teacherActorRef, QuoteRequest)
    }

    case QuoteResponse(quoteString) => {
      log.info ("Received QuoteResponse from Teacher")
      log.info(s"Printing from Student Actor: '$quoteString'")
    }
  }
}
