package org.example.actor

import akka.actor.{ActorSystem, Props}

/**
 * Created by kailianghe on 1/7/15.
 */
// http://www.toptal.com/scala/concurrency-and-fault-tolerance-made-easy-an-intro-to-akka
object Sample extends App {

  import akka.dispatch.ExecutionContexts._
  import akka.pattern.ask

import scala.concurrent.duration._

  override def main(args: Array[String]) {

    import akka.util.Timeout

    val system = ActorSystem("System")
    val actor = system.actorOf( Props(classOf[WordCounterActor], args(0)), "WordCounterActor" )

    implicit val timeout = Timeout(25 seconds)
    val future = actor ? StartProcessFileMsg()

    implicit val ec = global
    future map { result =>
      println("Total number of words " + result)
      system.shutdown
    }
  }

}
