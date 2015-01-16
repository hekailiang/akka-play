package org.example.backpressure

import akka.actor._

/**
 * Created by kailianghe on 1/12/15.
 */
class Watcher(target: ActorRef) extends Actor with ActorLogging  {
  context.watch(target)

  def receive: Actor.Receive = {
    case d: DeadLetter =>
      if(d.recipient.path.equals(target.path)) {
        log.info(s"Timed out message: ${d.message.toString}")
      }

    case Terminated(`target`) =>
      context.system.shutdown()
  }
}
