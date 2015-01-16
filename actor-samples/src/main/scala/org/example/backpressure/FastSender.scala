package org.example.backpressure

import akka.actor.{PoisonPill, ActorLogging, Actor, ActorRef}
import scala.concurrent.duration._

/**
 * Created by kailianghe on 1/12/15.
 */
class FastSender (slow: ActorRef) extends Actor with ActorLogging {

  override def postStop() {
    log.info("FastSender#postStop")
    context.system.scheduler.scheduleOnce(2 seconds, slow, PoisonPill)(context.dispatcher)
  }

  def receive = {
    case _ =>
      for(i <- 1 to 15) {
        slow ! s"[$i]"
      }
      log.info("Done sending all")
      self ! PoisonPill
  }

}
