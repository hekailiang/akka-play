package org.example.backpressure

import akka.actor.{ActorLogging, Actor}

/**
 * Created by kailianghe on 1/12/15.
 */
class SlowReceiver extends Actor with ActorLogging {

  override def postStop() {
    log.info("SlowReceiver#postStop")
  }

  def receive: Actor.Receive = {
    case msg: String =>
      log.info(s"Received: $msg")
      Thread.sleep(100) // simulate slow message processing
  }

}
