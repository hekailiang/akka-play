package org.example.lifecycle

import akka.actor.{Actor, ActorSystem, DeadLetter, Props}

/**
 * Created by kailianghe on 1/7/15.
 */
// http://rerun.me/2014/10/21/akka-notes-actor-lifecycle-basic/
object LifecycleApp extends App {
  val actorSystem=ActorSystem("LifecycleActorSystem")
  val lifecycleActor=actorSystem.actorOf(Props[BasicLifecycleLoggingActor],"lifecycleActor")

  val deadLetterListener = actorSystem.actorOf(Props[MyCustomDeadLetterListener])
  actorSystem.eventStream.subscribe(deadLetterListener, classOf[DeadLetter])

  lifecycleActor ! "hello"
  lifecycleActor ! "stop"
  lifecycleActor ! "hello"

  //wait for a couple of seconds before shutdown
  Thread.sleep(2000)
  actorSystem.shutdown()
}

class MyCustomDeadLetterListener extends Actor {
  def receive = {
    case deadLetter: DeadLetter => println(s"FROM CUSTOM LISTENER $deadLetter")
  }
}
