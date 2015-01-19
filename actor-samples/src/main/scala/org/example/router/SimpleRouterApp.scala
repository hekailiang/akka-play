package org.example.router

import akka.actor._
import akka.routing.{FromConfig, RoundRobinRoutingLogic, Router, ActorRefRoutee}
import com.typesafe.config.ConfigFactory

/**
 * Created by kailianghe on 1/19/15.
 */
object SimpleRouterApp extends App {
  val sys = ActorSystem("simpleRouterApp", ConfigFactory.load("SimpleRouterApp.conf")
    .withFallback(ConfigFactory.load())
  )
  val master = sys.actorOf(Props[Master], "master")
  for(i <- 0 to 9) {
    /**
     * 0->a, 1->b, 2->c, 3->d, 4->e, 5->a, 6->b, ...
     */
    master ! Work(s"Master Naive Work $i")
  }

  val router1: ActorRef =
    sys.actorOf(FromConfig.props(Props[Worker]), "router1")
  for(i <- 0 to 9) {
    router1 ! Work(s"Router1 Naive Work $i")
  }

  Thread sleep 1000
  sys.shutdown()
}

class Master extends Actor {
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Worker])
      context watch r
      ActorRefRoutee(r)
    }
    // The Router is immutable and the RoutingLogic is thread safe; meaning that they can also be used outside of actors.
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case w: Work =>
      router.route(w, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
  }
}


class Worker extends Actor {
  def receive = {
    case w : Work =>
      println(s"Job '${w.job}' received at ${self.path}")
  }
}

case class Work(job: String)